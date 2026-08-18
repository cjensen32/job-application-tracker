# Lesson 7 — Testing Console Applications

**Goal:** test pure rendering, focused boundaries, complete sessions, and real startup without brittle global-state leaks.

The test shape introduced here follows the reusable [test-writing conventions](../testing-standards/README.md). Chapter 4 turns those conventions into the dedicated test-writing curriculum.

**Prerequisite:** Lesson 6's role-specific CLI classes and constructor seams.

**Progress**

- [ ] Build deterministic stream fixtures
- [ ] Test tables and full sessions at the right boundaries
- [ ] Use a subprocess for the real Main lifecycle
- [ ] Interpret coverage as evidence
- [ ] Complete the self-check

---

## Step 1 — Build deterministic stream fixtures

Tests can drive the real CLI classes with byte-backed streams:

```java
ByteArrayInputStream input = new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
ByteArrayOutputStream bytes = new ByteArrayOutputStream();
PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8);
Scanner scanner = new Scanner(input, StandardCharsets.UTF_8);
```

- [ ] Build a helper that assembles the repository, service, prompter, view, table, and application.
- [ ] Return both the output and service state so each test can assert behavior and presentation.
- [ ] Avoid replacing `System.in` and `System.out` for ordinary session tests.

If a test must replace a global stream, save it and restore it in `finally`, even when the assertion fails.

### Verify

- [ ] Run one create session through injected streams.

```bash
mvn -q -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$ConsoleReadTests#createAcceptsEveryField' test && echo "fixture: ok"
```

```text
fixture: ok
```

If the test hangs, the script probably omitted a retry value, menu selection, or EOF boundary.

## Step 2 — Test tables and full sessions at the right boundaries

`TextTable` is pure, so compare its entire returned string. Stable whitespace is part of that small contract.

Table edge cases include:

- Null, empty, and whitespace-only cells.
- Empty header and body.
- Header wider than body.
- Body wider than header.
- Ragged rows.
- All six application columns.
- Caller collections unchanged after rendering.
- A final newline on every nonempty result.

Full sessions are longer. Assert exact important blocks and their order, then assert saved state. This avoids tying a workflow test to every repeated menu while still protecting the user-visible contract.

### Verify

- [ ] Run the renderer and CRUD-session groups.

```bash
mvn -q -Dcheckstyle.skip -Dtest=Chapter01CapstoneTest test && echo "sessions: ok"
```

```text
sessions: ok
```

If table assertions disagree only on spaces, inspect the rule: values are trimmed, centered, padded to a rectangle, and terminated by a newline.

## Step 3 — Use a subprocess for the real Main lifecycle

A directly constructed session proves the classes cooperate. A subprocess additionally proves the real classpath, `Main` wiring, process streams, EOF, and exit code.

- [ ] Resolve the Java executable from `java.home`.
- [ ] Pass the current test classpath to `java -cp`.
- [ ] Close the subprocess input to send EOF.
- [ ] Bound the wait time and destroy a hung process.
- [ ] Assert exit code zero and no exception text.

The subprocess owns its globals, so the test process cannot leak `System.in` or `System.out` changes.

### Verify

- [ ] Run only the real composition test.

```bash
mvn -q -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$ConsoleValidationTests#mainRunsToCleanEof' test && echo "main: ok"
```

```text
main: ok
```

If the child process times out, inspect which prompt reads without checking `hasNextLine()`.

## Step 4 — Interpret coverage as evidence

Coverage says which code executed, not whether assertions were meaningful. A test can execute every branch and check nothing useful.

- [ ] Run `mvn verify` after the full capstone is green.
- [ ] Open `target/site/jacoco/index.html`.
- [ ] Inspect missed methods in all four CLI classes.
- [ ] Add behavior tests for meaningful misses rather than tests that merely call methods.

The configured missed-method check is non-blocking. It creates a visible review signal without turning coverage into a gameable completion number.

### Verify

- [ ] Generate the report and confirm the XML artifact exists.

```bash
mvn -q verify && test -f target/site/jacoco/jacoco.xml && echo "coverage report: ok"
```

```text
coverage report: ok
```

If the file is absent, check that the JaCoCo report execution is still bound to `verify`.

## Self-check

1. Why are injected stream tests preferable to changing `System.out`?
2. Why should pure table output be asserted exactly?
3. What does a subprocess test prove that a constructed session does not?
4. Why is 100 percent method coverage not proof of correctness?

<details>
<summary>Answers</summary>

1. They isolate state, run safely with other tests, and need no cleanup of process globals.
2. The renderer's complete stable output is its return value and small public contract.
3. It proves the real `Main`, JVM startup, classpath, process streams, EOF, and exit status.
4. Execution without strong assertions can cover code while missing incorrect results.

</details>

## Where this lands you

The CLI now has executable behavior evidence. Lesson 8 adds deterministic source and build gates around the same code.

**Previous:** [Lesson 6 — Refactoring and Package Design](06-refactoring-and-package-design.md) · **Next:** [Lesson 8 — Java Quality Standards](08-java-quality-standards.md) · **Chapter:** [README](README.md)
