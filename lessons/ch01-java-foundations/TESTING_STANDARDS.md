# Chapter 1 Test-Writing Standards

This learner-facing reference collects the testing conventions used by the console tracker and its capstone.

## Organization

- Mirror production packages when package-private access is genuinely part of the test boundary.
- Group a large behavior contract with `@Nested` classes and plain-language `@DisplayName` labels.
- Name test methods as observable outcomes, such as `deleteSucceedsOnceAndThenReportsMissing`.
- Keep one primary behavior per test; use `assertAll` only for several facets of that same behavior.

## Test body shape

- Separate arrange, act, and assert with blank lines rather than comments that repeat the code.
- Make the action visually obvious and keep setup smaller than the behavior under test.
- Assert returned values, persisted state, and stable output rather than private helper calls.
- Prefer exact equality for small stable contracts and ordered fragments for long interactive output.

## Fixtures and isolation

- Build fresh mutable state per test and never make test order significant.
- Let helpers remove mechanical setup without hiding scenario values.
- Use `StandardCharsets.UTF_8` for byte/string conversion.
- Inject streams, clocks, repositories, and other boundaries when the design provides a seam.
- Avoid replacing `System.in` or `System.out`; restore them in `finally` when replacement is unavoidable.
- Use a bounded subprocess for JVM startup, classpath, exit-code, and EOF behavior.
- Do not share writable static fixtures.

## Scope and edge cases

- Use a unit test for a pure function or one object with controlled collaborators.
- Use an integration test for collaboration among real objects.
- Use a subprocess only for behavior narrower tests cannot prove.
- Fake an architectural boundary to prove dependency direction, not merely to increase isolation.
- Derive edge cases from the contract: empty, null, whitespace, boundary numbers, invalid syntax, unknown IDs, retries, duplicate actions, quit, and EOF.
- Add a regression test that fails for an observed bug before changing production code.

## Quality

- Apply production naming, imports, indentation, line length, braces, whitespace, and warning discipline to tests.
- Keep tests independent of locale, default charset, current date, filesystem order, and network access unless that dependency is under test.
- Close owned resources, never catch an assertion failure to keep a test green, and treat coverage as exercised-code evidence rather than correctness proof.

## Reference examples

- [`capstone/Chapter01CapstoneTest.java`](capstone/Chapter01CapstoneTest.java) is the installable behavior contract.
- `src/test/java/com/connorjensen/jobtracker/capstone/Chapter01CapstoneTest.java` is its synchronized executable copy.
- [Lesson 7](07-testing-console-applications.md) explains injected streams, exact tables, sessions, subprocesses, and coverage.
