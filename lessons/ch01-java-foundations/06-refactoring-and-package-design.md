# Lesson 6 — Refactoring and Package Design

**Goal:** finish the split you started early, put presentation decisions in the presentation layer, and keep the public API intentionally small.

**Prerequisite:** Lesson 5's injected I/O and validation rules.

**Progress**

- [x] Finish the CLI split and retire the `util` drawer
- [x] Move presentation labels out of the model
- [x] Introduce request records and service operations
- [x] Reduce visibility and keep Main as the composition root
- [ ] Complete the self-check

---

## Initialization

Start from the end of Lesson 5, commit `06b6ca0`. **Most of this lesson's original Step 1 already happened during Lessons 4 and 5.** You split the console code into the `cli` package while you were still implementing prompts, so this lesson audits and finishes that split instead of performing it.

What already exists:

```text
src/main/java/com/connorjensen/jobtracker/
├── Main.java                         composition root; builds the whole graph and calls run()
├── cli/
│   ├── ConsoleApplication.java       menu loop and one method per operation
│   ├── ConsolePrompter.java          twelve prompt methods, each with its own retry loop
│   ├── ConsoleView.java              header, menu, table, details, result and error messages
│   └── TextTable.java                render(header, rows) -> String
├── dto/ApplicationDto.java           Lesson 1's record-versus-class demo
├── model/
│   ├── Application.java              mutable entity, plus toValuesList() / toLabelsList()
│   ├── ApplicationDetails.java       your enum of labels and value extractors
│   └── Status.java
├── repository/                       contract plus in-memory implementation
├── service/ApplicationService.java   create x2, listAll, findById, listByStatus, updateStatus, delete
└── util/Centering.java               the last survivor of the old util package
```

Four things are unfinished, and each one is a step below:

- `util/Centering.java` still exists and `TextTable` still imports it.
- `model/ApplicationDetails.java` decides which columns a table has, so the model knows about presentation.
- `service` has no request records, so `ApplicationService` still takes loose scalars.
- Every method on `ConsolePrompter` and one on `ConsoleView` is `public`.

You also installed the Chapter 1 capstone grader early, at `src/test/java/com/connorjensen/jobtracker/capstone/Chapter01CapstoneTest.java`. It references the request records that do not exist yet, so **no test in the project can run right now** — including `ApplicationTest`.

- [ ] Confirm the starting state before you change anything.

```bash
mvn -q -Dcheckstyle.skip test 2>&1 | rg -o 'cannot find symbol|class (Create|Update)ApplicationRequest' | sort | uniq -c
```

```text
  28 cannot find symbol
  12 class CreateApplicationRequest
  16 class UpdateApplicationRequest
```

Step 3 is what turns the test suite back on. Until then, use `mvn -q -Dcheckstyle.skip -DskipTests compile` for feedback.

## Step 1 — Finish the CLI split and retire the `util` drawer

`util` becomes a drawer for code whose responsibility has not been named. A package name should help predict why its classes change.

The split you already made follows cohesion, and it is the right one:

| Class              | Reason to change                    |
|--------------------|-------------------------------------|
| ConsoleApplication | Menu workflow changes               |
| ConsolePrompter    | Input and validation policy changes |
| ConsoleView        | User-facing text changes            |
| TextTable          | Rendering algorithm changes         |

`Centering` does not get a row in that table, and that is the tell. Centering a string inside a column is not a general-purpose utility that many packages need — it is one rule inside the rendering algorithm, and it changes for exactly the same reason `TextTable` changes. A `public static` method in `util` also invites any future class to depend on it, which quietly widens the surface you have to keep working.

- [x] Move `Centering.center(...)` into `TextTable` as a `private static` helper.
- [x] Delete `src/main/java/com/connorjensen/jobtracker/util/Centering.java` and the now-empty `util` package.
- [x] Remove the `import com.connorjensen.jobtracker.util.Centering;` line from `TextTable`.

### Verify

- [x] Confirm no production code mentions the old CLI utilities, then compile.

```bash
if rg -n 'ConsoleExperience|ApplicationTextTable|Centering' src/main/java; then exit 1; else echo "old CLI utilities: removed"; fi && mvn -q -Dcheckstyle.skip -DskipTests compile && echo "compile: ok"
```

```text
old CLI utilities: removed
compile: ok
```

Before the move the same command prints five matches and exits `1`; `TextTable.java:6`, `:25`, `:42` are the callers you have to satisfy first.

## Step 2 — Move presentation labels out of the model

`ApplicationDetails` was a good instinct: one place that pairs each field with a label and a way to read it, instead of six handwritten lines in two different methods. The problem is where it lives.

Today `ApplicationDetails.getAllLabels()` filters out `NOTES` — so a type in `model` is deciding that the *list table* has six columns while the *detail view* has seven. That is a presentation decision, and it changes whenever the console's layout changes, not when a job application changes. It also means `getAllLabels()` no longer means "all labels", which is how the name and the body drifted apart.

Ask the ownership question: if another adapter replaces the console, does this code follow the model or follow the view? Column order, column names, and "notes is too wide for a table" all follow the view.

- [x] Give `ConsoleView` the six column names and a private method that turns one `Application` into one row.
- [x] Give `ConsoleView` the detail-view lines directly, so the two layouts stop sharing one enum.
- [x] Delete `Application.toLabelsList()` and `Application.toValuesList()`.
- [x] Delete `model/ApplicationDetails.java`.

The column contract is fixed by [CAPSTONE.md](CAPSTONE.md) — exactly `ID`, `Company`, `Role`, `Applied Date`, `Status`, `URL`, with the status rendered as the enum name. Two spellings in your current code do not match it: the header reads `Applied date`, and both the table and the detail view print `Status.getLabel()` (`Applied`) where the contract wants `Status.name()` (`APPLIED`). `getLabel()` keeps earning its place on `Status` for a future UI; the console is simply not that UI.

The shape `ConsoleView` needs, without the bodies:

```java
// src/main/java/com/connorjensen/jobtracker/cli/ConsoleView.java — edit excerpt
public class ConsoleView {
  private static final List<String> COLUMNS =
      List.of("ID", "Company", "Role", "Applied Date", "Status", "URL");

  // ... existing fields and constructor ...

  private List<String> toRow(Application application) {
    // six values, in COLUMNS order, status as application.getStatus().name()
    throw new UnsupportedOperationException("TODO");
  }
}
```

`TextTable.render(...)` currently adds padding cells to the list it is handed, so pass it a fresh `new ArrayList<>(COLUMNS)` rather than the shared constant. Lesson 7 makes the renderer stop doing that.

### Verify

- [x] Compile, then create one application and list it.

```bash
mvn -q -Dcheckstyle.skip -DskipTests compile &&
printf '0\nAcme\nEngineer\n2026-08-01\nreferral\nhttps://example.com/jobs/1\n1\n5\n' |
  java -cp target/classes com.connorjensen.jobtracker.Main | rg -e '^\|'
```

```text
| ID | Company |   Role   | Applied Date | Status  |            URL             |
| 1  |  Acme   | Engineer |  2026-08-01  | APPLIED | https://example.com/jobs/1 |
```

If the header still reads `Applied date` or the status column reads `Applied`, `ConsoleView` is still going through the old model helpers.

## Step 3 — Introduce request records and service operations

A service method with six scalar parameters is easy to call in the wrong order and painful to extend. A request record <sup>[J16](NOTES.md#records)</sup> gives the operation one named, typed input.

- [x] Add `CreateApplicationRequest` to the service package.
- [x] Add `UpdateApplicationRequest` to the service package.
- [x] Add `create(CreateApplicationRequest)` and `update(Long, UpdateApplicationRequest)` to `ApplicationService`.
- [x] Make `update(...)` throw `IllegalArgumentException` for an unknown id, the same way `updateStatus` already does.
- [x] Keep the existing `create` overloads and `updateStatus`; the grader asserts the old operations still work.

```java
// src/main/java/com/connorjensen/jobtracker/service/CreateApplicationRequest.java
package com.connorjensen.jobtracker.service;

import java.time.LocalDate;

public record CreateApplicationRequest(
    String company, String role, LocalDate appliedDate, String notes, String jobUrl) {}
```

`UpdateApplicationRequest` carries the same five components plus `Status`, because create defaults the status and update replaces it.

The record is an immutable *instruction*. `Application` stays the mutable domain object: `update(...)` loads the entity, copies all six values onto it, and saves it. If you find yourself wanting to change a field on the request, that mutation belongs on the loaded `Application` instead.

Note the seam: `IllegalArgumentException` for a missing ID could become `ApplicationNotFoundException` and map to an HTTP 404 at a web boundary. Keeping the throw here makes that change local to the service/boundary contract.

### Verify

- [x] Run the service group of the grader. This is also the moment the test suite compiles again.

```bash
mvn -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$ServiceTests' test 2>&1 | rg 'Tests run:.*Skipped|BUILD'
```

```text
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.039 s -- in com.connorjensen.jobtracker.capstone.Chapter01CapstoneTest$ServiceTests
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

If `requestRecordsHaveThePublishedComponentsAndValueEquality` fails, check the component order — a record's equality and accessor names come from the header, so the order is part of the contract.

## Step 4 — Reduce visibility and keep Main as the composition root

`public` is a promise to every caller in every package. Package-private helpers stay flexible and stop tests from freezing incidental method names.

`ConsoleApplication`, `ConsolePrompter`, `ConsoleView`, and `TextTable` all live in `com.connorjensen.jobtracker.cli`, so they can call each other's package-private methods freely. Nothing outside that package ever calls a prompt method or a `show...` method — only `Main` reaches in, and only for the constructors and `run()`.

- [x] Keep only the four constructors, `ConsoleApplication.run()`, and `TextTable.render(...)` public.
- [x] Drop `public` from all twelve `ConsolePrompter` prompt methods.
- [x] Drop `public` from `ConsoleView.showNoApplicationEntries(...)`.
- [x] Declare `Main` `final`; it already has the private constructor that stops instantiation.
- [x] Confirm `Main.main` builds repository, service, scanner, table, view, prompter, and application, then calls `run()` and nothing else.

Your `Main` already matches the intended dependency direction:

```text
Main → cli → service → repository contract → model
                    ↘ model
repository implementation → repository contract + model
```

This is a course architecture convention, not a restriction of the Java compiler. The compiler would happily let `ConsoleView` call the repository; the convention is what keeps a presentation-adapter swap cheap.

### Verify

- [x] Run the architecture group.

```bash
mvn -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$ArchitectureTests' test 2>&1 | rg 'Tests run:.*Skipped|BUILD'
```

```text
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.029 s -- in com.connorjensen.jobtracker.capstone.Chapter01CapstoneTest$ArchitectureTests
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

`cliPublicApiIsMinimal` names every method it did not expect, so a failure reads as a to-do list; `oldUtilityCliTypesAreGone` fails if Step 1 left `Centering` behind.

## Self-check

1. Why did `Centering` belong in `TextTable` rather than in `util`?
2. What went wrong when `getAllLabels()` started skipping `NOTES`?
3. What does a request record improve over six scalar parameters?
4. Why keep the older `create` overloads and `updateStatus`?
5. Why can `ConsolePrompter`'s methods be package-private when `ConsoleApplication` calls all of them?

<details>
<summary>Answers</summary>

1. It changes for the same reason the renderer changes, and `util` hid that by naming a package after nothing.
2. A model type started deciding a console layout, and the method's name stopped describing its body.
3. It names every value, prevents ordering mistakes, and lets the operation gain a field without changing every caller's signature.
4. They still express focused operations, earlier lessons use them, and the grader asserts the old contract survives the refactor.
5. They are in the same package, and package-private is the widest visibility that still covers every real caller.

</details>

## Where this lands you

The architecture has explicit seams, a small surface, and no leftover utility drawer. Lesson 7 uses those seams to test behavior without leaking global state — and the first thing it finds is that `TextTable` is not as pure as it looks.

**Previous:** [Lesson 5 — Console I/O and Validation](05-console-io-and-validation.md) · **Next:** [Lesson 7 — Testing Console Applications](07-testing-console-applications.md) · **Chapter:** [README](README.md)
