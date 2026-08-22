# Lesson 5 — Console I/O and Validation

**Goal:** turn console text into trusted Java values with explicit streams and local retries.

**Prerequisite:** Lesson 4's dispatch and EOF boundaries.

**Progress**

- [x] Inject input and output instead of owning global streams
- [x] Define required, date, status, ID, and URL validation
- [x] Make text and charset behavior deterministic
- [x] Complete the self-check

---

## Initialization

Continue in `src/main/java/com/connorjensen/jobtracker/Main.java` from Lesson 4. Keep its loop, dispatch, and operation methods in place; Lesson 6 moves working behavior into the `cli` package.

```text
main → run → read one selection → dispatch one operation → repeat
```

No new file is required for this lesson.

## Step 1 — Inject input and output

`Main.main(...)` chooses the process streams. The remaining methods receive what they use.

```text
System.in  → UTF-8 Scanner ─┐
System.out → PrintStream ───┴→ run(...) → prompt and operation methods
```

- [x] Construct the `Scanner` with `StandardCharsets.UTF_8` in `Main.main(...)`.
- [x] Pass the `Scanner` and `PrintStream` into methods that read or write.
- [x] Use `nextLine()` consistently; do not mix it with token reads such as `nextInt()`.
- [x] Keep direct `System.in` and `System.out` access in `Main.main(...)` only.

### Verify

- [x] Compile the explicit I/O path.

```bash
mvn -q -Dcheckstyle.skip -DskipTests compile && echo "streams: explicit"
```

```text
streams: explicit
```

If compilation fails at a caller, pass the new stream parameter through that caller instead of reaching for a global stream.

## Step 2 — Define one rule per input type

Every prompt follows one loop:

```text
print prompt → EOF? stop → read line → valid? return → explain and retry this prompt
```

`Optional.empty()` means EOF only. Invalid input stays inside the prompt loop.

- [x] Required text: `strip()`, reject blank, and retry.
- [ ] Date: require nonblank ISO `YYYY-MM-DD` text and parse it with `LocalDate.parse(...)`.
- [x] ID: parse with `Long.parseLong(...)` and require a value of at least `1`.
- [x] Status: `strip()`, replace spaces or hyphens with underscores, call `toUpperCase(Locale.ROOT)`, then use `Status.valueOf(...)`.
- [x] URL: trim it; accept blank or require an absolute `http` or `https` URI, case-insensitively.
- [x] Edit fields: blank keeps the current value; `-` clears only notes or URL; every changed value uses the same validator as create.

Status normalization is predictable:

| Input          | Enum name      |
|----------------|----------------|
| `offer`        | `OFFER`        |
| `phone screen` | `PHONE_SCREEN` |
| `phone-screen` | `PHONE_SCREEN` |

URI parsing checks syntax. The application adds the absolute HTTP(S) policy, so `URI.create("notes")` is not a valid job URL.

### Verify

- [x] Confirm required text and date failures retry only their own prompt.

```bash
mvn -q -Dcheckstyle.skip -DskipTests compile &&
printf '0\n\nAcme\nEngineer\nnot-a-date\n2026-08-17\n\n\n5\n' |
  java -cp target/classes com.connorjensen.jobtracker.Main |
  rg -o 'Company:|Applied date \(YYYY-MM-DD\):'
```

```text
Company:
Company:
Applied date (YYYY-MM-DD):
Applied date (YYYY-MM-DD):
```

If either prompt appears once, its invalid value escaped instead of retrying locally.

## Step 3 — Make text and charset behavior deterministic

- [ ] In `src/main/java/com/connorjensen/jobtracker/Main.java`, replace the menu-building loop with this class field. Its final newline is part of the value.

```java
private static final String MENU = """
    0 Create application
    1 List applications
    2 Filter applications by status
    3 Edit application
    4 Delete application
    5 Quit
    """;
```

- [x] Use `StandardCharsets.UTF_8` when constructing scanners or converting bytes and strings.
- [x] Keep prompts and messages stable, including spaces and final newlines.
- [x] List exactly `ID`, `Company`, `Role`, `Applied Date`, `Status`, and `URL`; keep notes out of the table.

### Verify

- [x] Compile, then confirm accented input survives create and list.

```bash
mvn -q -Dcheckstyle.skip -DskipTests compile &&
printf '0\nCafé Labs\nDéveloppeur\n2026-08-17\n\nhttps://example.com/jobs/1\n1\n5\n' |
  java -cp target/classes com.connorjensen.jobtracker.Main |
  rg -o 'Café Labs|Développeur'
```

```text
Café Labs
Développeur
```

If either value is missing or corrupted, find the scanner or byte/string conversion that omitted UTF-8.

## Asked Questions

### Do the Lesson 4 methods move into new classes now?

No. Finish the input rules in `Main` first. Lesson 6 moves the working loop, prompts, messages, and table rendering into role-specific `cli` classes.

### Who owns an invalid-menu retry?

The input method prints the short correction and `> ` prompt through its `PrintStream`, then reads again. It does not redraw the full menu. EOF returns `Optional.empty()`; invalid text never does.

## Self-check

1. Why pass a `Scanner` into a method instead of constructing one there?
2. Why is `URI.create("notes")` not enough to validate a job URL?
3. Why use `Locale.ROOT` when normalizing status text?
4. Why does `-` have meaning only for optional edit fields?

<details>
<summary>Answers</summary>

1. The caller controls the input source, which makes behavior deterministic without replacing process-wide state.
2. It is a syntactically valid relative URI, but the contract requires an absolute HTTP(S) URI.
3. Status normalization is protocol-like text handling and must not change with the machine's locale.
4. Required fields cannot be cleared; optional fields need a value distinct from blank, which already means keep current.

</details>

## Where this lands you

The session now retries invalid fields locally and keeps EOF distinct from blank input. Lesson 6 moves this working behavior into focused classes and tightens package boundaries.

**Previous:** [Lesson 4 — Control Flow and Exception Boundaries](04-control-flow-and-exception-boundaries.md) · **Next:** [Lesson 6 — Refactoring and Package Design](06-refactoring-and-package-design.md) · **Chapter:** [README](README.md)
