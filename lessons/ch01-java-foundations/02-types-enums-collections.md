# Lesson 2 — Packages, Enums, Collections, and Optional

**Goal:** Give the project a real package structure, replace stringly-typed status with an `enum`, and
hold many applications in a collection you can query.
**You'll be able to answer:** *"What problem does `Optional` solve, and when is it the wrong tool?"*

**Prerequisite:** Lesson 1 (you have `Application`, `ApplicationDto`, `Main`, and a passing test).

---

## Step 1 — Packages: stop putting everything in one bucket

Right now all four of your files sit in `com.connorjensen.jobtracker`. That's fine for four files and
untenable for forty. Java's answer is **packages**, and unlike Python's flexible module layout, the
package name *must* match the directory path — so reorganizing code means moving files.

The layout below is the one this project keeps to the end. Every Spring app you'll read in an
interview uses some version of it:

```
com.connorjensen.jobtracker
├── Main.java          entry point
├── model/             the things (Application, Status, later Interview)
├── repository/        how things are stored
├── service/           business rules
├── dto/               what goes over the wire (Chapter 2)
└── controller/        HTTP endpoints (Chapter 2)
```

Make the move:

```bash
mkdir -p src/main/java/com/connorjensen/jobtracker/{model,repository,service,dto}
git mv src/main/java/com/connorjensen/jobtracker/Application.java     src/main/java/com/connorjensen/jobtracker/model/
git mv src/main/java/com/connorjensen/jobtracker/ApplicationDto.java  src/main/java/com/connorjensen/jobtracker/dto/
```

(Plain `mv` works too if you haven't committed these yet.)

Now **the code won't compile** until you fix three things — and this is the part worth internalizing:

1. `Application.java` — change the first line to `package com.connorjensen.jobtracker.model;`
2. `ApplicationDto.java` — change it to `package com.connorjensen.jobtracker.dto;`
3. `Main.java` — it can no longer see either class. Add imports:

```java
import com.connorjensen.jobtracker.dto.ApplicationDto;
import com.connorjensen.jobtracker.model.Application;
```

Your test file has the same problem. Move it to mirror the main tree — tests live in a package that
matches the code they test:

```bash
mkdir -p src/test/java/com/connorjensen/jobtracker/model
git mv src/test/java/com/connorjensen/jobtracker/ApplicationTest.java src/test/java/com/connorjensen/jobtracker/model/
```

...then set its package to `com.connorjensen.jobtracker.model` and import `ApplicationDto` from
`...jobtracker.dto`.

```bash
mvn test
```

Green again. The lesson here isn't the file shuffling — it's that **`package` and `import` are the
only visibility mechanism Java has between files.** There's no `from . import *`, no implicit
sibling access. A class in `model` cannot see a class in `service` unless it imports it, and that
one-way import direction is how you'll keep the layers from tangling.

> **Why `model` doesn't import `service`:** dependencies point *inward*. Controllers know about
> services, services know about repositories, repositories know about models. Nothing points back
> up. If you ever find yourself wanting `model` to import `service`, the design has gone wrong.

---

## Step 2 — The `enum`: your first real type

PROJECT.md says an application's status is one of five values. In Python or JS you'd probably use a
string and hope. In TypeScript you'd write a union type — which vanishes at runtime.

Java's `enum` <sup>[J5](NOTES.md#enums)</sup> is a real class with a fixed set of instances, checked at
**compile time** and present at **runtime**.

Create `src/main/java/com/connorjensen/jobtracker/model/Status.java`:

```java
package com.connorjensen.jobtracker.model;

public enum Status {
    APPLIED,
    PHONE_SCREEN,
    INTERVIEWING,
    OFFER,
    REJECTED
}
```

Five lines, and you get a lot:

- `Status.APPLIED` is a **singleton** — there is exactly one instance of it in the whole JVM, so
  `==` works for comparison (unlike `String`, where `==` compares identity and burns beginners).
- `Status.values()` returns all five as an array.
- `Status.valueOf("OFFER")` parses a string, and **throws** on anything else. That's your input
  validation for free — and in Chapter 2, Spring will call this method for you to turn
  `?status=OFFER` into a `Status`.
- `switch` over an enum can be checked for exhaustiveness by the compiler.

Now wire it into `Application`. Add the field, default it in the constructor, and give it a getter
and setter:

```java
    private Status status;

    public Application(String company, String role, LocalDate appliedDate) {
        this.company = company;
        this.role = role;
        this.appliedDate = appliedDate;
        this.status = Status.APPLIED;   // a new application is always APPLIED
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
```

That default in the constructor is a **business rule enforced by the type system**. It is not
possible to construct an `Application` with no status. Compare to a Python dict, where
`app["status"]` might just... not be there.

While you're in the file, add the two remaining fields from PROJECT.md — `notes` and `jobUrl`, both
`String`, both with getter and setter, neither in the constructor (they're optional at creation
time). And add an `id`:

```java
    private Long id;
```

with `getId()` / `setId(Long id)`. Note **`Long`**, not `long` <sup>[J5](NOTES.md#long-vs-long-and-autoboxing)</sup> — the capital-L version is an
*object* and can be `null`, which is exactly what you need to mean "not saved yet." The lowercase
`long` is a primitive and would default to `0`. Chapter 3's database layer depends on this
distinction.

---

## Step 3 — Collections: `List`, `Map`, and generics

You need somewhere to put many applications. Java's collections are typed:

```java
List<Application> applications = new ArrayList<>();
```

Read that as "a List of Application." The `<Application>` is a **generic type parameter**
<sup>[J5](NOTES.md#generics)</sup> — it's what
lets the compiler reject `applications.add("oops")` before the program ever runs.

Two things to notice:

- **`List` on the left, `ArrayList` on the right.** `List` is an *interface* (the contract: add,
  get, size); `ArrayList` is one *implementation* (backed by an array). Declaring the variable as the
  interface means you could swap in `LinkedList` later without touching any other line. This is a
  small preview of Lesson 3, and it's idiomatic Java — always declare the interface type.
- **`new ArrayList<>()`** — the empty `<>` is the "diamond" <sup>[J7](NOTES.md#the-diamond-operator)</sup>. The compiler already knows the type from
  the left side, so you don't repeat it.

The other collection you need is `Map<K, V>` — a dictionary:

```java
Map<Long, Application> byId = new HashMap<>();
byId.put(1L, app);          // 1L is a long literal; plain 1 is an int
Application found = byId.get(1L);   // returns null if absent
```

`Map` is how you'll fake a database table in the capstone: id → row.

Try it in `Main` — replace the body with:

```java
List<Application> applications = new ArrayList<>();
applications.add(new Application("Acme Corp", "Backend Engineer", LocalDate.of(2026, 8, 1)));
applications.add(new Application("Globex", "Platform Engineer", LocalDate.of(2026, 8, 5)));
applications.add(new Application("Initech", "Java Developer", LocalDate.of(2026, 8, 9)));

applications.get(1).setStatus(Status.INTERVIEWING);

for (Application app : applications) {
    System.out.println(app.summary() + " [" + app.getStatus() + "]");
}
```

`for (Application app : applications)` is the **enhanced for loop** <sup>[J5](NOTES.md#enhanced-for-loop)</sup> — Java's `for x in list`. You'll
need `import java.util.ArrayList;`, `java.util.List;`, and the `model` imports.

---

## Step 4 — Streams: filtering without a loop

You could filter with a `for` loop and an `if`. Java's idiomatic answer is a **stream** <sup>[J8](NOTES.md#lambdas-streams-and-method-references)</sup>, and it
will look extremely familiar:

```java
List<Application> interviewing = applications.stream()
        .filter(app -> app.getStatus() == Status.INTERVIEWING)
        .toList();
```

That's JS `.filter()` with different punctuation. `app -> ...` is a **lambda** — an anonymous
function. `.stream()` opens a pipeline, `.filter()` describes a step, and `.toList()` **terminates**
it and produces a real list.

The one non-obvious rule: a stream is **lazy and single-use**. Nothing happens until a terminal
operation (`.toList()`, `.count()`, `.findFirst()`) runs, and you can't reuse the stream afterward.
It's a pipeline description, not a collection.

The other operations you'll actually use:

```java
.map(Application::getCompany)                 // transform each element
.sorted(Comparator.comparing(Application::getAppliedDate))
.count()
```

`Application::getCompany` is a **method reference** — shorthand for `app -> app.getCompany()`.

---

## Step 5 — `Optional`: the return type that admits it might be empty

Here's the problem. You want "find the application with this id." Sometimes there isn't one. In
Python you return `None` and hope the caller checks. In Java, returning `null` has the same flaw,
and it's the single most common source of production crashes — `NullPointerException`.

`Optional<T>` <sup>[J8](NOTES.md#optional)</sup> is a box that either contains a value or doesn't, and **the type
signature says so**:

```java
Optional<Application> found = applications.stream()
        .filter(app -> app.getCompany().equals("Globex"))
        .findFirst();
```

`.findFirst()` returns `Optional<Application>` because it genuinely might not find one. Now the
caller *cannot* accidentally use the value without confronting the empty case:

```java
if (found.isPresent()) {
    System.out.println(found.get().summary());
}

// or, better — no branch at all:
found.ifPresent(app -> System.out.println(app.summary()));

// supply a fallback:
Application app = found.orElse(null);
String company = found.map(Application::getCompany).orElse("not found");

// or blow up loudly with a message you chose:
Application required = found.orElseThrow(() -> new IllegalArgumentException("No Globex application"));
```

**When `Optional` is the wrong tool** — this is the second half of the interview question, and most
candidates miss it:

- **Not for fields.** `private Optional<String> notes;` is wrong — it isn't serializable and it adds
  a wrapper object per instance. Use a nullable field.
- **Not for method parameters.** Overload the method or pass `null`; forcing callers to wrap
  arguments is noise.
- **Not for collections.** An empty `List` already means "nothing" — `Optional<List<T>>` gives you
  two ways to say the same thing.

`Optional` is for **return types**, where it documents "this may legitimately find nothing" in a way
the compiler helps enforce. That's the whole answer.

> Spring Data hands you `Optional<T>` from `findById()` in Chapter 3. The pattern you write by hand
> in the capstone is byte-for-byte the pattern the framework expects.

---

## Self-check

1. You move a class into a new directory. What two lines of code must change, and where?
2. Why is `Status.APPLIED == Status.APPLIED` safe when `"APPLIED" == "APPLIED"` is not?
3. Why `List<Application> x = new ArrayList<>()` instead of `ArrayList<Application> x = ...`?
4. What happens if you call `.toList()` on the same stream twice?
5. Give one place `Optional` should *not* be used, and why.
6. Why `Long id` and not `long id`?

<details>
<summary>Answers</summary>

1. The `package` declaration in the moved file, and an `import` in every file that referenced it.
2. Each enum constant is a single instance, so identity comparison is value comparison. `String`s
   with the same content can be separate objects, so `==` may be false — use `.equals()`.
3. Declaring the interface type means the implementation can be swapped without changing any other
   code, and it stops you from depending on `ArrayList`-specific methods.
4. `IllegalStateException` — a stream is consumed by its terminal operation and cannot be reused.
5. As a field (not serializable, extra object per instance), as a parameter (noisy for callers), or
   wrapping a collection (an empty collection already conveys "none").
6. `Long` is an object and can be `null`, meaning "no id assigned yet." The primitive `long` would
   silently default to `0`, which is indistinguishable from a real id of 0.

</details>

---

## Where this lands you

You have a package structure, a type-safe `Status`, a collection of applications, and a way to
express "might not be there." Lesson 3 turns the loose `List` in `Main` into a **repository behind an
interface** — the single most important idea for understanding what Spring does for you.

---

**Previous:** [Lesson 1 — Classes, Objects, and Records](01-classes-and-records.md) ·
**Next:** [Lesson 3 — Interfaces and Dependency Injection](03-interfaces-and-di.md) ·
**Version history:** [NOTES.md](NOTES.md)
