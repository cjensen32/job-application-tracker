# Lesson 3 — Interfaces and Dependency Injection, By Hand

**Goal:** Put your applications behind a *repository interface*, and hand a repository to a service through its constructor — with no framework anywhere. **You'll be able to answer:** *"What is dependency injection and what does it actually buy you?"*

**Prerequisite:** Lesson 2 (packages, `Status`, collections, `Optional`).

**Progress** — tick these off as you go:

- [x] Step 1 — The interface
- [x] Step 2 — One implementation (finished, compiling)
- [x] Step 3 — Injection: the whole idea in six lines
- [x] Step 4 — Wire it up in `main`
- [x] Step 5 — Why this makes tests possible
- [x] Self-check answered

This is the highest-leverage lesson in Chapter 1. Dependency-injection containers automate this same pattern. If you understand the manual version, a framework is a convenience rather than magic.

---

## The problem, stated concretely

Right now `Main` holds a `List<Application>` and manipulates it directly. Suppose you write a `ApplicationService` that does the same:

```java
public class ApplicationService {
  private final List<Application> applications = new ArrayList<>();   // ← the problem
}
```

The service **constructs its own storage**. Three consequences, and the third is the one interviewers are listening for:

1. Swapping the `List` for Postgres means editing `ApplicationService`.
2. Every `ApplicationService` gets its own private list — you can't share storage.
3. **You cannot test it.** There's no seam. To test the service's logic you'd have to accept whatever storage it decided to build, so you could not hand it a fake that pretends storage failed.

The fix has two halves: an **interface** (what storage does) and **injection** (someone else decides which one).

---

## Step 1 — The interface

An interface is a contract: method signatures, no bodies. No fields, no constructor, no logic.

- [x] Create `src/main/java/com/connorjensen/jobtracker/repository/ApplicationRepository.java`:

```java
package com.connorjensen.jobtracker.repository;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository {

  Application save(Application application);

  List<Application> findAll();

  Optional<Application> findById(Long id);

  List<Application> findByStatus(Status status);

  boolean deleteById(Long id);
}
```

Note the semicolons where bodies would be. Interface methods are implicitly `public abstract`, so you don't write either keyword.

**Those method names are not arbitrary.** `save`, `findAll`, `findById`, and `deleteById` are common repository operations, and `findByStatus` has the shape used by derived-query libraries. You are hand-building the interface a persistence framework could later implement, which makes the abstraction concrete.

---

## Step 2 — One implementation

- [x] Create `src/main/java/com/connorjensen/jobtracker/repository/InMemoryApplicationRepository.java`:

```java
package com.connorjensen.jobtracker.repository;

import com.connorjensen.jobtracker.model.Application;

import java.util.HashMap;
import java.util.Map;

public class InMemoryApplicationRepository implements ApplicationRepository {

  private final Map<Long, Application> storage = new HashMap<>();
  private long nextId = 1;

  @Override
  public Application save(Application application) {
    if (application.getId() == null) {
      application.setId(nextId);
      nextId++;
    }
    storage.put(application.getId(), application);
    return application;
  }

  // ... the rest is yours to finish
}
```

Three things to read carefully:

- **`implements ApplicationRepository`** — this is a promise to the compiler. Leave out any method and the class won't compile. That's the contract being enforced, not documented.
- **`@Override`** optional but always write it. It tells the compiler "this is meant to implement something," so a typo in the method name becomes a compile error instead of a method nobody calls. This is your second annotation, and like `@Test` it's metadata, not behavior.
- **`save` assigns the id when it's `null`** — exactly what a database does with an auto-increment column. Save an object that already has an id and it's an update instead. That's why `Long id` needed to be nullable back in Lesson 2.

Now write the rest:

- [x] **Finish the class yourself** — `findAll`, `findById`, `findByStatus`, `deleteById`

- `findAll` returns `new ArrayList<>(storage.values())`
- `findById` uses `Optional.ofNullable(storage.get(id))`,
- `findByStatus` is a stream `.filter(...).toList()`
- `deleteById` returns whether `storage.remove(id)` gave back a non-null value.

---

## Step 3 — Injection: the whole idea in six lines

Now the service. The rule: **it never says `new` for its dependency.** It declares what it needs and receives it.

- [x] Create `src/main/java/com/connorjensen/jobtracker/service/ApplicationService.java`:

```java
package com.connorjensen.jobtracker.service;

import com.connorjensen.jobtracker.repository.ApplicationRepository;

public class ApplicationService {

  private final ApplicationRepository repository;

  public ApplicationService(ApplicationRepository repository) {
    this.repository = repository;
  }
}
```

That constructor is **constructor injection**. That's it. That's the pattern that the entire Spring framework exists to automate, and you just wrote it in five lines with zero dependencies.

Two details doing real work:

- The field type is `ApplicationRepository`, the **interface**. The service has no idea an in-memory `HashMap` exists. You could hand it a database-backed implementation later and **this file would not change**. That's the payoff.
- **`final`** means the field must be assigned exactly once, in the constructor, and can never be reassigned. The object is fully valid the instant it exists — no half-constructed service, no possibility of a `null` repository. This is why constructor injection is preferred over field injection (`@Autowired` on a field), and saying that in an interview is a genuinely strong signal.

---

## Step 4 — Wire it up in `main`

Someone still has to decide which implementation gets used. In a framework-free app, that someone is `main`:

- [x] Build the object graph in `main`

```java
public static void main(String[] args) {
  ApplicationRepository repository = new InMemoryApplicationRepository();
  ApplicationService service = new ApplicationService(repository);

  // ... use the service
}
```

This is **the composition root** — the one place in the program that knows about concrete classes. Everything below it deals only in interfaces.

Look at what you just built: a place that knows how to construct objects and hand them their dependencies. That is what a dependency-injection container automates. You can explain the generated object graph because you built it manually first.

---

## Step 5 — Why this makes tests possible

Here's the concrete payoff, and it's the answer to "what does DI buy you." Because `ApplicationService` accepts *any* `ApplicationRepository`, a test can pass one that isn't real:

- [x] Read this one — you'll write it for real in the capstone

```java
class AlwaysEmptyRepository implements ApplicationRepository {
  @Override public Optional<Application> findById(Long id) { return Optional.empty(); }
  // ... other methods
}

// in a test:
ApplicationService service = new ApplicationService(new AlwaysEmptyRepository());
```

Now you can test "what does the service do when the application doesn't exist?" without a database, without network, in microseconds. A mocking library could generate that fake class, but the *reason it's possible* is the interface and constructor, not the library.

**The interview answer:** *"Dependency injection means an object receives its collaborators rather than constructing them. It buys you substitutability — you can swap the implementation without touching the consumer, which is what makes the code testable and what let me move from an in-memory store to Postgres without changing my service layer."* That last clause is a real thing you will have done, which is worth more than the definition.

---

## Self-check

- [x] Answered all five without opening the answers

1. What's the difference between an interface and a class that has no fields?
2. Why does `ApplicationService` declare the field as `ApplicationRepository` and not `InMemoryApplicationRepository`?
3. What does `final` on the field prevent, and why does that matter at construction time?
4. `@Override` is optional. What do you lose by omitting it?
5. Where in a framework-free program does the choice of implementation get made, and what's that place called?

<details>
<summary>Answers</summary>

1. An interface has no state and no constructor and can't be instantiated; a class can be `new`ed even with no fields. More importantly a class can only extend one parent, but can implement many interfaces.
2. So the service depends on the contract, not the storage mechanism — allowing a database-backed implementation without editing the service.
3. Reassignment. The field must be set in the constructor, so the object is never in a partially built state with a `null` dependency.
4. Compile-time checking that the method actually implements something. Without it, a typo silently creates a new unused method and the interface method stays unimplemented (or, if it's abstract, you get a confusing error elsewhere).
5. In `main` — the composition root. A dependency-injection container could take over that role.

</details>

---

## Where this lands you

The core object graph is complete. Lesson 4 adds the control-flow and recovery boundaries needed before the capstone becomes a reliable interactive application.

**Next:** [Lesson 4 — Control Flow and Exception Boundaries](04-control-flow-and-exception-boundaries.md)

---

**Previous:** [Lesson 2 — Packages, Enums, Collections, Optional](02-types-enums-collections.md) · **Version history:** [NOTES.md](NOTES.md)
