# Chapter 1 Capstone — The Tracker Core

Build a complete plain-Java CRUD console tracker. The console and in-memory store are temporary adapters; the model, repository contract, service boundary, request records, and dependency direction survive into later chapters.

This capstone is intentionally contract-first and challenging. Choose your own private helper names and implementation order, but match every public type, signature, behavior, and stable output below.

**Progress**

- [ ] Install the synchronized capstone grader
- [ ] Preserve the model and repository contract
- [ ] Add request records and full service updates
- [ ] Refactor the CLI into four focused classes
- [ ] Implement create, list, filter, edit, delete, quit, and EOF behavior
- [ ] Pass table, validation, session, API-shape, and composition tests
- [ ] Pass every completion command
- [ ] Complete the chapter quiz

## Constraints

- Use Java 21 and the standard library for production code.
- Do not add Spring, a database, or a runtime dependency.
- Keep the configured build plugins as they are; Checkstyle and Spotless are part of the gate, not settings to swap or disable.
- Follow the [course standards](../course-standards/README.md) and `config/checkstyle/checkstyle.xml`.
- Treat `Main` as the composition root only.
- Keep public CLI API limited to the exact constructors and methods listed below.
- Keep helper names private or package-private; the grader does not prescribe them.

## Install the grader

- [ ] Copy the hidden grader into the normal test tree and confirm both copies match.

```bash
mkdir -p src/test/java/com/connorjensen/jobtracker/capstone
cp lessons/ch01-java-foundations/capstone/Chapter01CapstoneTest.java src/test/java/com/connorjensen/jobtracker/capstone/
cmp lessons/ch01-java-foundations/capstone/Chapter01CapstoneTest.java src/test/java/com/connorjensen/jobtracker/capstone/Chapter01CapstoneTest.java
```

The repository already carries the synchronized installed copy for this recalibration. Do not read the grader while solving; every assertion is specified here.

While types are missing, use `mvn -Dcheckstyle.skip test` to see compiler and test feedback. Final completion uses the full quality gate.

## Required production types

### Existing model and repository types

Preserve these public types and behavior:

- `com.connorjensen.jobtracker.model.Status` has exactly `APPLIED`, `PHONE_SCREEN`, `INTERVIEWING`, `OFFER`, and `REJECTED`.
- `com.connorjensen.jobtracker.model.Application` keeps its constructor and getters/setters for id, company, role, applied date, status, notes, and job URL.
- A new `Application` defaults to `APPLIED` with a null id.
- `com.connorjensen.jobtracker.repository.ApplicationRepository` keeps `save`, `findAll`, `findById`, `findByStatus`, and `deleteById`.
- `com.connorjensen.jobtracker.repository.InMemoryApplicationRepository` assigns sequential ids from 1, updates existing ids, never returns null collections or Optional values, filters by status, and reports delete success.

Move table labels and row conversion out of the model when the view can own them.

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

## Stable CLI text

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
Notes [referral] (blank keeps, - clears):␠
Job URL [https://example.com/jobs/1] (blank keeps, - clears):␠
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

## Menu behavior

### 0 Create application

- Trim company and role; reject blank values with `Value is required.` and retry only that prompt.
- Require an ISO local date and retry only the date prompt after failure.
- Accept notes and URL as optional trimmed text.
- Accept a blank URL or an absolute `http`/`https` URI, with the scheme checked case-insensitively.
- Retry only the URL prompt after invalid input.
- Create through `CreateApplicationRequest`; status defaults to `APPLIED`.
- Print the created id.
- If EOF occurs before completion, save nothing and exit cleanly.

### 1 List applications

- Print `No applications found.` when empty.
- Otherwise render all applications using exactly six columns in this order: `ID`, `Company`, `Role`, `Applied Date`, `Status`, `URL`.
- Render status using the enum name such as `PHONE_SCREEN`.
- Keep notes out of the table.

### 2 Filter applications by status

- Normalize trimmed input case-insensitively and replace one or more spaces or hyphens with underscores.
- Retry the same status prompt after an unknown value.
- Render matching applications with the same six-column table.
- Print `No applications found for status STATUS.` when there are no matches.

### 3 Edit application

- Require a positive numeric id and retry only the id prompt after invalid text, zero, or a negative value.
- Print the missing-id message and return to the menu when no application exists.
- Show current data, then prompt for company, role, date, status, notes, and URL in that order.
- Blank input keeps the current value for every field.
- `-` clears notes or URL to an empty string.
- Required fields cannot be cleared; `-` is ordinary invalid content for date and status and a blank company or role keeps the current value.
- Validate every changed value with the same rules used elsewhere.
- Save through `UpdateApplicationRequest` and print the updated id.
- If EOF occurs before completion, save no partial edit and exit cleanly.

### 4 Delete application

- Use the same positive-id parsing and retry behavior as edit.
- Print the missing-id message when deletion returns false.
- Print the deleted-id message when deletion returns true.
- Repeating the same deletion therefore succeeds once and reports missing once.

### 5 Quit and EOF

- Print `Goodbye.` exactly once and return from `run()`.
- EOF at the menu or any prompt follows the same clean exit path.
- Do not print a stack trace or exception name.

## Pure TextTable contract

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

## Executable acceptance checklist

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

## Completion

- [ ] Run the full quality and test lifecycle.

```bash
mvn verify
```

- [ ] Compile the production application explicitly.

```bash
mvn compile
```

- [ ] Run and manually exercise the real composition root.

```bash
java -cp target/classes com.connorjensen.jobtracker.Main
```

Chapter 1 is complete only when all three commands succeed and create, list, filter, edit, delete, quit, and EOF work without a crash.

## What later chapters replace

| Chapter 1                                 | Later replacement                                        |
|-------------------------------------------|----------------------------------------------------------|
| Manual object graph in `Main`             | Spring application context in Lesson 9                   |
| Console input and view                    | HTTP controller, binding, and JSON in Lessons 10–11      |
| Request records                           | HTTP request DTOs with Bean Validation in Lesson 11      |
| In-memory repository                      | Spring Data and Postgres in Lesson 12                    |
| `IllegalArgumentException` for missing id | `ApplicationNotFoundException` and HTTP 404 in Lesson 15 |
| Injected stream and subprocess tests      | Mockito and Spring test slices in Lessons 16–17          |

## Interview questions

- [ ] Why is `Main` a composition root rather than the application itself?
- [ ] Where should parsing exceptions be caught, and why?
- [ ] Why use request records while keeping `Application` mutable?
- [ ] What makes `TextTable` pure, and why does that improve testing?
- [ ] What does constructor injection buy without any framework?
- [ ] Which quality failures can Checkstyle find, and which still require review?

- [ ] Run `/code-sensei:quiz` after every completion command passes.

**Chapter:** [README](README.md) · **Glossary:** [GLOSSARY.md](GLOSSARY.md) · **Version history:** [NOTES.md](NOTES.md) · **Standards:** [course standards](../course-standards/README.md)
