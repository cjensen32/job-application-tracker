# Chapter 1 Capstone — The Tracker Core

Build a complete plain-Java CRUD console tracker. The console and in-memory store are temporary adapters; the model, repository contract, service boundary, request records, and dependency direction survive into later chapters.

This capstone is intentionally contract-first and challenging. Choose your own private helper names and implementation order, but match every public type, signature, behavior, and stable output below.

**Progress**

- [x] Install the synchronized capstone grader
- [x] Preserve the model and repository contract
- [x] Add request records and full service updates
- [x] Refactor the CLI into four focused classes
- [x] Implement create, list, filter, edit, delete, quit, and EOF behavior
- [x] Pass table, validation, session, API-shape, and composition tests
- [x] Pass every completion command
- [x] Complete the chapter quiz

Every marker below owns one `##` section, and every section ends with a `### Verify` command that passes only when that marker is genuinely done. The commands are cumulative: a later one does not re-prove an earlier one, so a green marker stays green only if you keep re-running it.

| Progress marker                                                      | Verify command                                                                                                       | Green looks like |
|----------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|------------------|
| Install the synchronized capstone grader                             | `mvn -Dcheckstyle.skip -Dmaven.test.failure.ignore=true -Dtest=Chapter01CapstoneTest test`                           | `Tests run: 39`  |
| Preserve the model and repository contract                           | `mvn -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$ExistingContractTests' test`                                    | 5 run, 0 failed  |
| Add request records and full service updates                         | `mvn -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$ServiceTests' test`                                             | 6 run, 0 failed  |
| Refactor the CLI into four focused classes                           | `mvn -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$ArchitectureTests' test`                                        | 3 run, 0 failed  |
| Implement create, list, filter, edit, delete, quit, and EOF behavior | `mvn -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$ConsoleReadTests,Chapter01CapstoneTest$ConsoleWriteTests' test` | 10 run, 0 failed |
| Pass table, validation, session, API-shape, and composition tests    | `mvn -Dcheckstyle.skip -Dtest=Chapter01CapstoneTest,ConsoleSessionTest,MainProcessTest test`                         | 43 run, 0 failed |
| Pass every completion command                                        | `mvn verify`                                                                                                         | `BUILD SUCCESS`  |
| Complete the chapter quiz                                            | `/code-sensei:quiz`                                                                                                  | —                |

## Constraints

- Use Java 21 and the standard library for production code.
- Do not add Spring, a database, or a runtime dependency.
- Keep the configured build plugins as they are; Checkstyle and Spotless are part of the gate, not settings to swap or disable.
- Follow the [course standards](../ch00-course-standards/README.md) and `config/checkstyle/checkstyle.xml`.
- Treat `Main` as the composition root only.
- Keep public CLI API limited to the exact constructors and methods listed below.
- Keep helper names private or package-private; the grader does not prescribe them.

## Install the synchronized capstone grader

- [x] Copy the hidden grader into the normal test tree.

```bash
mkdir -p src/test/java/com/connorjensen/jobtracker/capstone
cp lessons/ch01-java-foundations/capstone/Chapter01CapstoneTest.java src/test/java/com/connorjensen/jobtracker/capstone/
```

- [x] Confirm both copies are the same test, ignoring formatting.

```bash
diff <(tr -d '[:space:]' < lessons/ch01-java-foundations/capstone/Chapter01CapstoneTest.java) \
     <(tr -d '[:space:]' < src/test/java/com/connorjensen/jobtracker/capstone/Chapter01CapstoneTest.java) \
  && echo "grader copies match (ignoring formatting): ok"
```

```text
grader copies match (ignoring formatting): ok
```

A plain `cmp` no longer succeeds here, and that is expected: Lesson 8 put Spotless in the build, so `mvn spotless:apply` reformats the *installed* copy under `src/` while the hidden source file keeps its original line breaks. Stripping whitespace compares the two as programs rather than as byte streams. A difference that survives that strip means the installed copy is genuinely a different test — recopy it.

The repository already carries the synchronized installed copy for this recalibration. Do not read the grader while solving; every assertion is specified here.

While types are missing, use `mvn -Dcheckstyle.skip test` to see compiler and test feedback. Final completion uses the full quality gate.

### Verify

Installation is proved by *discovery*, not by passing: all 39 grader tests must compile and run. Failures at this point are the work ahead, so this one command ignores them.

```bash
mvn -Dcheckstyle.skip -Dmaven.test.failure.ignore=true -Dtest=Chapter01CapstoneTest test 2>&1 | rg 'Tests run: \d+, Fail.*Skipped: \d+$'
```

```text
[ERROR] Tests run: 39, Failures: 8, Errors: 1, Skipped: 0
```

Any count other than 39 means the grader is missing, stale, or not compiling. A compilation error instead of a count means production types are still absent — that is Lesson 6's work, not a bad install.

## Preserve the model and repository contract

Preserve these public types and behavior:

- `com.connorjensen.jobtracker.model.Status` has exactly `APPLIED`, `PHONE_SCREEN`, `INTERVIEWING`, `OFFER`, and `REJECTED`.
- `com.connorjensen.jobtracker.model.Application` keeps its constructor and getters/setters for id, company, role, applied date, status, notes, and job URL.
- A new `Application` defaults to `APPLIED` with a null id.
- `com.connorjensen.jobtracker.repository.ApplicationRepository` keeps `save`, `findAll`, `findById`, `findByStatus`, and `deleteById`.
- `com.connorjensen.jobtracker.repository.InMemoryApplicationRepository` assigns sequential ids from 1, updates existing ids, never returns null collections or Optional values, filters by status, and reports delete success.

Move table labels and row conversion out of the model when the view can own them.

### Verify

```bash
mvn -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$ExistingContractTests' test 2>&1 | rg 'Tests run: \d+, Fail.*Skipped: \d+$|BUILD'
```

```text
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

## Add request records and full service updates

### Request records

Add both records in `com.connorjensen.jobtracker.service`:

```java
public record CreateApplicationRequest(
    String company,
    String role,
    LocalDate appliedDate,
    String notes,
    String jobUrl) {
}
```

```java
public record UpdateApplicationRequest(
    String company,
    String role,
    LocalDate appliedDate,
    Status status,
    String notes,
    String jobUrl) {
}
```

### ApplicationService

Keep the existing constructor and methods, then add the two overloads:

```java
public ApplicationService(ApplicationRepository repository);
public Application create(String company, String role, LocalDate appliedDate);
public Application create(CreateApplicationRequest request);
public List<Application> listAll();
public Optional<Application> findById(Long id);
public List<Application> listByStatus(Status status);
public Application updateStatus(Long id, Status status);
public Application update(Long id, UpdateApplicationRequest request);
public boolean delete(Long id);
```

Required behavior:

- Both create methods build and save an application.
- Request-based create copies notes and job URL and retains the `APPLIED` default.
- Update loads the application, replaces all six mutable values from the request, saves it, and returns it.
- Update and `updateStatus` throw `IllegalArgumentException` for an unknown id.
- Every repository operation uses the repository passed to the constructor; the service never constructs storage.

### Verify

```bash
mvn -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$ServiceTests' test 2>&1 | rg 'Tests run: \d+, Fail.*Skipped: \d+$|BUILD'
```

```text
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

These six cover component order and value equality on both records, request-based create, full update, missing-id exceptions, the older compatibility overloads, and — through a recording repository — the proof that the service really uses its injected dependency.

## Refactor the CLI into four focused classes

### CLI types and exact public API

Delete `util.ConsoleExperience`, `util.ApplicationTextTable`, and `util.Centering` after replacing their callers.

Add these types in `com.connorjensen.jobtracker.cli`:

```java
public ConsolePrompter(Scanner scanner, PrintStream output);
```

```java
public ConsoleView(PrintStream output, TextTable table);
```

```java
public ConsoleApplication(
    ApplicationService service,
    ConsolePrompter prompter,
    ConsoleView view);

public void run();
```

```java
public TextTable();

public String render(List<String> header, List<List<String>> rows);
```

Only those constructors, `ConsoleApplication.run()`, and `TextTable.render(...)` may be declared public in the CLI package. Inherited `Object` methods do not count.

### Main

`com.connorjensen.jobtracker.Main.main(String[] args)` must only:

- Declare `Main` final with a private no-argument constructor because it is a static entry-point utility class.
- Construct `InMemoryApplicationRepository` behind the `ApplicationRepository` interface.
- Construct `ApplicationService`.
- Construct a UTF-8 `Scanner` from `System.in`.
- Construct `TextTable`, `ConsoleView`, `ConsolePrompter`, and `ConsoleApplication`.
- Call `ConsoleApplication.run()`.

Do not put menu dispatch, parsing, CRUD behavior, or table rendering in `Main`.

### Verify

```bash
mvn -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$ArchitectureTests' test 2>&1 | rg 'Tests run: \d+, Fail.*Skipped: \d+$|BUILD'
```

```text
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

The three assertions are the minimal public API, the exact constructor signatures, and the absence of the three deleted `util` classes. This marker is about shape only — behavior is the next section.

## Implement create, list, filter, edit, delete, quit, and EOF behavior

### Stable CLI text

Print this header once:

```text
Job Application Tracker
```

Print this menu before every selection:

```text
0 Create application
1 List applications
2 Filter applications by status
3 Edit application
4 Delete application
5 Quit
>␠
```

The `␠` marker means one trailing ASCII space and is not printed as a symbol. The final `> ` is a prompt without its own newline, so scripted output places the next message directly after it.

Use these exact create prompts:

```text
Company:␠
Role:␠
Applied date (YYYY-MM-DD):␠
Notes (optional):␠
Job URL (optional):␠
```

Use `Status: ` for filtering and `Application ID: ` for edit/delete lookup.

Before edit prompts, print this exact detail shape with actual values and a blank value for null:

```text
Current application
ID: 1
Company: Acme
Role: Engineer
Applied date: 2026-08-01
Status: APPLIED
Notes: referral
URL: https://example.com/jobs/1
```

Then prompt for every field in this order:

```text
Company [Acme]:␠
Role [Engineer]:␠
Applied date [2026-08-01]:␠
Status [APPLIED]:␠
Notes [referral] ('-' clears):␠
Job URL [https://example.com/jobs/1] ('-' clears):␠
```

Use these exact result and error messages:

```text
Created application 1.
Updated application 1.
Deleted application 1.
No applications found.
No applications found for status OFFER.
No application found with ID 99.
Enter a number from 0 to 5.
Value is required.
Enter a date as YYYY-MM-DD.
Enter a positive application ID.
Choose one of: APPLIED, PHONE_SCREEN, INTERVIEWING, OFFER, REJECTED.
Enter a blank value or an absolute HTTP(S) URL.
Goodbye.
```

Every message ends with a newline. Substitute the actual id or normalized filter status where a sample value appears.

### Menu behavior

#### 0 Create application

- Trim company and role; reject blank values with `Value is required.` and retry only that prompt.
- Accept a blank date as today's date, since an application is usually recorded the day it is submitted.
- Otherwise require an ISO local date and retry only the date prompt after failure.
- Accept notes and URL as optional trimmed text.
- Accept a blank URL or an absolute `http`/`https` URI, with the scheme checked case-insensitively.
- Retry only the URL prompt after invalid input.
- Create through `CreateApplicationRequest`; status defaults to `APPLIED`.
- Print the created id.
- If EOF occurs before completion, save nothing and exit cleanly.

#### 1 List applications

- Print `No applications found.` when empty.
- Otherwise render all applications using exactly six columns in this order: `ID`, `Company`, `Role`, `Applied Date`, `Status`, `URL`.
- Render status using the enum name such as `PHONE_SCREEN`.
- Keep notes out of the table.

#### 2 Filter applications by status

- Normalize trimmed input case-insensitively and replace one or more spaces or hyphens with underscores.
- Retry the same status prompt after an unknown value.
- Render matching applications with the same six-column table.
- Print `No applications found for status STATUS.` when there are no matches.

#### 3 Edit application

- Require a positive numeric id and retry only the id prompt after invalid text, zero, or a negative value.
- Print the missing-id message and return to the menu when no application exists.
- Show current data, then prompt for company, role, date, status, notes, and URL in that order.
- Blank input keeps the current value for every field.
- `-` clears notes or URL to an empty string; the prompt advertises that with `('-' clears)`.
- Required fields cannot be cleared; `-` is ordinary invalid content for date and status and a blank company or role keeps the current value.
- Validate every changed value with the same rules used elsewhere.
- Save through `UpdateApplicationRequest` and print the updated id.
- If EOF occurs before completion, save no partial edit and exit cleanly.

#### 4 Delete application

- Use the same positive-id parsing and retry behavior as edit.
- Print the missing-id message when deletion returns false.
- Print the deleted-id message when deletion returns true.
- Repeating the same deletion therefore succeeds once and reports missing once.

#### 5 Quit and EOF

- Print `Goodbye.` exactly once and return from `run()`.
- EOF at the menu or any prompt follows the same clean exit path.
- Do not print a stack trace or exception name.

### Verify

`ConsoleReadTests` owns create, list, and filter; `ConsoleWriteTests` owns edit and delete. Quit and EOF are asserted by `ConsoleValidationTests` in the next section, because they share the lifecycle assertions there.

```bash
mvn -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$ConsoleReadTests,Chapter01CapstoneTest$ConsoleWriteTests' test 2>&1 | rg 'Tests run: \d+, Fail.*Skipped: \d+$|BUILD'
```

```text
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

To work one behavior at a time, append `#methodName` to a nested class, for example `-Dtest='Chapter01CapstoneTest$ConsoleWriteTests#deleteSucceedsOnceAndThenReportsMissing'`.

## Pass table, validation, session, API-shape, and composition tests

This marker is the whole grader plus your own Lesson 7 tests, so it names the five groups that must all be green at once.

| Group       | Where it lives                                     | What it proves                                                   |
|-------------|----------------------------------------------------|------------------------------------------------------------------|
| Table       | `Chapter01CapstoneTest$TextTableTests` (9)         | The pure renderer contract below                                 |
| Validation  | `Chapter01CapstoneTest$ConsoleValidationTests` (6) | Local retries, clean quit, EOF, and real `Main` in a subprocess  |
| Session     | `ConsoleSessionTest` (3)                           | Your own byte-stream session fixture from Lesson 7               |
| API shape   | `Chapter01CapstoneTest$ArchitectureTests` (3)      | Minimal public CLI API and constructor signatures                |
| Composition | `MainProcessTest` (1)                              | The real composition root starts and exits without a stack trace |

### Pure TextTable contract

`TextTable.render(header, rows)` must:

- Return an empty string when header and body contain no cells.
- Determine the column count from the widest header or body row.
- Treat a missing ragged cell or null cell as blank.
- Trim every non-null cell before measuring or rendering.
- Center text with one required space on each side and put an unmatched padding space on the right.
- Render top, header separator, and bottom borders; an empty body shares its separator and bottom border.
- Preserve a rectangle when header and body rows have different lengths.
- Leave the header list, body list, and every nested row unchanged.
- End every nonempty result with exactly one newline.

Required examples:

```text
+---------+------+
| Company | Role |
+---------+------+
|  Acme   | Dev  |
+---------+------+
```

```text
+-----+-----+-------+
|  A  |  B  |       |
+-----+-----+-------+
| one | two | three |
|  x  |     |       |
+-----+-----+-------+
```

### Executable acceptance checklist

The grader covers:

- Request-record component order and value equality.
- Request-based create and full update, including missing ids and injected repository behavior.
- Minimal public CLI API and exact constructor signatures.
- Null, empty, whitespace, six-column, ragged, rectangular, non-mutating, and newline table behavior.
- Create with every field and create retry paths.
- Empty and populated listing.
- Status filtering with and without matches.
- Full edit, blank keep-current values, optional-field clearing, and invalid edit retries.
- Invalid and missing ids.
- Delete success and repeated deletion.
- Non-numeric and out-of-range menu retries.
- Case-insensitive status normalization for spaces and hyphens.
- Invalid date, status, and URL retries at the same prompt.
- Exact important output blocks and ordering.
- Clean quit and EOF without replacing the test JVM's global streams.
- Real `Main` startup in a subprocess.

### Verify

The five groups at once — 39 grader tests, 3 session tests, and 1 composition test:

```bash
mvn -Dcheckstyle.skip -Dtest=Chapter01CapstoneTest,ConsoleSessionTest,MainProcessTest test 2>&1 | rg 'Tests run: \d+, Fail.*Skipped: \d+$|BUILD'
```

```text
[INFO] Tests run: 43, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

To isolate one group while working, run it alone, for example:

```bash
mvn -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$TextTableTests,Chapter01CapstoneTest$ConsoleValidationTests' test 2>&1 | rg 'Tests run: \d+, Fail.*Skipped: \d+$|BUILD'
```

## Pass every completion command

- [x] Run the full quality and test lifecycle.

```bash
mvn verify
```

- [x] Compile the production application explicitly.

```bash
mvn compile
```

- [x] Run and manually exercise the real composition root.

```bash
java -cp target/classes com.connorjensen.jobtracker.Main
```

Chapter 1 is complete only when all three commands succeed and create, list, filter, edit, delete, quit, and EOF work without a crash.

### Verify

No `-Dcheckstyle.skip` here — this is the first command that runs Spotless, Checkstyle, the whole suite, and JaCoCo together:

```bash
mvn verify 2>&1 | rg 'Tests run: \d+, Fail.*Skipped: \d+$|BUILD'
```

```text
[INFO] Tests run: 49, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

The total is larger than the previous section's 43 because `mvn verify` also runs `ApplicationTest` and `ProjectLayoutTest`. If the count differs but everything is green, a lesson test was added or removed — confirm that before treating it as a problem. A Spotless or Checkstyle failure here is a real failure of this marker, not a formatting nit to skip.

## Complete the chapter quiz

### Interview questions

- [x] Why is `Main` a composition root rather than the application itself?
- [x] Where should parsing exceptions be caught, and why?
- [x] Why use request records while keeping `Application` mutable?
- [x] What makes `TextTable` pure, and why does that improve testing?
- [x] What does constructor injection buy without any framework?
- [x] Which quality failures can Checkstyle find, and which still require review?

### Verify

- [x] Run `/code-sensei:quiz` after every completion command passes.

## What later chapters replace

| Chapter 1                                 | Later replacement                                        |
|-------------------------------------------|----------------------------------------------------------|
| Manual object graph in `Main`             | Spring application context in Lesson 11                  |
| Console input and view                    | HTTP controller, binding, and JSON in Lessons 12–13      |
| Request records                           | HTTP request DTOs with Bean Validation in Lesson 13      |
| In-memory repository                      | Spring Data and Postgres in Lesson 14                    |
| `IllegalArgumentException` for missing id | `ApplicationNotFoundException` and HTTP 404 in Lesson 17 |
| Injected stream and subprocess tests      | Mockito and Spring test slices in Lessons 18–19          |

**Chapter:** [README](README.md) · **Glossary:** [GLOSSARY.md](GLOSSARY.md) · **Version history:** [NOTES.md](NOTES.md) · **Standards:** [course standards](../ch00-course-standards/README.md)
