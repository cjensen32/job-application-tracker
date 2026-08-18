# Chapter 1 — Java Foundations

*No production framework and no third-party runtime library.*

The chapter ends with a complete console CRUD application whose boundaries resemble the later Spring application. The model, repository contract, service, request shapes, and dependency direction survive; the console, memory store, and manual wiring are replaced in later chapters.

**Capstone:** [The Tracker Core](CAPSTONE.md)

## Lessons

| # | Lesson | Interview outcome | Status |
|---|--------|-------------------|--------|
| 0 | [Build system and project shape](00-build-system.md) | Explain `mvn package`, the classpath, and bytecode | ☑ |
| 1 | [Classes, objects, and records](01-classes-and-records.md) | Choose between mutable entities and value records | ☑ |
| 2 | [Packages, enums, collections, and Optional](02-types-enums-collections.md) | Explain typed absence, collections, and runtime enums | ☑ |
| 3 | [Interfaces and dependency injection](03-interfaces-and-di.md) | Explain constructor injection and substitutability | ☑ |
| 4 | [Control flow and exception boundaries](04-control-flow-and-exception-boundaries.md) | Place retries and exception handling at input boundaries | ☐ |
| 5 | [Console I/O and validation](05-console-io-and-validation.md) | Validate dates, statuses, IDs, and URLs consistently | ☐ |
| 6 | [Refactoring and package design](06-refactoring-and-package-design.md) | Split code by responsibility and control visibility | ☐ |
| 7 | [Testing console applications](07-testing-console-applications.md) | Test streams, sessions, EOF, and exact tables safely | ☐ |
| 8 | [Java quality standards](08-java-quality-standards.md) | Use Maven conventions and interpret quality-gate failures | ☐ |
| ★ | [**Capstone — The Tracker Core**](CAPSTONE.md) | Defend the whole design using code you wrote | ☐ |

Use [GLOSSARY.md](GLOSSARY.md) for quick recall, [NOTES.md](NOTES.md) for Java version history, [course standards](../course-standards/README.md) for repository-wide conventions, and [RECALIBRATION.md](RECALIBRATION.md) for the rationale and review history behind this expansion.

XP is banked once after the capstone quiz, not once per lesson.

## Target architecture

```text
src/main/java/com/connorjensen/jobtracker/
├── Main.java
├── cli/
│   ├── ConsoleApplication.java
│   ├── ConsolePrompter.java
│   ├── ConsoleView.java
│   └── TextTable.java
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

The Maven layout is a [course convention](../course-standards/README.md), not a rule of the Java language.

## Through-line

| Chapter 1 mechanism | Later replacement |
|---------------------|-------------------|
| Hand-written POM and classpath | Spring Boot dependency management and plugin |
| Request records | HTTP request DTOs in Lesson 11 |
| `Scanner` validation and retry | Spring binding and Bean Validation in Lessons 10–11 |
| Repository interface | Spring Data repository in Lesson 12 |
| Manual constructor wiring | Spring container in Lesson 9 |
| Console view | Controller plus JSON in Lesson 10 |
| In-memory repository | Postgres in Lesson 12 |
| Injected stream tests | MockMvc and test slices in Lesson 17 |

**Next chapter:** Spring Boot and the HTTP layer, beginning with Lesson 9.
