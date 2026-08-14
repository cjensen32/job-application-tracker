# Learning Plan — Job Application Tracker

A lesson track that builds [PROJECT.md](PROJECT.md) one concept at a time.

## How this works

**Calibration:** One Java course completed (academic exercises, never a full app). Real project
experience in React/TSX and Python Flask/uvicorn — so HTTP, REST, JSON, and component wiring are
already understood. The gap is *Java's way of saying those things*, plus the Spring framework layer.

**Explain before automating.** The first time any tool or concept appears, it gets explained and done
by hand before it gets automated. Hand-written `pom.xml` before start.spring.io; manual constructor
wiring before `@Autowired`; a console loop before `@RestController`.

**Revisit, don't restart.** New concepts get applied to code that already exists. Lesson 2 moves and
re-packages the classes from Lesson 1; Chapter 3 swaps the storage under Chapter 1's service without
editing it. Coming back to old code with a new idea is the point, not a detour.

**No throwaway work.** Every capstone builds a piece of the real project. Where something *is*
temporary (the in-memory repository, the console UI), the lesson says up front what replaces it and
why the interface around it survives.

---

## Chapter structure

Each chapter is a folder with sub-lessons, a glossary, and a capstone:

```
lessons/ch01-java-foundations/
├── README.md                       chapter map + what survives to v1
├── 00-build-system.md              sub-lesson
├── 01-classes-and-records.md       sub-lesson
├── 02-...                          sub-lesson
├── GLOSSARY.md                     terms + the answers to have cold
├── NOTES.md                        version history — what each feature replaced, and why
├── CAPSTONE.md                     the assignment — exact names and behaviour
└── capstone/
    └── ChapterNNCapstoneTest.java  the grader. Copied into src/test/, not read.
```

How chapters get authored is specified in [`lessons/AUTHORING.md`](lessons/AUTHORING.md) — a living
document, revised whenever something works badly.

**Sub-lessons** teach a concept, with a small amount of guided code. **Capstones** are where the code
gets written — specified in prose in `CAPSTONE.md`, graded by a JUnit file that is deliberately not
read. `CAPSTONE.md` names every class, method signature and behaviour the test checks, so peeking is
never necessary; the test file just tells you when you're done.

**Moving the goalposts is allowed.** If a capstone gets solved in a different but valid direction, or
lands too easy or too hard, the test file and the instructions get rewritten to match. The target is
a working project, not a specific keystroke sequence.

**Every sub-lesson ends with** self-check questions (answers collapsed) and an interview question
you'll be able to answer about code you wrote. **XP is banked once per chapter, after the capstone** —
`/code-sensei:quiz` covers the whole chapter's concepts at once, so the reward is for having built
the thing rather than for having read about it. A concept counts as mastered at 3 correct.

**Version-history notes.** Java's backward compatibility is strict enough that "x became y because z"
is unusually well-documented, and it's directly useful for working in older codebases. That material
stays out of the main content — lessons carry only a tag like <sup>[J8]</sup> linking to the
chapter's `NOTES.md`.

**Testing note:** PROJECT.md lists tests as Milestone 7. Here, tests exist from Lesson 1 and get their
formal deep-dive in Chapter 4. Writing tests alongside the code beats bolting them on at the end.

---

## Chapter 1 — Java Foundations *(no framework at all)*

📁 [`lessons/ch01-java-foundations/`](lessons/ch01-java-foundations/README.md)

*Goal: understand the language well enough that Spring looks like a library, not magic.*

| # | Lesson | Concepts |
|---|--------|----------|
| 0 | [Build system & project shape](lessons/ch01-java-foundations/00-build-system.md) | `pom.xml`, coordinates, scopes, classpath, the Maven lifecycle, bytecode |
| 1 | [Classes, objects, records](lessons/ch01-java-foundations/01-classes-and-records.md) | no free-floating functions, constructors, `static`, JavaBean convention, `record`, value vs identity equality |
| 2 | [Packages, enums, collections, Optional](lessons/ch01-java-foundations/02-types-enums-collections.md) | package/import as the only visibility mechanism, layered package layout, `enum`, generics, `List`/`Map`, streams, `Optional` |
| 3 | [Interfaces & dependency injection](lessons/ch01-java-foundations/03-interfaces-and-di.md) | interface vs implementation, constructor injection by hand, `final` fields, the composition root, why this is what makes tests possible |

**★ Capstone — [The Tracker Core](lessons/ch01-java-foundations/CAPSTONE.md):** a console-driven
tracker with `Application`/`Status`, an `ApplicationRepository` interface, an in-memory
implementation, and an `ApplicationService` wired by hand in `main`. Add, list, filter by status,
update status, delete.

*Survives to v1:* the model, the repository interface, the service. *Replaced later:* the console
loop (→ HTTP), the in-memory store (→ Postgres), the manual wiring (→ the Spring container).

---

## Chapter 2 — Spring Boot and the HTTP Layer *(PROJECT.md Milestones 1–2)*

*Goal: watch the framework do exactly what you just did manually, then get it onto the network.*

| # | Lesson | Concepts |
|---|--------|----------|
| 4 | Annotations & the Spring container | what an annotation *is* (metadata + reflection), the application context, `@Component`/`@Service`/`@Repository`, component scanning, `@SpringBootApplication`, auto-configuration, the Spring Boot Maven plugin finally making `java -jar` work |
| 5 | The HTTP layer | `@RestController`, `@GetMapping`/`@PostMapping` (≈ `@app.route`), path variables and query params, `@RequestBody`, Jackson (≈ `jsonify`, automatic), `ResponseEntity` and picking status codes |
| 6 | DTOs & validation | why exposing domain objects over HTTP is a smell, request vs response DTOs as `record`s, Bean Validation (`@NotBlank`, `@Valid`), where mapping code belongs |

**★ Capstone — The Tracker API:** Chapter 1's object graph, wired by the container and reachable over
HTTP. Full CRUD on `/api/applications` with validated DTOs and correct status codes, still backed by
the in-memory repository. Graded by a test file plus a curl script.

*The point:* the service and repository from Chapter 1 are not edited. Only the layer above changes.

---

## Chapter 3 — Persistence *(Milestones 1, 3, 4)*

*Goal: swap storage without the layers above noticing, then query it properly.*

| # | Lesson | Concepts |
|---|--------|----------|
| 7 | JPA & Postgres | what an ORM does and what it costs, `@Entity`/`@Id`/`@GeneratedValue`, JPA (spec) vs Hibernate (implementation) vs Spring Data JPA (convenience) — a common interview trip-up, the persistence context and dirty checking, `JpaRepository`, `application.properties`, `ddl-auto` |
| 8 | Filtering, sorting, pagination | derived query methods in depth, `Pageable`/`Page<T>`, why pagination is correctness rather than optimization, optional query params |
| 9 | Entity relationships | `@OneToMany`/`@ManyToOne`, the owning side and where the FK lives, `cascade`, `LAZY` vs `EAGER`, the N+1 problem, nested resource URLs |

**★ Capstone — The Tracker, Persisted:** the same API against Postgres, with `Interview` as a child
entity, `POST /api/applications/{id}/interviews`, and
`GET /api/applications?status=INTERVIEWING&page=0&size=20&sort=appliedDate,desc`. SQL logging on, so
N+1 gets *watched* happening and then fixed.

*The payoff:* deleting `InMemoryApplicationRepository` should not require touching
`ApplicationService`.

---

## Chapter 4 — Making It Robust *(Milestones 5, 7)*

| # | Lesson | Concepts |
|---|--------|----------|
| 10 | Exceptions & error handling | checked vs unchecked (a genuine Java-ism with no Python equivalent), custom exception types, `@ControllerAdvice` + `@ExceptionHandler`, designing one consistent JSON error body |
| 11 | Unit testing & Mockito | the test pyramid, JUnit 5 lifecycle, mocking the repository to test the service — the direct payoff for Lesson 3, what to mock and what never to mock |
| 12 | Test slices | `@WebMvcTest` + MockMvc, `@DataJpaTest`, why slices beat booting the whole app, testing the error contract from Lesson 10 |

**★ Capstone — Hardened:** `ApplicationNotFoundException` → a clean 404 body, applied everywhere, plus
real coverage across all three layers.

---

## Chapter 5 — Security *(Milestone 6)*

*Hardest chapter, deliberately last on the backend — once everything else is stable and tested.*

| # | Lesson | Concepts |
|---|--------|----------|
| 13 | Users & password hashing | the `User` entity, `UserDetailsService`, BCrypt and why hashing isn't encryption, registration |
| 14 | JWT & the filter chain | what a JWT actually is (header/payload/signature — decodeable, **not** encrypted), the Spring Security filter chain and where your filter plugs in, stateless auth vs sessions |
| 15 | Scoping data to a user | getting the current user out of the security context, scoping every query, and the ways this goes wrong |

**★ Capstone — Multi-user:** `/api/auth/register` and `/api/auth/login`, a JWT filter, and an API where
you can only ever see your own applications.

---

## Chapter 6 — Frontend *(Milestone 8)*

*Light chapter — React is already known. The focus is the seam between two apps.*

| # | Lesson | Concepts |
|---|--------|----------|
| 16 | Wiring React to the API | CORS and why the browser blocks what curl didn't, loading/error states against a real API, driving the Lesson 8 query params from UI filters |
| 17 | Auth in the browser | attaching the JWT, where to store it and the tradeoff, handling 401s |

**★ Capstone — v1:** list view with filters, add/edit form, delete, login. **Definition of Done reached.**

---

## Progress

| Ch | # | Lesson | Milestone | Status |
|----|---|--------|-----------|--------|
| 1 | 0 | [Build system & project shape](lessons/ch01-java-foundations/00-build-system.md) | — | ☑ |
| 1 | 1 | [Classes, objects, records](lessons/ch01-java-foundations/01-classes-and-records.md) | — | ☑ |
| 1 | 2 | [Packages, enums, collections, Optional](lessons/ch01-java-foundations/02-types-enums-collections.md) | — | ☐ |
| 1 | 3 | [Interfaces & manual DI](lessons/ch01-java-foundations/03-interfaces-and-di.md) | — | ☐ |
| 1 | ★ | [**Capstone — The Tracker Core**](lessons/ch01-java-foundations/CAPSTONE.md) | — | ☐ |
| 2 | 4 | Annotations & the Spring container | 1 | ☐ |
| 2 | 5 | The HTTP layer | 2 | ☐ |
| 2 | 6 | DTOs & validation | 2 | ☐ |
| 2 | ★ | **Capstone — The Tracker API** | 1–2 | ☐ |
| 3 | 7 | JPA & Postgres | 1 | ☐ |
| 3 | 8 | Filtering, sorting, pagination | 3 | ☐ |
| 3 | 9 | Entity relationships | 4 | ☐ |
| 3 | ★ | **Capstone — The Tracker, Persisted** | 1, 3, 4 | ☐ |
| 4 | 10 | Exceptions & error handling | 5 | ☐ |
| 4 | 11 | Unit testing & Mockito | 7 | ☐ |
| 4 | 12 | Test slices | 7 | ☐ |
| 4 | ★ | **Capstone — Hardened** | 5, 7 | ☐ |
| 5 | 13 | Users & password hashing | 6 | ☐ |
| 5 | 14 | JWT & the filter chain | 6 | ☐ |
| 5 | 15 | Scoping data to a user | 6 | ☐ |
| 5 | ★ | **Capstone — Multi-user** | 6 | ☐ |
| 6 | 16 | Wiring React to the API | 8 | ☐ |
| 6 | 17 | Auth in the browser | 8 | ☐ |
| 6 | ★ | **Capstone — v1** | 8 | ☐ |
