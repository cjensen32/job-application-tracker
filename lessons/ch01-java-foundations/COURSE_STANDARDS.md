# Chapter 1 Course Standards

These are conventions for this completed tracker, not universal rules imposed by Java.

## Runtime and layout

- Compile production code for Java 21 and use only the Java standard library at runtime.
- Keep JUnit, JaCoCo, Checkstyle, and Spotless as build or test tooling.
- Follow Maven's standard layout: production code in `src/main/java`, tests in `src/test/java`, and generated output in `target`.
- Keep package declarations aligned with paths beneath `com.connorjensen.jobtracker`.
- Keep learner-facing curriculum under `lessons/`; it must not be required to build or run the code.

## Dependency direction

- Treat `Main` as the composition root: it creates concrete dependencies and starts the application.
- Let `cli` depend on `service`, `service` depend on `repository` and `model`, and repository implementations depend on their contract and model.
- Do not let `model` depend on CLI, service, repository, build, or framework code.
- Inject collaborators through constructors and type fields to interfaces when a useful contract exists.

## Names and visibility

- Use lowercase package names, `UpperCamelCase` types, `lowerCamelCase` methods and variables, and `UPPER_SNAKE_CASE` constants.
- Use explicit ordered imports and no wildcard imports.
- Keep fields private and expose the smallest API callers require.
- In the CLI, only constructors, `ConsoleApplication.run()`, and `TextTable.render(...)` are public.

## Tests

- Follow the [Chapter 1 test-writing standards](TESTING_STANDARDS.md).
- Test observable behavior at the narrowest useful boundary.
- Inject `Scanner` and `PrintStream` for deterministic console tests; use a subprocess for the real `Main` or EOF lifecycle.
- Restore global streams in `finally` or avoid replacing them.
- Assert exact output for stable contracts and ordered fragments for longer sessions.
- Cover null, empty, whitespace, ragged rows, invalid input, retries, missing values, quit, and EOF where relevant.

## Quality gates

- Run `mvn verify` for Checkstyle, Spotless, tests, and JaCoCo.
- Treat compiler warnings as review work even when they are not build-blocking.
- Checkstyle reports violations without editing; Spotless rewrites only when `mvn spotless:apply` is run explicitly.
- Run `mvn compile` and the real application after verification because tests alone do not prove the composition root works.

## Boundary

Chapter 1 owns the complete plain-Java CRUD console experience. Framework, database, browser, and automation implementations are outside this repository; references to them explain the value of the boundaries built here.
