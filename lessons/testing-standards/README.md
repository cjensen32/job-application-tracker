# Test-Writing Conventions

This is the course-wide reference for readable Java tests. Chapter 1 applies the conventions to a console application; Chapter 4 teaches them in depth with JUnit, Mockito, integration tests, and Spring test slices.

## Test organization

- Mirror production packages when package-private access is genuinely part of the test boundary.
- Group a large behavior contract with `@Nested` classes and plain-language `@DisplayName` labels.
- Name test methods as observable outcomes, such as `deleteSucceedsOnceAndThenReportsMissing`.
- Keep one primary behavior per test; use `assertAll` only for several facets of that same behavior.
- Keep constants and fixture helpers below the behavior groups unless early placement materially improves readability.

## Test body shape

- Separate arrange, act, and assert sections with blank lines instead of comments that repeat the code.
- Make the action visually obvious and keep setup smaller than the behavior under test.
- Assert returned values, persisted state, and stable output rather than private helper calls.
- Include assertion messages when failure intent is not already clear from the method and expected value.
- Prefer exact equality for small stable contracts and targeted ordered fragments for long interactive output.

## Fixtures and helpers

- Build fresh mutable state per test.
- Let fixture helpers remove mechanical setup without hiding the values important to the scenario.
- Pass seed behavior explicitly when a session needs existing data.
- Keep byte/string conversions on `StandardCharsets.UTF_8`.
- Never make test order significant.

## Isolation

- Inject streams, clocks, repositories, and other boundaries when production design provides a seam.
- Avoid replacing `System.in` or `System.out`; restore them in `finally` when replacement is unavoidable.
- Use a subprocess for the real JVM startup, classpath, process-stream, exit-code, and EOF contract.
- Bound asynchronous and subprocess waits so a defect fails instead of hanging the suite.
- Do not share writable static fixtures.

## Choosing test scope

- Use a unit test for a pure function or one object with controlled collaborators.
- Use an integration test when the behavior is the collaboration among real objects.
- Use a subprocess or end-to-end test only for behavior that narrower tests cannot prove.
- Do not mock value objects, collections, or the class whose behavior is under test.
- Mock or fake an architectural boundary to prove dependency direction, not merely to increase isolation.

## Edge cases

- Derive edge cases from the contract: empty, null, whitespace, boundary numbers, invalid syntax, unknown ids, retries, duplicate actions, quit, and EOF.
- Add a regression test that fails for the observed bug before changing production code.
- Use parameterized tests when the cases share one behavior and differ only in data.
- Keep distinct workflows in distinct tests even if they reuse the same fixture helper.

## Java quality standards in tests

- Apply the same naming, imports, indentation, line length, braces, whitespace, and warning discipline as production code.
- Keep tests deterministic and independent of locale, default charset, current date, filesystem order, and network access unless that dependency is the subject of the test.
- Close resources or use try-with-resources when ownership belongs to the test.
- Do not catch an assertion failure to keep a test passing.
- Treat coverage as evidence of exercised code, not as proof that assertions are meaningful.

## Diagnosing a failure

Two different events fail a test, and they carry different information.

- **An exception.** Something threw. The JVM recorded the live method chain, so the stack trace names *your* files.
- **An assertion failure.** Nothing threw. The code ran to completion and returned a value the test disliked. JUnit throws at the assert statement, so the only file in the trace is the test.

Most console and API failures are the second kind, and often unavoidably so. A prompt that must re-prompt on bad input catches its own `IllegalArgumentException` one line after `Status.valueOf` throws it — correctly, because the spec requires re-prompting rather than crashing. The exception is born and dies inside the method under test. A stack frame pointing into that method would appear only if the program crashed, which would mean the spec was failed.

So the failures worth diagnosing are, by construction, the ones that cannot produce a useful stack trace. Write tests that supply the missing information rather than hoping for a trace:

- From Lesson 9 onward, give every learner-written assertion a message whose first line is `Owner:` naming the responsible production method and whose second line is `Expected:` stating the requirement in words rather than implementation detail.
- Show the input that produced the failure alongside the output that resulted.
- In post-Chapter-1 grader reports, show a narrow window of the output around the divergence, not a boolean and not a full dump.
- Guard preconditions before destructuring. `service.listAll().getFirst()` on an empty list throws `NoSuchElementException` from the *test's* bookkeeping and buries the real defect; `assertEquals(1, saved.size(), "create must persist one application")` names it.
- Prefer one comparison over a whole structured value and one ordered assertion over a bag of independent `contains` checks. Ordered matching carries a cursor that says how far the output was correct; `assertAll` over unlabeled booleans reports `Multiple Failures (n failures)` and tells you nothing about which.
- Add an in-method invariant that throws when a real one exists, such as reconciling a count against its source size. A throw restores a genuine production frame that no assertion message can imitate.

### Surfacing swallowed exceptions on demand

A `catch (SomeException ignored)` is correct when the contract says recover, but it destroys the origin. Log it behind a flag rather than deleting the information:

```console
$ mvn -Dtracker.debug=true test
[debug] ConsolePrompter.promptStatus:166 caught IllegalArgumentException for input "unknown", re-prompting
```

Production behavior is unchanged; the origin is available when you need it.

## Who writes which tests

Chapter 1's grader is frozen and predates this convention. Beginning with Lesson 9 after the Chapter 1 capstone, lessons have the learner write tests with `Owner:` and `Expected:` messages. Beginning with Chapter 2, every capstone grader imports that chapter's learner-written harness and adds `R-##` requirement tags, ordered scenario composition, structural reflection checks, adversarial inputs, and windowed output reports.

| Layer      | Learner writes in lessons                                | Capstone grader brings                            |
|------------|----------------------------------------------------------|---------------------------------------------------|
| Plumbing   | build the fixture, call the operation, capture the value | —                                                 |
| Assertions | one fact per assertion                                   | ordered multi-step scenario composition           |
| Reporting  | `Owner:` and `Expected:` on the assertion message        | those fields plus `R-##` and a windowed transcript |
| Structure  | —                                                        | reflection checks on class and method shape       |
| Inputs     | happy path plus one edge case                            | EOF, malformed input, unknown ids, retries        |

A harness is chapter-local. Chapter 1's console harness does not survive the move to HTTP; Chapter 2 builds its own. The post-Chapter-1 report format and ownership split carry between chapters; the harness code does not.

## Reference examples

- `lessons/ch01-java-foundations/capstone/Chapter01CapstoneTest.java` is the installable behavior contract.
- `src/test/java/com/connorjensen/jobtracker/capstone/Chapter01CapstoneTest.java` is its synchronized executable copy.
- `lessons/ch01-java-foundations/07-testing-console-applications.md` explains injected streams, exact tables, sessions, subprocesses, and coverage.

These files intentionally demonstrate a large contract suite. Smaller production classes should usually have smaller focused test classes rather than copying one large file mechanically.
