# Lesson 5 — Console I/O and Validation

**Goal:** turn console text into trusted Java values with injected streams and consistent local retries.

**Prerequisite:** Lesson 4's dispatch and EOF boundaries.

**Progress**

- [ ] Inject input and output instead of owning global streams
- [ ] Define required, date, status, ID, and URL validation
- [ ] Make text and charset behavior deterministic
- [ ] Complete the self-check

---

## Initialization

Start from the completed Lesson 4 state. These shells establish the files and dependency fields Lesson 5 assumes before validation behavior is added; the unfinished `render` method is deliberately visible instead of pretending the table refactor is complete.

- [x] Create `src/main/java/com/connorjensen/jobtracker/cli/ConsolePrompter.java` with its injected input and output dependencies.

```java
package com.connorjensen.jobtracker.cli;

import java.io.PrintStream;
import java.util.Scanner;

public class ConsolePrompter {
  private final Scanner promptScanner;
  private final PrintStream promptStream;

  public ConsolePrompter(Scanner promptScanner, PrintStream promptStream) {
    this.promptScanner = promptScanner;
    this.promptStream = promptStream;
  }
}
```

- [x] Create `src/main/java/com/connorjensen/jobtracker/cli/ConsoleView.java` with its injected output and table dependencies.

```java
package com.connorjensen.jobtracker.cli;

import java.io.PrintStream;

public class ConsoleView {
  private final PrintStream consoleStream;
  private final TextTable textTable;

  public ConsoleView(PrintStream consoleStream, TextTable textTable) {
    this.consoleStream = consoleStream;
    this.textTable = textTable;
  }
}
```

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
}
```

- [ ] Create `src/main/java/com/connorjensen/jobtracker/cli/ConsoleApplication.java` with the dependencies needed to coordinate the session. `run()` stays empty here because its workflow is part of the assignment.

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
}
```

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

  public static void main(String[] args) {
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

Use this conversion map when moving the completed Lesson 4 behavior. It names ownership without supplying the new implementations:

| Existing `Main` content                                                              | New owner                                     |
|--------------------------------------------------------------------------------------|-----------------------------------------------|
| `run(...)` loop and `dispatch(...)` workflow                                         | `ConsoleApplication`                          |
| Menu selection and the input/validation sections of create, filter, edit, and delete | `ConsolePrompter`                             |
| `menu()`, `exit()`, messages, application details, and table display sections        | `ConsoleView`                                 |
| `TextTable.render(...)` call                                                         | `ConsoleView`, using its injected `TextTable` |
| Calls to `ApplicationService` inside each operation                                  | `ConsoleApplication`                          |

Some old methods contain more than one responsibility. Move the sections named above rather than copying an entire mixed method into one class.

## Step 1 — Inject input and output

`System.in` and `System.out` are process-wide mutable state. They are appropriate dependencies for `Main` to choose, but not for every CLI class to reach for directly.

- [ ] Use `promptScanner` and `promptStream` for input behavior instead of reaching for global streams inside `ConsolePrompter`.
- [ ] Use `consoleStream` and `textTable` for display behavior instead of reaching for global output inside `ConsoleView`.
- [ ] Keep the injected collaborators in private final fields so every instance has a complete dependency set after construction.

`Main` remains the only place that chooses the real process streams. Tests can choose byte arrays without replacing global state.

Use line-oriented reads for every prompt. Mixing `nextInt()` with `nextLine()` leaves newline characters behind and creates confusing skipped prompts.

### Verify

- [ ] Compile the new constructor graph.

```bash
mvn -q -Dcheckstyle.skip -DskipTests compile && echo "streams: injected"
```

```text
streams: injected
```

If a test still calls `System.setOut`, first check whether output can be passed through a constructor instead.

## Step 2 — Define one rule per input type

- [ ] Trim company and role, reject blank values, and retry the same field.
- [ ] Parse dates with `LocalDate.parse`, which accepts ISO `YYYY-MM-DD` input.
- [ ] Parse IDs with `Long.parseLong`, then reject zero and negative values.
- [ ] Normalize status text with trim, uppercase, and replacement of spaces or hyphens by underscores.
- [ ] Accept a blank URL or require an absolute URI whose scheme is `http` or `https`, case-insensitively.

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

- [ ] Run the focused capstone validation group after installing the grader.

```bash
mvn -q -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$ConsoleValidationTests#invalidInputRetriesLocally' test && echo "validation: ok"
```

```text
validation: ok
```

If the entire form restarts, the retry loop is outside the individual prompt boundary.

## Step 3 — Make text and charset behavior deterministic

Text blocks keep stable multiline messages readable. They do not remove whitespace from the contract: indentation and the final newline remain observable.

- [ ] Use `StandardCharsets.UTF_8` whenever tests convert bytes to strings or create byte-backed streams.
- [ ] Keep prompts in `ConsolePrompter` and display blocks in `ConsoleView`.
- [ ] Keep `TextTable` pure by returning a string instead of printing it.

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
