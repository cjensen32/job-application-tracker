# Lesson 5 — Console I/O and Validation

**Goal:** turn console text into trusted Java values with injected streams and consistent local retries.

**Prerequisite:** Lesson 4's dispatch and EOF boundaries.

**Progress**

- [x] Inject input and output instead of owning global streams
- [ ] Define required, date, status, ID, and URL validation
- [ ] Make text and charset behavior deterministic
- [ ] Complete the self-check

---

## Initialization

Start from the completed Lesson 4 state. These shells show one valid destination for the methods you already wrote without completing their Lesson 5 behavior. The helper names are suggestions, but the responsibility boundaries are the goal.

Think of each menu operation as three handoffs:

```text
ConsoleApplication → ConsolePrompter → ConsoleApplication → ApplicationService
ConsoleApplication → ConsoleView → TextTable when table formatting is needed
```

Do not copy an entire mixed `Main` method into one new class. Keep the operation method in `ConsoleApplication`, then move only its reading and validation sections to `ConsolePrompter` and only its printing sections to `ConsoleView`.

- [x] Create `src/main/java/com/connorjensen/jobtracker/cli/ConsolePrompter.java` with its injected input and output dependencies.

```java
package com.connorjensen.jobtracker.cli;

import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Scanner;

import com.connorjensen.jobtracker.model.Status;

public class ConsolePrompter {
  private final Scanner promptScanner;
  private final PrintStream promptStream;

  public ConsolePrompter(Scanner promptScanner, PrintStream promptStream) {
    this.promptScanner = promptScanner;
    this.promptStream = promptStream;
  }

  Optional<Integer> promptMenuSelection() {
    throw new UnsupportedOperationException("TODO");
  }

  Optional<String> promptRequiredText(String label) {
    throw new UnsupportedOperationException("TODO");
  }

  // Follow the same read, validate, and local-retry shape for:
  // promptDate(String label) -> Optional<LocalDate>
  // promptStatus(String label) -> Optional<Status>
  // promptPositiveId(String label) -> Optional<Long>
  // promptOptionalText(String label) -> Optional<String>
  // promptUrl(String label) -> Optional<String>
}
```

`ConsolePrompter` owns reads, parsing, normalization, and retry loops. It does not call `ApplicationService`, render tables, or decide which menu operation runs. `Optional.empty()` is one possible explicit EOF signal; `Optional.of("")` can still represent a real blank line.

- [x] Create `src/main/java/com/connorjensen/jobtracker/cli/ConsoleView.java` with its injected output and table dependencies.

```java
package com.connorjensen.jobtracker.cli;

import java.io.PrintStream;
import java.util.List;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;

public class ConsoleView {
  private final PrintStream consoleStream;
  private final TextTable textTable;

  public ConsoleView(PrintStream consoleStream, TextTable textTable) {
    this.consoleStream = consoleStream;
    this.textTable = textTable;
  }

  void showMenu() {}

  void showApplications(List<Application> applications) {}

  void showNoApplicationsForStatus(Status status) {}

  void showGoodbye() {}

  // Use the same output-only shape for created, missing, updated, and deleted messages.
}
```

`ConsoleView` owns user-facing text and converts applications into table rows before calling `textTable.render(...)`. It does not read input, call the service, or choose the next operation.

- [x] Replace `src/main/java/com/connorjensen/jobtracker/cli/TextTable.java` with the pure table's starting shell.

```java
package com.connorjensen.jobtracker.cli;

import java.util.List;

public class TextTable {
  public TextTable() {}

  public String render(List<String> header, List<List<String>> rows) {
    // toTable() -> render(): return text so ConsoleView owns printing.
    throw new UnsupportedOperationException("TODO");
  }

  // Keep width, border, cell-normalization, and centering helpers private.
}
```

- [x] Create `src/main/java/com/connorjensen/jobtracker/cli/ConsoleApplication.java` with the dependencies needed to coordinate the session. `run()` stays empty here because its workflow is part of the assignment.

```java
package com.connorjensen.jobtracker.cli;

import com.connorjensen.jobtracker.service.ApplicationService;

public class ConsoleApplication {
  private final ApplicationService service;
  private final ConsolePrompter prompter;
  private final ConsoleView view;

  public ConsoleApplication(
      ApplicationService service, ConsolePrompter prompter, ConsoleView view) {
    this.service = service;
    this.prompter = prompter;
    this.view = view;
  }

  public void run() {}

  private boolean dispatch(int selection) {
    throw new UnsupportedOperationException("TODO");
  }

  private boolean createApplication() {
    // Ask the prompter for validated values, call service.create(...), then tell the view.
    throw new UnsupportedOperationException("TODO");
  }

  private boolean listApplications() {
    throw new UnsupportedOperationException("TODO");
  }

  private boolean filterApplications() {
    throw new UnsupportedOperationException("TODO");
  }

  private boolean editApplication() {
    throw new UnsupportedOperationException("TODO");
  }

  private boolean deleteApplication() {
    throw new UnsupportedOperationException("TODO");
  }
}
```

The familiar operation names stay in `ConsoleApplication` because they coordinate other objects. They no longer need `Scanner` or `ApplicationService` parameters because those collaborators are already available through the injected fields.

- [x] Replace `src/main/java/com/connorjensen/jobtracker/Main.java` with the composition-root shape below.

```java
package com.connorjensen.jobtracker;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import com.connorjensen.jobtracker.cli.ConsoleApplication;
import com.connorjensen.jobtracker.cli.ConsolePrompter;
import com.connorjensen.jobtracker.cli.ConsoleView;
import com.connorjensen.jobtracker.cli.TextTable;
import com.connorjensen.jobtracker.repository.ApplicationRepository;
import com.connorjensen.jobtracker.repository.InMemoryApplicationRepository;
import com.connorjensen.jobtracker.service.ApplicationService;

public final class Main {
  private Main() {}

  static void main(String[] args) {
    ApplicationRepository repository = new InMemoryApplicationRepository();
    ApplicationService service = new ApplicationService(repository);
    Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
    TextTable table = new TextTable();
    ConsoleView view = new ConsoleView(System.out, table);
    ConsolePrompter prompter = new ConsolePrompter(scanner, System.out);
    ConsoleApplication application = new ConsoleApplication(service, prompter, view);

    // run(scanner, service) -> ConsoleApplication.run()
    application.run();
  }
}
```

`Main` does not call prompt or view methods directly. It constructs the object graph, hands the collaborators to `ConsoleApplication`, and starts the session.

Use this conversion map when moving the completed Lesson 4 behavior. Suggested helper names make the destination concrete without requiring those exact private names:

| Existing `Main` method or section                       | Suggested destination                                                                                         |
|---------------------------------------------------------|---------------------------------------------------------------------------------------------------------------|
| `run(...)` session loop                                 | `ConsoleApplication.run()`; ask `view.showMenu()`, then `prompter.promptMenuSelection()`, then dispatch       |
| `input(...)` parsing and range retry                    | `ConsolePrompter.promptMenuSelection()`                                                                       |
| `menu()` printing                                       | `ConsoleView.showMenu()`                                                                                      |
| `dispatch(...)` switch                                  | `ConsoleApplication.dispatch(int)`                                                                            |
| `createApplication(...)` field reads                    | `ConsolePrompter.promptRequiredText(...)`, `promptDate(...)`, `promptOptionalText(...)`, and `promptUrl(...)` |
| `createApplication(...)` service call                   | `ConsoleApplication.createApplication()`                                                                      |
| Created message                                         | A small `ConsoleView` output method such as `showCreated(...)`                                                |
| `listApplications(...)` service call                    | `ConsoleApplication.listApplications()`                                                                       |
| Empty-list decision, row conversion, and table printing | `ConsoleView.showApplications(...)`, using `textTable.render(...)`                                            |
| `filterApplications(...)` status read and normalization | `ConsolePrompter.promptStatus(...)`                                                                           |
| `filterApplications(...)` service call                  | `ConsoleApplication.filterApplications()`                                                                     |

| Filtered rows or no-match message | `ConsoleView.showApplications(...)` or `showNoApplicationsForStatus(...)` |
| `editApplication(...)` and `deleteApplication(...)` operation coordination | Keep these methods in `ConsoleApplication`; send ID and field reads to `ConsolePrompter` as you implement them |
| `exit()` printing | `ConsoleView.showGoodbye()`; `ConsoleApplication.run()` decides when to call it |

For example, `createApplication()` does not move intact. Its prompts become calls to `ConsolePrompter`, its `service.create(...)` call stays in `ConsoleApplication`, and its success text becomes a `ConsoleView` call. Apply that same split to filter, edit, and delete.

## Step 1 — Inject input and output

`System.in` and `System.out` are process-wide mutable state. They are appropriate dependencies for `Main` to choose, but not for every CLI class to reach for directly.

- [x] Use `ConsolePrompter`'s `promptScanner` and `promptStream` for input behavior instead of reaching for global streams.
- [x] Use `ConsoleView`'s `consoleStream` and `textTable` for display behavior instead of reaching for global output.
- [x] Keep the injected collaborators in private final fields so every instance has a complete dependency set after construction.

`Main` remains the only place that chooses the real process streams. Tests can choose byte arrays without replacing global state.

Use line-oriented reads for every prompt. Mixing `nextInt()` with `nextLine()` leaves newline characters behind and creates confusing skipped prompts.

### Verify

- [x] Compile the new constructor graph.

```bash
mvn -q -Dcheckstyle.skip -DskipTests compile && echo "streams: injected"
```

```text
streams: injected
```

If a test still calls `System.setOut`, first check whether output can be passed through a constructor instead.

## Step 2 — Define one rule per input type

- [x] Trim company and role, reject blank values, and retry the same field.
- [x] Parse dates with `LocalDate.parse`, which accepts ISO `YYYY-MM-DD` input.
- [x] Parse IDs with `Long.parseLong`, then reject zero and negative values.
- [x] Normalize status text with trim, uppercase, and replacement of spaces or hyphens by underscores.
- [x] Accept a blank URL or require an absolute URI whose scheme is `http` or `https`, case-insensitively.

Status examples:

| Input          | Normalized     |
|----------------|----------------|
| `offer`        | `OFFER`        |
| `phone screen` | `PHONE_SCREEN` |
| `phone-screen` | `PHONE_SCREEN` |

URI parsing answers only whether text has URI syntax. The application adds policy: a job URL must be absolute and use HTTP(S).

Edit mode adds context-sensitive rules:

- Blank keeps every current value.
- `-` clears notes or URL.
- Required fields cannot be cleared.
- A changed value still passes the same validator used during create.

### Verify

- [x] Run the focused capstone validation group after installing the grader.

```bash
mvn -q -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$ConsoleValidationTests#invalidInputRetriesLocally' test && echo "validation: ok"
```

```text
validation: ok
```

If the entire form restarts, the retry loop is outside the individual prompt boundary.

## Step 3 — Make text and charset behavior deterministic

Text blocks keep stable multiline messages readable. They do not remove whitespace from the contract: indentation and the final newline remain observable.

- [x] Use `StandardCharsets.UTF_8` whenever tests convert bytes to strings or create byte-backed streams.
- [x] Keep prompts in `ConsolePrompter` and display blocks in `ConsoleView`.
- [x] Keep `TextTable` pure by returning a string instead of printing it.

The six list columns are fixed:

```text
ID, Company, Role, Applied Date, Status, URL
```

Notes stay out of the concise table and appear in the edit/detail view.

### Verify

- [ ] Run a UTF-8 create-and-list session after the capstone refactor.

```bash
printf '0\nCafé Labs\nDéveloppeur\n2026-08-17\n\nhttps://example.com/jobs/1\n1\n5\n' | java -cp target/classes com.connorjensen.jobtracker.Main
```

```text
Created application 1.
```

The full output contains menus and the table; if `Café` or `Développeur` is corrupted, find the byte/string conversion that omitted UTF-8.

## Asked Questions

> Questions came up while working through this lesson. “Resolved” means an explanation or design decision exists;

### Did the initial `ConsolePrompter` and `ConsoleView` setup match the lesson?

**Resolution:** `ConsolePrompter` must receive both `Scanner` and `PrintStream`:
- `ConsoleView` must receive `PrintStream` and `TextTable`. Injected collaborators belong in `private final` fields. 
- A later review confirmed the prompter and view constructor wiring was correct, although `ConsoleApplication` still needed `final` collaborator fields.

### Why use separate `ConsolePrompter` and `ConsoleView` classes?

**Resolution:** They handle opposite directions of the console boundary. `ConsolePrompter` converts text into validated Java values; `ConsoleView` converts Java values into displayed text. This keeps validation and presentation independently changeable and testable. `ConsoleApplication` coordinates them.

### Where do the methods previously stored in `Main` belong?

**Resolution:**

- `Main` constructs the object graph and calls `ConsoleApplication.run()`.
- `ConsoleApplication` owns the loop, dispatch, service calls, and workflow decisions.
- `ConsolePrompter` owns reading, parsing, validation, retry loops, and EOF signalling.
- `ConsoleView` owns menus, messages, details, and displayed tables.
- `TextTable` only converts table data into a string.

Mixed methods should be split by responsibility rather than moved intact.

### How much code should the lesson’s Initialization section provide?

**Resolution:** Initialization should provide repository-relative paths, compilable class shells, constructors, essential method signatures, caller wiring, and a method-migration map. `void` methods remain empty; unfinished non-void methods may throw `UnsupportedOperationException("TODO")`. Repetitive methods can be named in comments. Validation and workflow implementation remain the assignment.

### Why does `new TextTable()` produce “Instantiation of utility class”?

**Resolution:** The warning appears when `TextTable` still exposes static behavior, making an object unnecessary. The Chapter 1 destination uses an injected `TextTable` instance whose `render(...)` and helper methods are non-static. Completing that refactor removes the warning. Making the constructor private would preserve the wrong utility-class shape.

### Why do several prompt methods accept a parameter named `label`?

**Resolution:** `label` is reusable prompt text, such as `"Company: "` or `"Role: "`. It was a design suggestion, not a consequence of the learner’s notes. `promptText` may be clearer, and a method with one permanently fixed prompt may need no parameter.

### Is the retry loop inside `promptMenuSelection()` too much responsibility?

**Resolution:** No. Its single responsibility is to keep reading until it obtains one valid menu selection or reaches EOF. It should not display the full menu or dispatch the selection. `ConsoleApplication` coordinates those responsibilities.

### How should menu EOF be handled?

**Resolution:** Check `hasNextLine()` before calling `nextLine()`. EOF should return `Optional.empty()`, while a real quit selection returns `Optional.of(5)`. Converting EOF into a fake `"5"` loses information. `ConsoleApplication` should interpret the empty result as a clean end to the session.

### What unresolved problems did the implementation review find?

**Status:** Open as of the Claude review; no later local chat proves these were fixed and re-verified.

- Menu EOF caused an exception instead of a clean exit.
- Required company and role text was not trimmed, rejected when blank, or retried.
- URL validation accepted relative values such as `notes`; nonblank URLs must be absolute HTTP or HTTPS URIs.
- Status normalization handled spaces but not hyphens such as `phone-screen`.
- Successful creation did not print `Created application N.`
- An empty filtered result used the generic “No applications to list” message instead of a status-specific message.
- Two `ConsoleView` paths used `System.out` instead of the injected `PrintStream`.

Additional advisories covered `final` collaborator fields, private dispatch, prompt-before-read ordering, retry prompts, private `TextTable` helpers, and avoiding mutation of lists passed into `render(...)`.

## Self-check

1. Why inject a `Scanner` instead of constructing it inside the prompter?
2. Why is `URI.create("notes")` not enough to validate a job URL?
3. Why do status values normalize spaces and hyphens?
4. Why does `-` have meaning only for optional edit fields?

<details>
<summary>Answers</summary>

1. Injection makes input deterministic and avoids replacing process-wide state in tests.
2. It is a syntactically valid relative URI, but the contract requires an absolute HTTP(S) URI.
3. Humans commonly type labels in those forms while the enum uses underscores.
4. Required fields cannot be cleared; optional fields need a value distinct from blank, which already means keep current.

</details>

## Where this lands you

The boundary now produces typed, validated values. Lesson 6 decides which class owns each part and how packages may depend on one another.

**Previous:** [Lesson 4 — Control Flow and Exception Boundaries](04-control-flow-and-exception-boundaries.md) · **Next:** [Lesson 6 — Refactoring and Package Design](06-refactoring-and-package-design.md) · **Chapter:** [README](README.md)
