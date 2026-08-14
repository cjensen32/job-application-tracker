# Chapter 1 Capstone — The Tracker Core

**What you're building:** a working, console-driven job application tracker with an in-memory store,
layered exactly the way the finished Spring app will be layered.

**Why it matters:** none of this is throwaway. In Chapter 2 the console loop is replaced by HTTP
endpoints and the hand-wiring is replaced by annotations. In Chapter 3 the in-memory repository is
replaced by Postgres. **The model, the repository interface, and the service survive to v1
unchanged.** You are building the spine of the project.

**Progress** — tick these off as you go:

- [ ] Setup — capstone test installed, `mvn test` failing to compile
- [ ] `model.Status`
- [ ] `model.Application`
- [ ] `repository.ApplicationRepository`
- [ ] `repository.InMemoryApplicationRepository`
- [ ] `service.ApplicationService`
- [ ] `Main` — the console loop
- [ ] Done when: `mvn test` green **and** the app runs
- [ ] `/code-sensei:quiz` — the chapter's XP gate

---

## Rules

1. **Do not open `capstone/Chapter01CapstoneTest.java`.** Everything it checks is specified below,
   down to method signatures. If a test fails, read the failure message — they're written to tell you
   what's wrong without showing you the answer.
2. Use only Chapter 1 material. No Spring, no libraries beyond JUnit.
3. Working code first, elegant code second. You can refactor after it's green.

---

## Setup

- [ ] Install the capstone test and run it

```bash
mkdir -p src/test/java/com/connorjensen/jobtracker/capstone
cp lessons/ch01-java-foundations/capstone/Chapter01CapstoneTest.java \
   src/test/java/com/connorjensen/jobtracker/capstone/
mvn test
```

It will fail to compile. That's your starting line — the compiler errors are a to-do list.

---

## The contract

Names and signatures must match exactly, because the test compiles against them. This is not
pedantry: in Chapter 3, Spring Data JPA generates a repository with *these* method names, so getting
them right now is what makes the swap free later.

### `com.connorjensen.jobtracker.model.Status`

- [ ] Written

An `enum` with exactly five constants:

```
APPLIED, PHONE_SCREEN, INTERVIEWING, OFFER, REJECTED
```

### `com.connorjensen.jobtracker.model.Application`

- [ ] Written

A class (not a record — Lesson 1 explains why).

| Member                                                       | Notes                                       |
|--------------------------------------------------------------|---------------------------------------------|
| `Application(String company, String role, LocalDate appliedDate)` | Must default `status` to `APPLIED` and leave `id` null |
| `Long getId()` / `void setId(Long id)`                        | `Long`, nullable — null means "not saved"   |
| `String getCompany()`                                         | no setter                                   |
| `String getRole()` / `void setRole(String role)`              |                                             |
| `LocalDate getAppliedDate()`                                  |                                             |
| `Status getStatus()` / `void setStatus(Status status)`        |                                             |
| `String getNotes()` / `void setNotes(String notes)`           | not a constructor argument                  |
| `String getJobUrl()` / `void setJobUrl(String jobUrl)`        | not a constructor argument                  |
| `String summary()`                                            | yours to format; only `Main` uses it        |

### `com.connorjensen.jobtracker.repository.ApplicationRepository`

- [ ] Written

The interface from Lesson 3, unchanged:

```java
Application save(Application application);
List<Application> findAll();
Optional<Application> findById(Long id);
List<Application> findByStatus(Status status);
boolean deleteById(Long id);
```

### `com.connorjensen.jobtracker.repository.InMemoryApplicationRepository`

`implements ApplicationRepository`, with a **no-argument constructor** (the default one is fine).
Required behaviour — one box per method:

- [ ] `save` — if `getId()` is null, assign the next id and store it; **ids start at 1 and increment**.
  If the id is already set, overwrite the existing entry rather than adding a new one. Returns the
  application either way.
- [ ] `findAll` — every stored application. **Never null**; empty list when empty.
- [ ] `findById` — `Optional.of(...)` when present, `Optional.empty()` when not. Never null.
- [ ] `findByStatus` — only matching applications; empty list when none match.
- [ ] `deleteById` — `true` if something was removed, `false` if that id wasn't there. Deleting the
  same id twice gives `true` then `false`.

### `com.connorjensen.jobtracker.service.ApplicationService`

- [ ] Constructor + `create` + `listAll` + `findById`
- [ ] `listByStatus` + `updateStatus` (throwing) + `delete`

| Member                                                                  | Behaviour                                                                 |
|-------------------------------------------------------------------------|---------------------------------------------------------------------------|
| `ApplicationService(ApplicationRepository repository)`                   | Constructor injection. **Must not construct a repository itself.**         |
| `Application create(String company, String role, LocalDate appliedDate)` | Builds an `Application` and saves it; returns the saved one, id included   |
| `List<Application> listAll()`                                            | delegates to `findAll`                                                     |
| `Optional<Application> findById(Long id)`                                | delegates                                                                  |
| `List<Application> listByStatus(Status status)`                          | delegates to `findByStatus`                                                |
| `Application updateStatus(Long id, Status status)`                       | Loads, sets status, saves, returns it. **Throws `IllegalArgumentException` if the id doesn't exist.** |
| `boolean delete(Long id)`                                                | delegates to `deleteById`                                                  |

> The field must be typed `ApplicationRepository`, not `InMemoryApplicationRepository` — one test
> hands the service a completely different implementation and checks the service uses it.

> `updateStatus` throwing `IllegalArgumentException` is a placeholder. Chapter 4 replaces it with a
> custom `ApplicationNotFoundException` that becomes a clean HTTP 404. Leaving a deliberate seam here
> is intentional.

---

## The part the test doesn't check: `Main`

The test covers the three layers below the UI. The console loop is yours to verify by running it —
which is the honest division, since that's exactly the layer that gets thrown away and replaced by
HTTP in Chapter 2.

Rewrite `Main.main` to wire the object graph and then loop on `Scanner` input:

```java
Scanner scanner = new Scanner(System.in);
```

Support at least these commands — one box each:

- [ ] `add` — prompts for company, role, date; creates it
- [ ] `list` — prints all applications with id, company, role, status
- [ ] `list INTERVIEWING` — prints only that status
- [ ] `status <id> <STATUS>` — updates one application's status
- [ ] `delete <id>` — removes it
- [ ] `quit` — exits

Two things to get right, because they're really input validation in disguise:

- [ ] `Status.valueOf(text.toUpperCase())` throws on garbage. Catch `IllegalArgumentException` and print
  something helpful rather than letting the app die. In Chapter 2, Spring performs this exact
  conversion on `?status=INTERVIEWING` and returns a 400 when it fails — you're writing the manual
  version of a thing you'll later get for free.
- [ ] `LocalDate.parse("2026-08-01")` reads ISO dates and throws `DateTimeParseException` otherwise.

---

## Done when

- [ ] Tests green

```bash
mvn test
```

...is green (your Lesson 1 test plus all of the capstone's), **and**

- [ ] you can run the app and add, list, filter, update and delete without it crashing:

```bash
mvn compile
java -cp target/classes com.connorjensen.jobtracker.Main
```

---

## Stretch — only if you want more reps

These are all real project features, not busywork:

- [ ] **Sort `list` by `appliedDate`, newest first.** `Comparator.comparing(...).reversed()` on a
  stream. You'll re-implement this as `?sort=appliedDate,desc` in Chapter 3.
- [ ] **`search <text>`** — case-insensitive match against company or role. Becomes a derived query
  method later.
- [ ] **`stats`** — count by status. `Collectors.groupingBy(Application::getStatus, Collectors.counting())`
  returns a `Map<Status, Long>`. This is the dashboard from PROJECT.md's stretch goals.

---

## The interview questions this earns you

Be able to answer these about **code you wrote**, not in the abstract:

- [ ] *"What is dependency injection and what does it buy you?"*
- [ ] *"Why is your repository an interface when there's only one implementation?"*
- [ ] *"When would you use a record instead of a class?"*
- [ ] *"What does `Optional` solve, and when is it the wrong tool?"*
- [ ] *"Walk me through what happens when you run `mvn package`."*

---

## What Chapter 2 does to this code

So you can see nothing is wasted:

| This chapter                              | Chapter 2                                            |
|-------------------------------------------|------------------------------------------------------|
| `new ApplicationService(repository)` in `Main` | `@Service` + component scanning — same object graph |
| `Scanner` command loop                    | `@RestController` with `GET`/`POST` endpoints        |
| Printing `summary()` to stdout            | Jackson serializing to JSON                          |
| `Status.valueOf` in a try/catch           | Spring's parameter binding, returning 400            |
| `ApplicationDto` from Lesson 1            | a real request/response DTO with validation          |
| `IllegalArgumentException`                | `ApplicationNotFoundException` → 404 (Chapter 4)     |

- [ ] **Now run `/code-sensei:quiz`.** This is the chapter's XP gate — it covers Lessons 0–3
  together, banked for having built the thing rather than for having read about it.

Then tell me how the difficulty landed; the next chapter's capstone gets tuned from there.

---

**Chapter:** [Chapter 1 — Java Foundations](README.md) · **Glossary:** [GLOSSARY.md](GLOSSARY.md) ·
**Version history:** [NOTES.md](NOTES.md)
