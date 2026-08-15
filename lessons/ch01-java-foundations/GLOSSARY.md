# Chapter 1 — Glossary & Cheatsheet

Terms from Lessons 0–3, with the Python/JS equivalent where one exists. Skim this before a quiz or an interview; it's
meant to be the fastest re-entry point into the chapter.

---

## Build & tooling

| Term                   | Meaning                                                                                                                                                                            |
|------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **JVM**                | The runtime that executes bytecode. Java compiles to `.class` files, not to machine code.                                                                                          |
| **JDK vs JRE**         | JDK = compiler + tools + runtime (what you develop with). JRE = runtime only.                                                                                                      |
| **classpath**          | The list of locations where the compiler and JVM look for classes. Python resolves imports at runtime by scanning `site-packages`; Java must be told, twice.                       |
| **Maven**              | Dependency manager + build tool. ≈ `pip` + `npm scripts` + a standard build, in one.                                                                                               |
| **`pom.xml`**          | The project descriptor. ≈ `package.json` + build config.                                                                                                                           |
| **coordinates**        | `groupId` + `artifactId` + `version` — the globally unique address of an artifact.                                                                                                 |
| **`-SNAPSHOT`**        | "In development, may change." Released versions are immutable forever.                                                                                                             |
| **scope**              | Where a dependency applies: `compile` (default, shipped), `test` (test classpath only), `provided` (compile only, supplied at runtime), `runtime` (needed to run, not to compile). |
| **lifecycle**          | `validate → compile → test → package → verify → install → deploy`, run cumulatively. `clean` is a separate lifecycle.                                                              |
| **surefire**           | The Maven plugin that runs tests. Must be pinned to 3.x or JUnit 5 tests are silently skipped.                                                                                     |
| **`~/.m2/repository`** | The local dependency cache, shared across all your projects.                                                                                                                       |
| **jar**                | A zip of `.class` files plus metadata. Not runnable with `java -jar` unless a `Main-Class` manifest entry exists.                                                                  |

## Language shape

| Term                           | Meaning                                                                                                                                                                                       |
|--------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **package**                    | Namespace, and it **must** match the directory path. Declared at the top of every file.                                                                                                       |
| **import**                     | The only way one file sees a class in another package. No implicit sibling access.                                                                                                            |
| **class**                      | Blueprint for objects. Holds fields (state) and methods (behaviour).                                                                                                                          |
| **field**                      | State belonging to an object. `private` by convention.                                                                                                                                        |
| **constructor**                | Same name as the class, no return type. Runs on `new`.                                                                                                                                        |
| **`static`**                   | Belongs to the class, not an instance. `main` is static because no objects exist at JVM startup.                                                                                              |
| **`final` (field)**            | Assignable exactly once, in the constructor. Cannot be reassigned.                                                                                                                            |
| **getter/setter**              | `getX()` / `setX()`. The **JavaBean convention** — Jackson and Hibernate are built on this naming pattern, so it isn't decoration.                                                            |
| **POJO / bean**                | "Plain Old Java Object" — a class with fields and accessors and no framework coupling.                                                                                                        |
| **`record`**                   | Java 21 concise immutable data carrier. Auto-generates constructor, accessors (`company()`, no `get`), `equals`, `hashCode`, `toString`. ≈ TS `interface` / Python `@dataclass(frozen=True)`. |
| **value vs identity equality** | Records compare by value. Plain classes inherit `Object.equals`, which compares identity — two identical-looking objects are **not** equal unless you override it.                            |
| **`enum`**                     | A fixed set of singleton instances, checked at compile time and present at runtime. ≈ a TS union that actually exists at runtime. `values()`, `valueOf(String)` (throws on unknown).          |
| **`Long` vs `long`**           | `Long` is an object and can be `null` ("not saved yet"). `long` is a primitive that defaults to `0`.                                                                                          |
| **annotation**                 | Metadata attached to code, read via reflection. Does nothing by itself — something has to scan for it. `@Test`, `@Override`, and later every Spring annotation.                               |

## Collections

| Term                       | Meaning                                                                                                                                                                                    |
|----------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **generics**               | `List<Application>` — the type parameter that lets the compiler reject wrong element types.                                                                                                |
| **`List` / `ArrayList`**   | Interface / implementation. Always declare the interface: `List<X> x = new ArrayList<>();`                                                                                                 |
| **`Map<K,V>` / `HashMap`** | Key-value store. ≈ a dict. `put`, `get` (returns null if absent), `remove`, `values()`.                                                                                                    |
| **diamond `<>`**           | Type inferred from the left-hand side; don't repeat it.                                                                                                                                    |
| **enhanced for**           | `for (Application a : list)` — Java's `for x in list`.                                                                                                                                     |
| **stream**                 | A lazy, single-use pipeline. `.stream().filter(...).map(...).toList()`. ≈ JS array methods, but nothing runs until a terminal operation.                                                   |
| **lambda**                 | `app -> app.getStatus()` — an anonymous function.                                                                                                                                          |
| **method reference**       | `Application::getCompany` — shorthand for the lambda above.                                                                                                                                |
| **`Optional<T>`**          | A box that may or may not hold a value. For **return types only**. `isPresent`, `isEmpty`, `get`, `orElse`, `orElseThrow`, `map`, `ifPresent`. Not for fields, parameters, or collections. |

## Design

| Term                        | Meaning                                                                                                                              |
|-----------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| **interface**               | A contract: method signatures, no bodies, no state. A class can implement many.                                                      |
| **`implements`**            | A compiler-enforced promise to provide every method.                                                                                 |
| **`@Override`**             | Optional annotation that turns a method-name typo into a compile error. Always write it.                                             |
| **dependency injection**    | An object *receives* its collaborators rather than constructing them.                                                                |
| **constructor injection**   | DI via constructor parameters, with `final` fields. Preferred: the object is never partially built.                                  |
| **composition root**        | The one place that knows about concrete implementations. In Chapter 1 that's `main`; in Chapter 2 it's Spring's application context. |
| **program to an interface** | Depend on the contract, not the implementation — so it can be swapped or faked.                                                      |
| **layering**                | `controller → service → repository → model`. Dependencies point inward only.                                                         |

---

## Commands

```bash
mvn compile                  # src/main/java → target/classes
mvn test                     # compile + run tests
mvn package                  # ... + build the jar
mvn clean package            # delete target/ first
mvn test -Dtest=ClassName    # one test class
mvn test -Dtest=ClassName#methodName

java -cp target/classes com.connorjensen.jobtracker.Main
```

---

## The five answers you should have cold after Chapter 1

1. **`mvn package`** runs validate → compile → test → package, in order; a failing test halts it and you get no jar.
2. **A record instead of a class** when you want an immutable value carrier (DTOs). Not for JPA entities — Hibernate
   needs a no-arg constructor and mutable fields.
3. **`Optional`** documents "this may find nothing" in the return type so the compiler makes you deal with it. Wrong for
   fields, parameters, and collections.
4. **Dependency injection** means receiving collaborators instead of constructing them; it buys substitutability, which
   is what makes code testable and what let the storage layer be swapped without touching the service.
5. **Constructor injection over field injection** because `final` fields guarantee the object is fully valid the moment
   it exists, and the dependencies are visible in the signature.

---

**Chapter:** [Chapter 1 — Java Foundations](README.md)
