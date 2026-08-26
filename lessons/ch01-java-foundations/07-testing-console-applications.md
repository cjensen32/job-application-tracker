# Lesson 7 — Testing Console Applications

**Goal:** drive the real console classes from byte-backed streams, test a pure renderer exactly, prove the real `Main` starts, and read coverage as evidence rather than as a score.

The test shape introduced here follows the reusable [test-writing conventions](../ch00-testing-standards/README.md). Chapter 4 turns those conventions into the dedicated test-writing curriculum.

**Prerequisite:** Lesson 6's finished CLI split, request records, and minimal public API.

**Progress**

- [x] Build a deterministic session fixture
- [x] Test the pure renderer exactly, and fix what that exposes
- [x] Use a subprocess for the real Main lifecycle
- [x] Interpret coverage as evidence
- [x] Complete the self-check

---

## Initialization

Start from the end of Lesson 6. The capstone grader now compiles, so the whole suite runs for the first time since you installed it.

- [x] See where you actually stand.

```bash
mvn -Dcheckstyle.skip -Dtest=Chapter01CapstoneTest test 2>&1 | rg 'Tests run: \d+, Fail.*Skipped: \d+$'
```

```text
[ERROR] Tests run: 38, Failures: 10, Errors: 1, Skipped: 0
```

Eleven red out of thirty-eight is the normal shape at this point, not a problem with your Lesson 6 work. They fall into two groups, and this lesson only owns one of them:

| Failing group                                                     | Count | Owner    | What it is                                                        |
|-------------------------------------------------------------------|-------|----------|-------------------------------------------------------------------|
| `TextTableTests`                                                  | 4     | Lesson 7 | Real defects in `render(...)` that only exact assertions expose   |
| `ConsoleReadTests`, `ConsoleWriteTests`, `ConsoleValidationTests` | 7     | Capstone | Prompt wording, edit and delete control flow, and message strings |

You will write your own tests in a new file, `src/test/java/com/connorjensen/jobtracker/cli/ConsoleSessionTest.java`. Putting it in the `cli` package is deliberate: Lesson 6 made those classes package-private, and a test in the same package can still construct and drive them without reopening the API.

Do not open the grader. [CAPSTONE.md](CAPSTONE.md) specifies every string and behavior it asserts.

## Step 1 — Build a deterministic session fixture

A console session needs an input stream, an output stream, and a way to look at the result. All three can be plain byte buffers, which means a session runs in memory with no console, no timing, and no global state:

```java
ByteArrayInputStream input = new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));
ByteArrayOutputStream bytes = new ByteArrayOutputStream();
PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8);
Scanner scanner = new Scanner(input, StandardCharsets.UTF_8);
```

This is why Lesson 5 made you inject the `Scanner` and `PrintStream` instead of reaching for `System.in` and `System.out`. The payoff is here: nothing has to be saved and restored, tests can run in parallel, and a failing assertion cannot leave the JVM's globals broken for the next test.

- [x] Create `ConsoleSessionTest` with a private helper that assembles repository, service, table, view, prompter, and application, runs one session, and returns both the captured output and the service.
- [x] Return the service, not just the text — presentation and saved state are two different assertions, and a test that only reads output cannot tell you whether anything was stored.
- [x] Assert the quit-only session's output exactly, using a text block.
- [x] Assert that an empty script saves nothing.

```java
// src/test/java/com/connorjensen/jobtracker/cli/ConsoleSessionTest.java — edit excerpt
public class ConsoleSessionTest {
  private record Session(
      String output,
      ApplicationService service) {
  }

  private static Session run(String script) {
    // build the streams above, wire the four cli classes, call run(), return both
    throw new UnsupportedOperationException("TODO");
  }
}
```

If a test ever *must* replace `System.in` or `System.out`, save the original and restore it in a `finally` block so a failed assertion still cleans up. No test in this lesson needs to.

### Verify

- [x] Run only your own test class.

```bash
mvn -Dcheckstyle.skip -Dtest=ConsoleSessionTest test 2>&1 | rg 'Tests run:.*Skipped|BUILD'
```

```text
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.037 s -- in com.connorjensen.jobtracker.cli.ConsoleSessionTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

If the run never finishes, the script ended while a prompt was still looping; if it fails with `NoSuchElementException`, the session asked for more lines than the script supplied.

## Step 2 — Test the pure renderer exactly, and fix what that exposes

`TextTable.render(...)` takes lists and returns a string. It touches no streams and no state, so its entire output is its contract and the right assertion is `assertEquals` on the whole thing. Stable whitespace is part of that contract, not an implementation detail.

**Checkpoint.** Before reading the next paragraph, run the grader's table group and look at the four diffs.

- [x] Run the table group.

```bash
mvn -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$TextTableTests' test 2>&1 | rg 'Tests run: \d+, Fail'
```

```text
[ERROR] Tests run: 9, Failures: 4, Errors: 0, Skipped: 0
```

Four defects, and none of them are reachable through the app. `showApplications` always hands over six headers and six-cell rows, so every input that breaks the renderer is an input the console never produces. That is the whole argument for testing a pure function directly instead of only through its caller.

| Failing test                               | What `render(...)` does now                                        | Why                                                                   |
|--------------------------------------------|--------------------------------------------------------------------|-----------------------------------------------------------------------|
| `completelyEmptyInputReturnsEmptyString`   | Returns a three-line `+` / `\|` / `+` skeleton for no cells at all | `buildBorder` appends its closing `+\n` even with zero columns        |
| `nullEmptyAndWhitespaceCellsAreNormalized` | Measures `" A "` as 3 wide, then renders it as `A`                 | Widths come from the raw string; `center(...)` strips it afterwards   |
| `raggedRowsRemainRectangular`              | Renders a one-cell row against a three-column border               | `standardizeValues` pads rows only when the header is the longer side |
| `renderDoesNotMutateCallerCollections`     | Turns the caller's `[ A , null]` into `[ A , null, ]`              | `standardizeValues` calls `add("")` on the list it was handed         |

The last one is the interesting one. `render` looks pure — `List` in, `String` out — but it edits its arguments on the way through. Today the app gets away with it because `Application.toLabelsList()` handed back a fresh list every call; Lesson 6 replaced that with a shared `COLUMNS` constant, so the same bug would now corrupt the constant if you had not copied it at the call site.

- [x] Copy the header and every row into new lists before touching them.
- [x] Strip each cell once, up front, and treat `null` and a missing ragged cell identically as `""`.
- [x] Take the column count from the widest of the header and all rows, and pad every row to it.
- [x] Return `""` when there are no columns at all.
- [x] Measure widths from the stripped values, so measuring and rendering agree.

Doing all five at once is easier than patching, because they are the same idea: normalize the input into a rectangle of stripped strings first, then render. The old code interleaved normalizing, measuring, and printing, which is why one method could hold four bugs.

### Verify

- [x] Run the table group again.

```bash
mvn -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$TextTableTests' test 2>&1 | rg 'Tests run:.*Skipped|BUILD'
```

```text
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.045 s -- in com.connorjensen.jobtracker.capstone.Chapter01CapstoneTest$TextTableTests
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

If two tables differ only in spaces, re-read the rule: cells are stripped, padded to a rectangle, centered with the odd space on the right, and the result ends with exactly one newline.

## Step 3 — Use a subprocess for the real Main lifecycle

A directly constructed session proves the four classes cooperate. It does not prove that `Main` wires them the same way, that the class is on the classpath under the name you think, that the process exits, or that EOF on a real pipe behaves like EOF on a `ByteArrayInputStream`. A subprocess proves all of it.

- [x] Resolve the Java executable from the `java.home` system property rather than assuming `java` is on `PATH`.
- [x] Pass the current test classpath through to `java -cp`.
- [x] Close the child's input stream to send EOF.
- [x] Bound the wait and destroy the process if it overruns, so a regression fails in seconds instead of hanging the build.
- [x] Assert exit code `0` and no exception text in the output.

The child owns its own globals, so this is also the one test shape that can safely exercise `System.in` and `System.out` without leaking anything into the test JVM.

### Verify

- [x] Run the grader's composition test.

```bash
mvn -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$ConsoleValidationTests#mainRunsToCleanEof' test 2>&1 | rg 'Tests run:.*Skipped|BUILD'
```

```text
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.130 s -- in com.connorjensen.jobtracker.capstone.Chapter01CapstoneTest$ConsoleValidationTests
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

If the child times out, find the prompt that calls `nextLine()` without first checking `hasNextLine()` — under EOF that prompt never returns.

## Step 4 — Interpret coverage as evidence

Coverage tells you which code ran. It does not tell you whether anything was checked. This project makes that unusually easy to see.

- [x] Generate the report without letting the remaining capstone failures stop the build.

```bash
mvn -q -Dcheckstyle.skip -Dmaven.test.failure.ignore=true verify > /dev/null 2>&1; test -f target/site/jacoco/jacoco.xml && echo "coverage report: ok"
```

```text
coverage report: ok
```

- [x] Open `target/site/jacoco/index.html` and look at the four CLI classes.
- 
Read the **Missed / Methods** pair of columns, not the instruction or branch bars. With the capstone's session tests still failing, the method counter reads like this:

```text
ConsolePrompter      methods: missed 0  covered 14
TextTable            methods: missed 0  covered 5
ConsoleView          methods: missed 0  covered 12
ConsoleApplication   methods: missed 0  covered 8
```

Zero missed methods across the whole console layer, and seven grader tests still red. Every method executes; several of them print the wrong thing while doing it. That is the entire lesson about coverage in one screen.

The other columns on that same row are *not* zero — instructions and branches still show gaps, largest in `ConsoleApplication`. That is the finer-grained version of the same signal: a missed branch names a path no test takes. It still cannot tell you that a taken path produced the right output.

- [x] Treat a *missed* method as a question — which behavior has no test at all?
- [x] Do not treat a *covered* method as an answer; add assertions about results, not calls that raise the number.

The configured missed-method check in `pom.xml` runs with `haltOnFailure` set to `false` on purpose. It produces a visible review signal without turning coverage into a number worth gaming.

Once the capstone's session behavior is green, the real gate is the plain `mvn verify` from [CAPSTONE.md](CAPSTONE.md) — which also runs Spotless and Checkstyle, the subject of Lesson 8.

### Verify

- [x] Confirm the report is regenerated from the current run rather than an old one.

```bash
rm -rf target/site/jacoco && mvn -q -Dcheckstyle.skip -Dmaven.test.failure.ignore=true verify > /dev/null 2>&1; test -f target/site/jacoco/jacoco.xml && echo "coverage report: ok"
```

```text
coverage report: ok
```

If the file is absent, check that the JaCoCo `report` execution is still bound to the `verify` phase in `pom.xml`.

## Self-check

1. Why are injected-stream tests preferable to replacing `System.out`?
2. Why did four `TextTable` bugs survive every manual run of the application?
3. Why is asserting the renderer's entire output reasonable, when asserting a whole session's output is not?
4. What does the subprocess test prove that a constructed session does not?
5. What does "zero missed methods, seven failing tests" tell you about coverage?

<details>
<summary>Answers</summary>

1. They isolate state, run safely alongside other tests, and need no cleanup that a failed assertion could skip.
2. The console only ever calls `render` with well-formed six-column input, so the broken paths were unreachable from the UI.
3. The renderer's complete return value *is* its contract; a session's output includes repeated menus that no behavior depends on, so exact blocks and their order are the useful assertion.
4. It proves the real `Main`, JVM startup, the classpath, process streams, pipe EOF, and the exit status.
5. Execution is not verification — coverage can only tell you what was never reached, never that what was reached is correct.

</details>

## Where this lands you

The console layer now has executable evidence behind it, and the renderer is genuinely pure. What is left red is session behavior — prompt wording, edit and delete control flow, and the message contract — which is the capstone's work. Lesson 8 adds the deterministic source and build gates that `mvn verify` runs around all of it.

**Previous:** [Lesson 6 — Refactoring and Package Design](06-refactoring-and-package-design.md) · **Next:** [Lesson 8 — Java Quality Standards](08-java-quality-standards.md) · **Chapter:** [README](README.md)
