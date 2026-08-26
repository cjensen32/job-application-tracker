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

## Failure diagnosability

A test failure has to say which production method to open. Two events end a test, and only one of them can say it by itself:

- An **exception** captures the live method chain at the throw, so every production frame on the way down appears by file and line.
- An **assertion failure** captures nothing of the kind. The production method already returned and its frame was discarded; JUnit throws at the assert line, so the only project file in the trace is the test.

Most failures in this course are the second kind, so the missing frame is reconstructed by hand in the assertion message:

- Give every assertion a message whose first line is `Owner:` naming the production method that must change, and whose second line is `Expected:` stating the requirement in words rather than in code.
- Keep the literal `Expected:` wording free of implementation detail; a learner reading it should know what to build, not how the assertion was written.
- Prefer one comparison over a whole value — a map, a record, a rendered table — to five comparisons over its parts. Independent assertions report only the first difference; one comparison prints both sides.
- Add an in-method invariant that throws when a real one exists, such as reconciling a count against its source size. A throw restores a genuine production frame that no assertion message can imitate.
- Do not wrap several independent `assertTrue(output.contains(...))` calls in `assertAll`. They carry no cursor into the output, and the report collapses to unlabeled `Multiple Failures`.

## Who writes which tests

Lessons have the learner write tests; capstone graders bring the layer above them. The grader is then a harder version of something already written, not an alien artifact.

| Layer      | Learner writes in lessons                                | Capstone grader brings                            |
|------------|----------------------------------------------------------|---------------------------------------------------|
| Plumbing   | build the fixture, call the operation, capture the value | —                                                 |
| Assertions | one fact per assertion                                   | ordered multi-step scenario composition           |
| Reporting  | `Owner:` and `Expected:` on the assertion message        | the same fields plus a windowed output transcript |
| Structure  | —                                                        | reflection checks on class and method shape       |
| Inputs     | happy path plus one edge case                            | EOF, malformed input, unknown ids, retries        |

Chapter 1's grader is frozen and predates this convention. Lesson 9 introduces the assertion form, and Chapter 2 adds the grader-side `R-##` requirement tag and windowed transcript.

## Reference examples

- `lessons/ch01-java-foundations/capstone/Chapter01CapstoneTest.java` is the installable behavior contract.
- `src/test/java/com/connorjensen/jobtracker/capstone/Chapter01CapstoneTest.java` is its synchronized executable copy.
- `lessons/ch01-java-foundations/07-testing-console-applications.md` explains injected streams, exact tables, sessions, subprocesses, and coverage.

These files intentionally demonstrate a large contract suite. Smaller production classes should usually have smaller focused test classes rather than copying one large file mechanically.
