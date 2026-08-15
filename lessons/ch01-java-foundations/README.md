# Chapter 1 — Java Foundations

*No framework. Nothing but the language and a build tool.*

The goal of this chapter is that when Spring Boot shows up in Chapter 2, it looks like a **library doing things you already did by hand** rather than magic. Every piece of Spring you meet later has a manual ancestor in here.

**Capstone:** [The Tracker Core](CAPSTONE.md) — a working console job tracker, layered exactly like the finished app. Its model, repository interface and service survive to v1 unchanged.

---

## Lessons

| #  | Lesson                                                                  | You'll be able to answer                                             | Status |
|----|-------------------------------------------------------------------------|----------------------------------------------------------------------|--------|
| 0  | [Build system & project shape](00-build-system.md)                      | "Walk me through what happens when you run `mvn package`."           | ☑     |
| 1  | [Classes, objects, records](01-classes-and-records.md)                  | "When would you use a `record` instead of a class?"                  | ☑     |
| 2  | [Packages, enums, collections, Optional](02-types-enums-collections.md) | "What problem does `Optional` solve, and when is it the wrong tool?" | ☑     |
| 3  | [Interfaces & dependency injection](03-interfaces-and-di.md)            | "What is dependency injection and what does it buy you?"             | ☑     |
| ★ | [**Capstone — The Tracker Core**](CAPSTONE.md)                          | all of the above, about code you wrote                               | ☐     |

**Tracking progress:** every lesson opens with a **Progress** checklist mirroring its steps, and carries a `- [ ]` on each thing you actually do — create a file, run a command, observe an output. Tick them in the file as you go; the table above is the chapter-level view of the same thing.

**Reference:** [GLOSSARY.md](GLOSSARY.md) — every term in the chapter with its Python/JS equivalent, plus the five answers you should have cold.

**Side reading:** [NOTES.md](NOTES.md) — what each feature replaced and why it changed. Lessons link here with tags like <sup>[J16](NOTES.md#records)</sup>. Optional, and aimed at working in codebases pinned to Java 8.

**XP:** banked once, after the capstone — not per lesson.

---

## What exists at the end of this chapter

```
src/main/java/com/connorjensen/jobtracker
├── Main.java                                  console loop + hand-wired object graph
├── model/
│   ├── Application.java                       the domain class
│   ├── Status.java                            the enum
│   └── (Interview.java — Chapter 3)
├── repository/
│   ├── ApplicationRepository.java             the interface ★
│   └── InMemoryApplicationRepository.java     the throwaway implementation
├── service/
│   └── ApplicationService.java                business rules ★
└── dto/
    └── ApplicationDto.java                    (reworked in Chapter 2)
```

★ = survives to v1 essentially unchanged.

---

## The through-line

Each lesson sets up a piece of framework machinery you'll meet later:

| You do this manually                           | Later, this does it for you                                  |
|------------------------------------------------|--------------------------------------------------------------|
| Hand-write `pom.xml`, see the classpath        | start.spring.io + Spring Boot's dependency BOM               |
| `@Test` as metadata something scans for        | Spring scanning for `@Service`, `@RestController`, `@Entity` |
| `Application` class vs `ApplicationDto` record | JPA entity vs request/response DTO (Ch. 2–3)                 |
| `Status.valueOf` in a try/catch                | Spring binding `?status=OFFER`, returning 400 on garbage     |
| `Optional<Application> findById`               | Spring Data's `findById` — same signature                    |
| `findByStatus` written by hand                 | Spring Data deriving the SQL from the method name            |
| `new ApplicationService(repository)` in `main` | the application context building the same graph              |
| `final` field set in the constructor           | constructor injection, the recommended Spring style          |
| Writing a fake repository in a test            | Mockito generating it (Ch. 4)                                |

---

**Next chapter:** Chapter 2 — Spring Boot and the HTTP layer. Same object graph, wired by the container, reachable over HTTP.
