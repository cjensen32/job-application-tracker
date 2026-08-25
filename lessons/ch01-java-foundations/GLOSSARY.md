# Chapter 1 — Glossary and Cheatsheet

Use this as the fast re-entry point for Lessons 0–8.

## Build and tooling

| Term        | Meaning                                                                                                |
|-------------|--------------------------------------------------------------------------------------------------------|
| JVM         | Runtime that executes Java bytecode.                                                                   |
| JDK         | Compiler, tools, and runtime used for development.                                                     |
| classpath   | Locations where the compiler or JVM searches for classes.                                              |
| Maven       | Build and dependency tool driven by `pom.xml`.                                                         |
| coordinates | `groupId`, `artifactId`, and `version` identifying an artifact.                                        |
| scope       | Lifecycle locations where a dependency is available, such as `compile` or `test`.                      |
| lifecycle   | Ordered phases including `validate`, `compile`, `test`, `package`, and `verify`.                       |
| Surefire    | Maven plugin that runs JUnit tests; this project pins a JUnit 5-capable version.                       |
| JaCoCo      | Coverage agent and report generator; its CLI missed-method check is non-blocking.                      |
| Checkstyle  | Deterministic source-rule checker; it reports style and structural violations without formatting code. |
| Spotless    | Format gate running `google-java-format`; `check` fails on drift and `apply` rewrites the files.       |

## Language and data

| Term      | Meaning                                                                                     |
|-----------|---------------------------------------------------------------------------------------------|
| class     | Mutable or behavioral object blueprint with fields, constructors, and methods.              |
| record    | Concise value carrier with generated accessors, value equality, hash code, and string form. |
| enum      | Fixed runtime set of typed singleton values.                                                |
| primitive | Built-in value such as `int` that cannot be null.                                           |
| wrapper   | Object form such as `Long` that can represent null.                                         |
| generic   | Type parameter such as `List<Application>` checked by the compiler.                         |
| Optional  | Return type that makes absence explicit without returning null.                             |
| stream    | Lazy pipeline for transforming or selecting collection data.                                |
| URI       | Structured identifier; `isAbsolute`, scheme checks, and parsing support URL validation.     |

## Control flow and boundaries

| Term                  | Meaning                                                                                        |
|-----------------------|------------------------------------------------------------------------------------------------|
| guard clause          | Early return or rejection that keeps invalid state out of the main path.                       |
| dispatch              | Routing one menu selection to one operation, usually with `switch`.                            |
| recoverable exception | Expected boundary failure such as invalid user text that should produce a retry.               |
| exception boundary    | Layer that translates a low-level exception into behavior meaningful to its caller.            |
| EOF                   | End of input; `Scanner.hasNextLine()` detects it without throwing.                             |
| normalization         | Converting equivalent forms to one canonical value, such as spaces and hyphens to underscores. |
| invariant             | Condition that must remain true, such as a positive application ID.                            |

## Console I/O

| Term         | Meaning                                                                                |
|--------------|----------------------------------------------------------------------------------------|
| Scanner      | Token and line reader; this CLI uses line reads to keep prompt boundaries predictable. |
| PrintStream  | Output dependency used for prompts and views.                                          |
| UTF-8        | Explicit charset used when turning bytes into text in tests or subprocesses.           |
| text block   | Multiline string literal delimited by three quotation marks.                           |
| prompt retry | Loop local to one input field that repeats only that field after invalid input.        |

## Design

| Term                  | Meaning                                                                                |
|-----------------------|----------------------------------------------------------------------------------------|
| cohesion              | Degree to which one class has one focused responsibility.                              |
| coupling              | Degree to which a class depends on details of another class.                           |
| interface             | Compiler-enforced contract implemented by one or more classes.                         |
| dependency injection  | Receiving collaborators rather than constructing them internally.                      |
| composition root      | Single place that creates concrete objects and connects them; here it is `Main`.       |
| constructor injection | Passing required collaborators through a constructor and storing them in final fields. |
| request record        | Immutable data carrier that groups all input for one service operation.                |
| package-private       | Default Java visibility, available only inside the same package.                       |
| pure function         | Output depends only on arguments and produces no observable side effects.              |
| dependency direction  | Rule describing which packages may depend on which lower-level packages.               |

## Testing

| Term                   | Meaning                                                                                                       |
|------------------------|---------------------------------------------------------------------------------------------------------------|
| unit test              | Focused test of one behavior with controlled dependencies.                                                    |
| integration test       | Test of several real collaborators working together.                                                          |
| subprocess test        | Launches a separate JVM to test actual startup, standard streams, and exit behavior.                          |
| fixture                | Reusable test setup or sample data.                                                                           |
| exact output assertion | Compares complete stable text including spaces and newlines.                                                  |
| global state cleanup   | Restores values such as `System.in` and `System.out` in `finally`.                                            |
| ragged rows            | Table rows with different column counts that a renderer must pad into a rectangle.                            |
| coverage               | Measurement of executed instructions, branches, lines, or methods; useful evidence, not proof of correctness. |

## Commands

```bash
mvn -Dcheckstyle.skip test
mvn verify
mvn compile
java -cp target/classes com.connorjensen.jobtracker.Main
```

Use the first command while the capstone refactor is structurally incomplete. Use the final three commands for completion.

## Answers to have cold

1. `mvn verify` runs every earlier default-lifecycle phase, including validation, compilation, tests, packaging, quality checks, and reporting configured in the POM.
2. A record is a good request carrier because its identity is its values; a mutable domain entity needs a class because its state changes over time.
3. Constructor injection exposes dependencies, prevents partially initialized objects, and makes collaborators replaceable in tests.
4. Input parsing belongs at the CLI boundary because the service should receive typed `LocalDate`, `Status`, and `Long` values.
5. A prompt catches only expected parse failures and retries locally; EOF ends the session instead of being treated as invalid input.
6. `Main` is a composition root when it only constructs dependencies and calls `run()`.
7. A pure table renderer returns a string, does not write global output, and does not mutate caller-owned collections.
8. Checkstyle is a reproducible rule gate, not an automatic formatter and not a substitute for code review.
9. Maven's directory layout is a build convention used by this course, not syntax required by Java.

**Chapter:** [Chapter 1 — Java Foundations](README.md) · **Version history:** [NOTES.md](NOTES.md)
