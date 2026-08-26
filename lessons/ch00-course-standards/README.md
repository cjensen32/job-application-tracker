# Course Standards

These are course conventions for this repository, not universal rules imposed by Java.

## Runtime and dependencies

- Compile production code for Java 21.
- Use only the Java standard library in Chapter 1 production code.
- Keep JUnit, JaCoCo, and Checkstyle as build or test tooling, not runtime dependencies.
- Add Spring and other framework dependencies only when their scheduled chapter begins.

## Project layout

- Follow Maven's standard layout: production code in `src/main/java`, tests in `src/test/java`, and build output in `target`.
- Keep package names aligned with directory paths beneath `com.connorjensen.jobtracker`.
- Keep course material in `lessons`; this is a repository convention rather than a Java language requirement.

## Dependency direction

- Treat `Main` as the composition root: it creates concrete dependencies and starts the application.
- Let `cli` depend on `service`, `service` depend on `repository` and `model`, and repository implementations depend on their repository contract and model.
- Do not let `model` depend on CLI, service, repository, build, or framework code.
- Inject collaborators through constructors and type fields to interfaces when a useful contract exists.

## Names and visibility

- Use lowercase package names, `UpperCamelCase` types, `lowerCamelCase` methods and variables, and `UPPER_SNAKE_CASE` constants.
- Use explicit ordered imports and no wildcard imports.
- Keep fields private and expose the smallest API that callers require.
- In the Chapter 1 CLI, only constructors, `ConsoleApplication.run()`, and `TextTable.render(...)` are public.

## Tests

- Follow the reusable [test-writing conventions](../ch00-testing-standards/README.md); Chapter 4 expands them into a full test-writing curriculum.
- Test observable behavior at the narrowest useful boundary.
- Inject `Scanner` and `PrintStream` for deterministic console tests.
- Use a subprocess when testing the real `Main` process or EOF lifecycle.
- Restore global streams in `finally` or avoid replacing them entirely.
- Assert exact output for stable contracts and targeted fragments for longer interactive sessions.
- Include null, empty, whitespace, ragged-row, retry, missing-value, quit, and EOF cases where relevant.

## Quality gates

- Run `mvn verify` for Checkstyle, Spotless, tests, and the JaCoCo report.
- Treat compiler warnings as review work even when they are not build-blocking.
- Checkstyle enforces two-space indentation, 100-column Java lines, names, imports, braces, whitespace, one statement per line, safe switch flow, `equals`/`hashCode` pairing, and utility-class construction.
- Checkstyle reports violations deterministically and never edits files. Spotless holds formatting to `google-java-format` (GOOGLE style) and runs as `spotless:check` in the build, so files are rewritten only when you run `mvn spotless:apply` yourself.
- Run `mvn compile` and the real application after verification because unit tests do not prove the composition root works.

## Framework boundary

- Chapter 1 owns the complete plain-Java CRUD console experience.
- Chapter 2 replaces the CLI with HTTP and manual wiring with Spring.
- Chapter 3 replaces in-memory storage with persistence behind the repository boundary.
- Later chapters add robustness, security, and the frontend in the order listed in `LEARNING.md`.
