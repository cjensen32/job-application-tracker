# Learning Plan — Job Application Tracker

A lesson track that builds [PROJECT.md](PROJECT.md) one concept at a time.

## How this works

- **Calibration:** One university Java course plus real React/TSX and Flask/uvicorn project experience.
- **Explain before automating:** Use the manual Java mechanism before the framework feature that replaces it.
- **Revisit, do not restart:** Apply each concept to the same tracker instead of building disposable exercises.
- **No throwaway work:** Every capstone builds a real project layer; temporary adapters have named replacements.
- **Course conventions:** [`lessons/course-standards/README.md`](lessons/course-standards/README.md) defines project layout, dependency direction, tests, and quality gates.

Each chapter contains lessons, a glossary, optional version notes, a capstone contract, and an installable grader. A standalone interlude may extend the project between chapter capstones without adding another capstone or XP gate. [`lessons/AUTHORING.md`](lessons/AUTHORING.md) defines how those artifacts are written.

Sub-lessons teach with small examples and self-checks. Capstones contain most of the typing and are graded by JUnit without requiring the learner to read the grader. XP is banked once after each capstone.

Tests begin in Chapter 1. Chapter 4 is the dedicated test-writing and robustness chapter: it formalizes maintainable test structure, JUnit design, mocking, Spring test slices, and PROJECT.md Milestone 7.

## Chapter 1 — Java Foundations *(no framework)*

📁 [`lessons/ch01-java-foundations/`](lessons/ch01-java-foundations/README.md)

*Goal: understand enough Java, design, console I/O, testing, and build discipline that Spring looks like replaceable machinery rather than magic.*

| # | Lesson                                                                                                             | Concepts                                                                    |
|---|--------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------|
| 0 | [Build system and project shape](lessons/ch01-java-foundations/00-build-system.md)                                 | POM, coordinates, scopes, classpath, lifecycle, bytecode                    |
| 1 | [Classes, objects, and records](lessons/ch01-java-foundations/01-classes-and-records.md)                           | constructors, static members, beans, records, equality                      |
| 2 | [Packages, enums, collections, and Optional](lessons/ch01-java-foundations/02-types-enums-collections.md)          | packages, enums, generics, collections, streams, Optional                   |
| 3 | [Interfaces and dependency injection](lessons/ch01-java-foundations/03-interfaces-and-di.md)                       | contracts, constructor injection, final fields, composition roots           |
| 4 | [Control flow and exception boundaries](lessons/ch01-java-foundations/04-control-flow-and-exception-boundaries.md) | loops, switch dispatch, guards, parsing, retries, EOF                       |
| 5 | [Console I/O and validation](lessons/ch01-java-foundations/05-console-io-and-validation.md)                        | Scanner, PrintStream, UTF-8, text blocks, dates, enums, IDs, URI            |
| 6 | [Refactoring and package design](lessons/ch01-java-foundations/06-refactoring-and-package-design.md)               | cohesion, visibility, injection, request records, package dependencies      |
| 7 | [Testing console applications](lessons/ch01-java-foundations/07-testing-console-applications.md)                   | injected streams, subprocesses, exact output, cleanup, edge cases, coverage |
| 8 | [Java quality standards](lessons/ch01-java-foundations/08-java-quality-standards.md)                               | Maven conventions, names, imports, formatting, Checkstyle, warnings         |

**★ Capstone — [The Tracker Core](lessons/ch01-java-foundations/CAPSTONE.md):** a complete plain-Java CRUD tracker with a role-specific CLI, request records, validation boundaries, deterministic output, and a pure text renderer.

*Survives to v1:* the model, repository contract, service, request shapes, and testing habits. *Replaced later:* the console layer, in-memory storage, and manual wiring.

## Interlude — Reliable Automation *(after the Chapter 1 capstone)*

📁 [`lessons/interlude-reliable-automation/`](lessons/interlude-reliable-automation/README.md)

*Goal: extend the plain-Java tracker with trustworthy data aggregation, then design an auditable scheduled workflow before choosing Spring or cloud infrastructure.*

| #  | Lesson                                                                                                    | Concepts                                                                                 |
|----|-----------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| 9  | [Data processing and reconciliation](lessons/interlude-reliable-automation/09-data-processing-and-reconciliation.md) | grouping, aggregation, zero categories, reconciliation, defensive copies, SQL translation, learner-written tests |
| 10 | [Designing reliable automation workflows](lessons/interlude-reliable-automation/10-reliable-automation-workflows.md) | intake, failure models, retries, idempotency, auditability, AWS and RPA decision boundaries |

There is no interlude capstone or separate XP award. Lesson 9 produces a tested dashboard summary seam; Lesson 10 produces the design for the stale-application reminder stretch goal.

## Chapter 2 — Spring Boot and the HTTP Layer *(PROJECT.md Milestones 1–2)*

*Goal: watch the framework perform the same wiring and boundary work, then expose it over HTTP.*

| #  | Lesson                               | Concepts                                                                             |
|----|--------------------------------------|--------------------------------------------------------------------------------------|
| 11 | Annotations and the Spring container | metadata, reflection, application context, scanning, auto-configuration, Boot plugin |
| 12 | The HTTP layer                       | controllers, mappings, path/query parameters, request bodies, Jackson, status codes  |
| 13 | DTOs and validation                  | request/response records, Bean Validation, mapping, API boundaries                   |

**★ Capstone — The Tracker API:** expose full CRUD at `/api/applications` with validated DTOs while retaining the in-memory repository.

## Chapter 3 — Persistence *(Milestones 1, 3, 4)*

*Goal: swap storage without changing the layers above it, then query relationships correctly.*

| #  | Lesson                             | Concepts                                                                            |
|----|------------------------------------|-------------------------------------------------------------------------------------|
| 14 | JPA and Postgres                   | ORM tradeoffs, entities, persistence context, Hibernate, Spring Data, configuration |
| 15 | Filtering, sorting, and pagination | derived queries, Pageable, Page, optional query parameters                          |
| 16 | Entity relationships               | one-to-many, owning side, foreign keys, cascade, fetching, N+1, nested resources    |

**★ Capstone — The Tracker, Persisted:** run the same API on Postgres, add `Interview`, query by status, and observe then fix N+1 behavior.

## Chapter 4 — Test Writing and Robustness *(Milestones 5, 7)*

*Goal: write tests that communicate behavior, isolate the right boundaries, diagnose failures clearly, and remain useful as the application changes.*

| #  | Lesson                            | Concepts                                                                                               |
|----|-----------------------------------|--------------------------------------------------------------------------------------------------------|
| 17 | Exceptions and error handling     | checked/unchecked exceptions, custom types, controller advice, error bodies                            |
| 18 | Writing maintainable unit tests   | JUnit lifecycle, arrange/act/assert, fixtures, parameterization, assertion quality, Mockito boundaries |
| 19 | Integration tests and test slices | WebMvcTest, MockMvc, DataJpaTest, focused contexts, subprocesses, error-contract tests                 |

**★ Capstone — Hardened:** map `ApplicationNotFoundException` to a consistent 404 and cover controller, service, and repository behavior.

## Chapter 5 — Security *(Milestone 6)*

| #  | Lesson                     | Concepts                                                             |
|----|----------------------------|----------------------------------------------------------------------|
| 20 | Users and password hashing | user model, UserDetailsService, BCrypt, registration                 |
| 21 | JWT and the filter chain   | token structure, signatures, filters, stateless auth versus sessions |
| 22 | Scoping data to a user     | security context, query ownership, isolation failures                |

**★ Capstone — Multi-user:** add registration, login, JWT validation, and per-user application isolation.

## Chapter 6 — Frontend *(Milestone 8)*

| #  | Lesson                  | Concepts                                          |
|----|-------------------------|---------------------------------------------------|
| 23 | Wiring React to the API | CORS, loading/error states, real query parameters |
| 24 | Auth in the browser     | attaching JWTs, storage tradeoffs, 401 handling   |

**★ Capstone — v1:** deliver list/filter, add/edit, delete, and login screens against the completed API.

## Progress

| Ch | #  | Lesson                                                                                                             | Milestone | Status |
|----|----|--------------------------------------------------------------------------------------------------------------------|-----------|--------|
| 1  | 0  | [Build system and project shape](lessons/ch01-java-foundations/00-build-system.md)                                 | —         | ☑     |
| 1  | 1  | [Classes, objects, and records](lessons/ch01-java-foundations/01-classes-and-records.md)                           | —         | ☑     |
| 1  | 2  | [Packages, enums, collections, and Optional](lessons/ch01-java-foundations/02-types-enums-collections.md)          | —         | ☑     |
| 1  | 3  | [Interfaces and dependency injection](lessons/ch01-java-foundations/03-interfaces-and-di.md)                       | —         | ☑     |
| 1  | 4  | [Control flow and exception boundaries](lessons/ch01-java-foundations/04-control-flow-and-exception-boundaries.md) | —         | ☐     |
| 1  | 5  | [Console I/O and validation](lessons/ch01-java-foundations/05-console-io-and-validation.md)                        | —         | ☐     |
| 1  | 6  | [Refactoring and package design](lessons/ch01-java-foundations/06-refactoring-and-package-design.md)               | —         | ☐     |
| 1  | 7  | [Testing console applications](lessons/ch01-java-foundations/07-testing-console-applications.md)                   | 7         | ☐     |
| 1  | 8  | [Java quality standards](lessons/ch01-java-foundations/08-java-quality-standards.md)                               | —         | ☐     |
| 1  | ★ | [**Capstone — The Tracker Core**](lessons/ch01-java-foundations/CAPSTONE.md)                                       | —         | ☐     |
| I  | 9  | [Data processing and reconciliation](lessons/interlude-reliable-automation/09-data-processing-and-reconciliation.md) | —         | ☐     |
| I  | 10 | [Designing reliable automation workflows](lessons/interlude-reliable-automation/10-reliable-automation-workflows.md) | —         | ☐     |
| 2  | 11 | Annotations and the Spring container                                                                               | 1         | ☐     |
| 2  | 12 | The HTTP layer                                                                                                     | 2         | ☐     |
| 2  | 13 | DTOs and validation                                                                                                | 2         | ☐     |
| 2  | ★ | **Capstone — The Tracker API**                                                                                     | 1–2       | ☐     |
| 3  | 14 | JPA and Postgres                                                                                                   | 1         | ☐     |
| 3  | 15 | Filtering, sorting, and pagination                                                                                 | 3         | ☐     |
| 3  | 16 | Entity relationships                                                                                               | 4         | ☐     |
| 3  | ★ | **Capstone — The Tracker, Persisted**                                                                              | 1, 3, 4   | ☐     |
| 4  | 17 | Exceptions and error handling                                                                                      | 5         | ☐     |
| 4  | 18 | Writing maintainable unit tests                                                                                    | 7         | ☐     |
| 4  | 19 | Integration tests and test slices                                                                                  | 7         | ☐     |
| 4  | ★ | **Capstone — Hardened**                                                                                            | 5, 7      | ☐     |
| 5  | 20 | Users and password hashing                                                                                         | 6         | ☐     |
| 5  | 21 | JWT and the filter chain                                                                                           | 6         | ☐     |
| 5  | 22 | Scoping data to a user                                                                                             | 6         | ☐     |
| 5  | ★ | **Capstone — Multi-user**                                                                                          | 6         | ☐     |
| 6  | 23 | Wiring React to the API                                                                                            | 8         | ☐     |
| 6  | 24 | Auth in the browser                                                                                                | 8         | ☐     |
| 6  | ★ | **Capstone — v1**                                                                                                  | 8         | ☐     |
