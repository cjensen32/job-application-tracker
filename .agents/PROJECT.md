# Job Application Tracker — Completed Chapter 1 Scope

Status: completed and closed at the plain-Java Chapter 1 boundary.

## Purpose

This repository records a learner-built Java foundation project: a command-line job application tracker that demonstrates object modeling, collections, interfaces, constructor injection, service boundaries, console input validation, deterministic output, tests, and Maven quality gates.

Advanced development continues in a separate project. This repository has no pending framework, database, API, security, frontend, deployment, or automation milestone.

## Delivered behavior

- Create an application with company, role, applied date, optional notes, and optional absolute HTTP(S) job URL.
- List every application in a deterministic text table.
- Filter applications by one of five statuses: `APPLIED`, `PHONE_SCREEN`, `INTERVIEWING`, `OFFER`, and `REJECTED`.
- Edit all fields, preserve required values on blank input, and clear optional values with `-`.
- Delete by positive ID and report missing IDs without crashing.
- Retry invalid menu, ID, date, status, and URL input locally.
- Exit cleanly on option `5` or EOF without saving partial create/edit input.

## Technical architecture

```text
src/main/java/com/connorjensen/jobtracker/
├── Main.java                         composition root
├── cli/                              session coordination, prompts, output, table rendering
├── dto/                              record-versus-class learning example
├── model/                            mutable Application and Status enum
├── repository/                       storage contract and in-memory implementation
└── service/                          application operations and request records
```

Dependency direction is `Main -> cli -> service -> repository/model`. `Main` wires dependencies; `ConsoleApplication` coordinates; `ConsolePrompter` owns parsing and retries; `ConsoleView` owns output; `TextTable` is a pure renderer; `ApplicationService` depends on the repository interface; `InMemoryApplicationRepository` assigns IDs and stores values.

## Build contract

- Java 21.
- Maven with pinned JUnit 5-capable Surefire.
- JUnit 5 tests, including the installed Chapter 1 capstone grader.
- Checkstyle and Spotless at validation.
- JaCoCo reporting and the Chapter 1 console-method coverage check.

## Definition of done

- `mvn verify` passes 49 tests with zero failures, errors, or skips and all quality gates green.
- `mvn compile` succeeds.
- The real `Main` composition root starts, prints the complete menu, handles clean quit and EOF, and exits successfully.
- Learner progress and the capstone completion record agree.
- The tracked tree contains only the completed Chapter 1 code, learning surface, and consolidated agent backbone.

## Explicit non-goals

Spring, HTTP endpoints, persistence, authentication, a browser UI, scheduled automation, and interview-specific extensions are intentionally absent. Chapter 1 mentions some of these only to explain why its interfaces and boundaries matter; those comparisons are not a roadmap for this repository.
