# Lesson 4 — Control Flow and Exception Boundaries

**Goal:** turn a fragile menu loop into explicit dispatch with local recovery and clean EOF behavior.

**Prerequisite:** Lesson 3 and a working service/repository object graph.

**Progress**

- [ ] Map one iteration of the application loop
- [ ] Separate dispatch from input recovery
- [ ] Make EOF a normal exit path
- [ ] Complete the self-check

---

## Why this lesson exists

A console program is a boundary: untyped text arrives, typed operations run, and failures become useful messages. If parsing, business operations, output, and looping all live in one method, every new command makes recovery harder.

The central rule is simple: catch an exception only where you can translate it into behavior the caller understands.

## Step 1 — Map one iteration of the application loop

- [ ] Write the loop as four responsibilities before touching code.

```text
show menu → read valid selection → dispatch one operation → repeat
```

- [ ] Mark option `5` and EOF as the two normal exit paths.
- [ ] Mark invalid selection as a retry of only the selection prompt.

A guard clause handles exceptional state early. It keeps the valid path flat:

```java
if (selection == 5) {
  return;
}
```

The guard is not automatically better than a loop condition. Use whichever makes the six menu cases easiest to scan.

### Verify

- [ ] Confirm the current production sources still compile before the structural refactor.

```bash
mvn -q -Dcheckstyle.skip -DskipTests compile && echo "compile: ok"
```

```text
compile: ok
```

If compilation fails here, fix that existing failure before changing the control-flow shape.

## Step 2 — Separate dispatch from input recovery

- [ ] Keep parsing and retry logic near the prompt that owns it.
- [ ] Keep the dispatch block concerned only with which operation runs.

```java
switch (selection) {
  case 0 -> createApplication();
  case 1 -> listApplications();
  case 2 -> filterApplications();
  case 3 -> editApplication();
  case 4 -> deleteApplication();
  case 5 -> running = false;
  default -> throw new IllegalStateException("validated selection escaped its boundary");
}
```

The `default` is an invariant check, not user validation. A valid-menu reader should never return an out-of-range value.

Catch narrow expected failures at the input boundary:

- `NumberFormatException` for numeric text.
- `DateTimeParseException` for ISO dates.
- `IllegalArgumentException` from enum conversion or URI construction when that conversion is local.

Do not wrap the entire session in `catch (Exception)`. That converts programming bugs into misleading user errors.

### Verify

- [ ] Compile after the dispatch refactor.

```bash
mvn -q -Dcheckstyle.skip -DskipTests compile && echo "dispatch: ok"
```

```text
dispatch: ok
```

If a switch branch cannot call its operation, check package visibility and constructor wiring before adding broader access.

## Step 3 — Make EOF a normal exit path

`Scanner.nextLine()` throws when no line exists. Interactive users rarely notice, but redirected input, tests, and closed terminals reach EOF routinely.

- [ ] Check `hasNextLine()` before every line read.
- [ ] Return an explicit absence signal to the session coordinator.
- [ ] Let the coordinator print `Goodbye.` once and return from `run()`.

An empty line and EOF are different:

- Empty line is a value and may mean keep the current edit field.
- EOF means no future value can arrive and ends the session.

### Verify

- [ ] Run the program with an empty input stream after the capstone refactor.

```bash
java -cp target/classes com.connorjensen.jobtracker.Main < /dev/null
```

```text
Job Application Tracker
0 Create application
1 List applications
2 Filter applications by status
3 Edit application
4 Delete application
5 Quit
> Goodbye.
```

If you see `NoSuchElementException`, one prompt still calls `nextLine()` without checking for EOF.

## Self-check

1. Why should an invalid date retry the date prompt instead of restarting the whole create flow?
2. Why is `catch (Exception)` too broad for the application loop?
3. What is the semantic difference between an empty line and EOF?
4. What does the switch `default` mean after selection validation is complete?

<details>
<summary>Answers</summary>

1. Earlier valid fields should remain intact, and the boundary that understands date text can give the most precise recovery.
2. It hides programming defects and cannot produce an honest message for every possible failure.
3. Empty line is user input; EOF means the input source is exhausted.
4. It signals an internal invariant violation because invalid selections should have been rejected already.

</details>

## Where this lands you

You now have the control-flow skeleton for a resilient session. Lesson 5 defines the typed validation rules each prompt must enforce.

**Previous:** [Lesson 3 — Interfaces and Dependency Injection](03-interfaces-and-di.md) · **Next:** [Lesson 5 — Console I/O and Validation](05-console-io-and-validation.md) · **Chapter:** [README](README.md)
