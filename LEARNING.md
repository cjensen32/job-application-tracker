# Learning Plan — Job Application Tracker

A lesson track that builds [PROJECT.md](PROJECT.md) one concept at a time.

## How this works

**Calibration:** One Java course completed (academic exercises, never a full app). Real project experience in React/TSX
and Python Flask/uvicorn — so HTTP, REST, JSON, and component wiring are already understood. The gap is *Java's way of
saying those things*, plus the Spring framework layer.

**Format:** Mixed. Boilerplate gets scaffolded, but **the first time any tool or concept appears, it gets explained
before it gets automated.** You will hand-write a minimal `pom.xml` before letting start.spring.io generate one. You
will hand-wire a dependency before `@Autowired` does it for you. The point is that no part of the final app is magic.

**Every lesson has four parts:**

1. **Concept goals** — what you should be able to explain afterward
2. **Build goal** — something that actually runs at the end
3. **Interview check** — the question a real interviewer asks about this
4. **Quiz gate** — `/code-sensei:quiz` before moving on; a concept counts as mastered at 3 correct

**Testing note:** PROJECT.md lists tests as Milestone 7. This plan starts writing tests at Lesson 5 and does the formal
deep-dive at Lesson 11. Writing tests alongside the code is both better practice and better learning than bolting them
on at the end.

---

## Phase 1 — Java, without any framework

*Goal: understand the language well enough that Spring looks like a library, not magic.*

### Lesson 0 — Build system and project shape

Before any code: what a JVM project actually is.

- **Concepts:** What `pom.xml` is and why it exists (≈ `package.json` + build config in one file);
  groupId/artifactId/version coordinates; dependency scopes; the standard `src/main/java` +
  `src/test/java` layout and why it's rigid; compilation to `.class` files and what the classpath is (vs Python's import
  path); what the Maven wrapper `./mvnw` does.
- **Build goal:** Hand-write a minimal `pom.xml` with one dependency. Compile and run a `main()`
  that prints something. No Spring, no generator.
- **Interview check:** *"Walk me through what happens when you run `mvn package`."*
- **Quiz gate:** `maven`, `classpath`, `jvm`

### Lesson 1 — Classes, objects, and why everything lives in one

- **Concepts:** No free-floating functions in Java — every function is a method on a class (contrast: Flask's
  module-level `@app.route` functions); instance vs `static`; constructors; fields vs local variables; getters/setters
  and what "POJO"/"bean" means; `record` (Java 21) as the concise version — closest thing to a TS `interface` or Python
  `@dataclass`.
- **Build goal:** Write `Application` twice — once as a traditional class with constructor + getters, once as a
  `record`. Compare them.
- **Interview check:** *"When would you use a `record` instead of a class?"*
- **Quiz gate:** `classes`, `objects`, `records`

### Lesson 2 — Types, enums, collections, and Optional

- **Concepts:** Static typing and what the compiler catches that Python doesn't; `enum` for `Status`
  (≈ a TS union type, but a real object); generics and `List<Application>` (≈ `list[Application]`);
  `Map`; `Optional<T>` and why Java prefers it to returning `null` (≈ handling `None`, but enforced); a first look at
  streams for filtering.
- **Build goal:** An in-memory `List<Application>`, filtered by status with a stream, returning
  `Optional<Application>` from a find-by-id.
- **Interview check:** *"What problem does `Optional` solve, and when is it the wrong tool?"*
- **Quiz gate:** `data-types`, `enums`, `generics`, `optional`

### Lesson 3 — Interfaces and dependency injection, done by hand

The single most important lesson for understanding Spring.

- **Concepts:** Interface vs implementation; "program to an interface"; constructor injection written manually with no
  framework; why this makes code testable (you can pass in a fake).
- **Build goal:** An `ApplicationRepository` *interface* with an in-memory implementation, and an
  `ApplicationService` that receives it through its constructor. Wire it together yourself in
  `main()`.
- **Interview check:** *"What is dependency injection and what does it buy you?"* — you'll be able to answer this from
  having done it by hand, which is rare.
- **Quiz gate:** `interfaces`, `dependency-injection`

---

## Phase 2 — Spring Boot as a layer on top

*Goal: see the framework do exactly what you just did manually.*

### Lesson 4 — Annotations, and what Spring actually is

- **Concepts:** What an annotation *is* (metadata read via reflection at runtime — related to but not the same as a
  Python decorator); the application context as a big registry of objects;
  `@Component`/`@Service`/`@Repository`; `@SpringBootApplication` and component scanning; auto-configuration and why the
  app boots a web server without you writing one.
- **Build goal:** Port Lesson 3's code to Spring Boot. Delete your manual wiring, add annotations, print the bean list
  to prove the container built the same object graph.
- **Interview check:** *"What is the Spring IoC container doing at startup?"*
- **Quiz gate:** `annotations`, `spring-context`, `beans`

### Lesson 5 — The HTTP layer *(PROJECT.md Milestone 2, part 1)*

Closest lesson to what you already know from Flask.

- **Concepts:** `@RestController` and `@GetMapping`/`@PostMapping` (≈ `@app.route`); path variables and query params;
  `@RequestBody`; Jackson auto-serializing objects to JSON (≈ `jsonify`, but automatic); `ResponseEntity` and choosing
  correct status codes (201 vs 200, 204 vs 200).
- **Build goal:** `GET /api/applications` and `POST /api/applications` against the in-memory repo. Hit them with curl.
  **First test written here.**
- **Interview check:** *"What status code do you return from a POST that creates a resource, and what header goes with
  it?"*
- **Quiz gate:** `rest-controllers`, `http-status-codes`, `json-serialization`

### Lesson 6 — Persistence with JPA and Postgres *(Milestone 1's database half)*

- **Concepts:** What an ORM does and what it costs; `@Entity`/`@Id`/`@GeneratedValue`; JPA (spec) vs Hibernate
  (implementation) vs Spring Data JPA (convenience layer) — a common interview trip-up; the persistence context and
  dirty checking; `JpaRepository` and derived query methods (`findByStatus` written as a method name, not SQL);
  `application.properties` and `ddl-auto`.
- **Build goal:** Swap the in-memory implementation for a real Postgres-backed one. The service layer above it should
  not change — that's the payoff from Lesson 3.
- **Interview check:** *"What does Hibernate give you that plain JDBC doesn't? What does it cost?"*
- **Quiz gate:** `orm`, `jpa-entities`, `spring-data-repositories`

### Lesson 7 — DTOs and validation *(Milestone 2, part 2)*

- **Concepts:** Why exposing entities directly over HTTP is a design smell (coupling, over-fetching, mass-assignment);
  request vs response DTOs as `record`s; Bean Validation (`@NotBlank`, `@Valid`); where mapping code belongs.
- **Build goal:** `CreateApplicationRequest` / `ApplicationResponse` records, validated, with a 400 response on bad
  input.
- **Interview check:** *"Why not just return your JPA entity from the controller?"*
- **Quiz gate:** `dtos`, `validation`

### Lesson 8 — Filtering, sorting, pagination *(Milestone 3)*

- **Concepts:** Derived query methods in depth; `Pageable` and `Page<T>`; why pagination is a correctness concern, not
  an optimization; optional query params.
- **Build goal:** `GET /api/applications?status=INTERVIEWING&page=0&size=20&sort=appliedDate,desc`
- **Quiz gate:** `pagination`, `query-methods`

### Lesson 9 — Entity relationships *(Milestone 4)*

Highest-value interview topic in the whole project.

- **Concepts:** `@OneToMany`/`@ManyToOne`; the owning side and where the foreign key actually lives;
  `cascade`; `LAZY` vs `EAGER` fetching; the N+1 query problem and how to spot it in the SQL log; nested resource URL
  design.
- **Build goal:** `Interview` entity, `POST /api/applications/{id}/interviews`, plus SQL logging turned on so you can
  *watch* N+1 happen and then fix it.
- **Interview check:** *"Explain the N+1 problem and how you'd fix it."*
- **Quiz gate:** `entity-relationships`, `lazy-loading`, `n-plus-one`

### Lesson 10 — Exceptions and error handling *(Milestone 5)*

- **Concepts:** Checked vs unchecked exceptions (a genuine Java-ism with no Python equivalent); custom exception types;
  `@ControllerAdvice` + `@ExceptionHandler` as cross-cutting concern handling; designing a consistent JSON error body.
- **Build goal:** `ApplicationNotFoundException` → a clean 404 JSON body, handled globally rather than in every
  controller method.
- **Interview check:** *"Checked or unchecked for a 'not found' condition — and why?"*
- **Quiz gate:** `exceptions`, `error-handling`

---

## Phase 3 — Production concerns

### Lesson 11 — Testing, properly *(Milestone 7)*

- **Concepts:** The test pyramid; JUnit 5 (`@Test`, assertions, lifecycle); Mockito for unit-testing the service with a
  fake repository — this is the payoff for Lesson 3; `@WebMvcTest` + MockMvc for controllers; `@DataJpaTest` for
  repositories; test slices and why they're faster than booting the whole app.
- **Build goal:** Backfill coverage across all three layers.
- **Interview check:** *"What do you mock, and what do you deliberately not mock?"*
- **Quiz gate:** `unit-testing`, `mocking`, `integration-testing`

### Lesson 12 — Security and JWT *(Milestone 6)*

Hardest lesson — deliberately last on the backend, once everything else is stable and tested.

- **Concepts:** The Spring Security filter chain and where your code plugs into it;
  `UserDetailsService`; password hashing with BCrypt; what a JWT actually is (header/payload/signature — decodeable, not
  encrypted, a common misconception); stateless auth vs sessions; scoping every query to the current user.
- **Build goal:** `/api/auth/register` + `/api/auth/login`, a JWT filter, and applications that only return rows
  belonging to the logged-in user.
- **Interview check:** *"Where would you store the JWT on the client, and what's the tradeoff?"*
- **Quiz gate:** `authentication`, `jwt`, `password-hashing`

### Lesson 13 — Frontend integration *(Milestone 8)*

Light lesson — you already know React. Focus is the seam between the two apps.

- **Concepts:** CORS and why the browser blocks you (and why curl didn't); attaching the JWT to requests; loading/error
  states against a real API; wiring the status filter to the query params from Lesson 8.
- **Build goal:** List view with filter, add/edit form, delete, login screen. **v1 Definition of Done reached.**

---

## Progress

| #  | Lesson                                                                | Milestone | Status |
|----|-----------------------------------------------------------------------|-----------|--------|
| 0  | [Build system & project shape](lessons/lesson-00-build-system.md)     | —         | ☐     |
| 1  | [Classes, objects, records](lessons/lesson-01-classes-and-records.md) | —         | ☐     |
| 2  | Types, enums, collections, Optional                                   | —         | ☐     |
| 3  | Interfaces & manual DI                                                | —         | ☐     |
| 4  | Annotations & the Spring container                                    | 1         | ☐     |
| 5  | HTTP layer                                                            | 2         | ☐     |
| 6  | JPA & Postgres                                                        | 1         | ☐     |
| 7  | DTOs & validation                                                     | 2         | ☐     |
| 8  | Filtering, sorting, pagination                                        | 3         | ☐     |
| 9  | Entity relationships                                                  | 4         | ☐     |
| 10 | Exceptions & error handling                                           | 5         | ☐     |
| 11 | Testing                                                               | 7         | ☐     |
| 12 | Security & JWT                                                        | 6         | ☐     |
| 13 | Frontend integration                                                  | 8         | ☐     |
