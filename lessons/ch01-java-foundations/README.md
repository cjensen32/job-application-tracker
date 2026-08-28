# Chapter 1 — Java Foundations

The completed chapter builds a full CRUD console application without a production framework or third-party runtime library. The model, repository contract, service, request shapes, dependency direction, and testing habits are durable; the console, memory store, and manual wiring deliberately expose mechanisms that larger systems often automate.

**Capstone:** [The Tracker Core](CAPSTONE.md)

## Lessons

| #  | Lesson                                                                               | Interview outcome                                         | Status |
|----|--------------------------------------------------------------------------------------|-----------------------------------------------------------|--------|
| 0  | [Build system and project shape](00-build-system.md)                                 | Explain Maven lifecycle, classpath, and bytecode          | ☑     |
| 1  | [Classes, objects, and records](01-classes-and-records.md)                           | Choose between mutable entities and value records         | ☑     |
| 2  | [Packages, enums, collections, and Optional](02-types-enums-collections.md)          | Explain typed absence, collections, and runtime enums     | ☑     |
| 3  | [Interfaces and dependency injection](03-interfaces-and-di.md)                       | Explain constructor injection and substitutability        | ☑     |
| 4  | [Control flow and exception boundaries](04-control-flow-and-exception-boundaries.md) | Place retries and exception handling at input boundaries  | ☑     |
| 5  | [Console I/O and validation](05-console-io-and-validation.md)                        | Validate dates, statuses, IDs, and URLs consistently      | ☑     |
| 6  | [Refactoring and package design](06-refactoring-and-package-design.md)               | Split code by responsibility and control visibility       | ☑     |
| 7  | [Testing console applications](07-testing-console-applications.md)                   | Test streams, sessions, EOF, and exact tables safely      | ☑     |
| 8  | [Java quality standards](08-java-quality-standards.md)                               | Use Maven conventions and interpret quality-gate failures | ☑     |
| ★ | [The Tracker Core](CAPSTONE.md)                                                      | Defend the complete design using learner-owned code       | ☑     |

Use [GLOSSARY.md](GLOSSARY.md) for quick recall, [NOTES.md](NOTES.md) for Java version history, [COURSE_STANDARDS.md](COURSE_STANDARDS.md) for project conventions, [TESTING_STANDARDS.md](TESTING_STANDARDS.md) for test design, and [RECALIBRATION.md](RECALIBRATION.md) for the chapter's revision history.

## Final architecture

```text
src/main/java/com/connorjensen/jobtracker/
├── Main.java
├── cli/
│   ├── ConsoleApplication.java
│   ├── ConsolePrompter.java
│   ├── ConsoleView.java
│   └── TextTable.java
├── dto/
│   └── ApplicationDto.java
├── model/
│   ├── Application.java
│   └── Status.java
├── repository/
│   ├── ApplicationRepository.java
│   └── InMemoryApplicationRepository.java
└── service/
    ├── ApplicationService.java
    ├── CreateApplicationRequest.java
    └── UpdateApplicationRequest.java
```

`Main` is only the composition root. `ConsoleApplication` coordinates a session, `ConsolePrompter` owns input and local retries, `ConsoleView` owns output, and `TextTable` is a pure renderer.

## Through-line

| Chapter 1 mechanism                   | What the boundary enables elsewhere                            |
|---------------------------------------|----------------------------------------------------------------|
| Hand-written POM and classpath        | Build tooling can later automate dependency and packaging work |
| Request records                       | Another input boundary can reuse explicit command shapes       |
| `Scanner` validation and retry        | A web or GUI adapter can replace console parsing               |
| Repository interface                  | Persistent storage can replace the in-memory implementation    |
| Manual constructor wiring             | A dependency-injection container can construct the same graph  |
| Console view                          | Another presentation adapter can replace formatted text        |
| Injected streams and subprocess tests | Other boundary tests can reuse the same isolation principles   |

## Completion

Chapter 1 is complete: 49 tests and every Maven quality gate pass, production compiles, the real composition root runs, clean quit and EOF terminate successfully, and the capstone quiz is recorded complete.

**Course:** [Chapter 1 learning record](../README.md) · **Capstone:** [The Tracker Core](CAPSTONE.md)
