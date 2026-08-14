# Lesson 1 — Classes, Objects, and Records

**Goal:** Understand why everything in Java lives inside a class, and when to use a `record`
instead. **You'll be able to answer:** *"When would you use a `record` instead of a class?"*

**Prerequisite:** Lesson 0 (you need a working `mvn test`).

**Progress** — tick these off as you go:

- [ ] Step 1 — The traditional class
- [ ] Step 2 — `static` vs instance, and why `main` is weird
- [ ] Step 3 — The same thing as a `record`
- [ ] Step 4 — The difference that actually matters (`mvn test` green)
- [ ] Step 5 — So which do I use?
- [ ] Self-check answered

---

## The core difference from Flask

In Flask, this is a complete, legal program:

```python
def create_application(company, role):
    return {"company": company, "role": role}
```

A function floating at module level, returning a dict. Java has **no equivalent**. There are no free-floating
functions — every function is a method attached to a class, and there are no anonymous dicts standing in for objects.
You must declare the shape of your data up front.

This feels like ceremony at first. The payoff shows up in Lesson 6: because the shape is declared and typed, Hibernate
can read it and generate a database table, and Jackson can read it and generate JSON — neither needs you to describe the
data twice.

---

## Step 1 — The traditional class

- [ ] Create `src/main/java/com/connorjensen/jobtracker/Application.java`:

```java
package com.connorjensen.jobtracker;

import java.time.LocalDate;

public class Application {

    private String company;
    private String role;
    private LocalDate appliedDate;

    public Application(String company, String role, LocalDate appliedDate) {
        this.company = company;
        this.role = role;
        this.appliedDate = appliedDate;
    }

    public String getCompany() {
        return company;
    }

    public String getRole() {
        return role;
    }

    public LocalDate getAppliedDate() {
        return appliedDate;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
```

Reading it piece by piece:

- **`private String company;`** — a **field**, the object's state. `private` means only code inside this class can touch
  it directly.
- **The constructor** — same name as the class, no return type. It runs when you write
  `new Application(...)`. `this.company = company` disambiguates the field from the parameter (they share a name by
  convention).
- **Getters/setters** — public methods gating access to private fields. This pattern (`private`
  field + `getX()`/`setX()`) is the **JavaBean convention**, and it is not decoration: Jackson uses
  `getCompany()` to decide the JSON key is `"company"`, and Hibernate uses the same convention to map fields to columns.
  The frameworks in this project are *built on* this naming pattern.

Notice `role` has a setter but `company` doesn't — that's a deliberate design choice meaning "the role can be corrected,
the company can't change." Encapsulation lets you enforce that; a Python dict can't.

---

## Step 2 — `static` vs instance, and why `main` is weird

- [ ] Add a method to `Application`:

```java
    public String summary() {
    return company + " — " + role;
}
```

`summary()` is an **instance method**: it needs an actual object, because it reads `company` from *that* object.

Compare to `Main.main()` from Lesson 0:

```java
public static void main(String[] args);
```

**`static`** means the method belongs to the *class itself*, not to any instance — you call it as
`Main.main(...)` with no object involved. `main` must be static for an obvious reason: when the JVM starts, **no objects
exist yet**. Something has to run before anything can be constructed.

- [ ] Try it in `Main.java`:

```java
package com.connorjensen.jobtracker;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Application app = new Application("Acme Corp", "Backend Engineer", LocalDate.of(2026, 8, 1));
        System.out.println(app.summary());
        System.out.println(app);
    }
}
```

- [ ] Compile and run it

```bash
mvn compile
java -cp target/classes com.connorjensen.jobtracker.Main
```

The second line prints something like `com.connorjensen.jobtracker.Application@1b6d3586` — the class name and a
memory-ish hash. **Java has no useful default `toString()`.** Hold onto that.

---

## Step 3 — The same thing as a `record`

- [ ] Create `src/main/java/com/connorjensen/jobtracker/ApplicationDto.java`:

```java
package com.connorjensen.jobtracker;

import java.time.LocalDate;

public record ApplicationDto(String company, String role, LocalDate appliedDate) {
}
```

**That's the whole file.** <sup>[J16](NOTES.md#records)</sup> It is not a stripped-down version of the class above —
it's very nearly equivalent. The compiler generates:

- a constructor taking all three components
- **accessors** — but named `company()`, *not* `getCompany()`
- `equals()` and `hashCode()` based on the values
- a real `toString()`

It's the closest thing Java has to a TypeScript `interface` or a Python `@dataclass(frozen=True)`. The tradeoff: a
record is **immutable** — no setters, ever. Fields are final. To "change" one you construct a new one.

- [ ] Add to `main`, then recompile and run:

```java
        ApplicationDto dto = new ApplicationDto("Acme Corp", "Backend Engineer", LocalDate.of(2026, 8, 1));
        System.out.println(dto);
```

Recompile and run. You get
`ApplicationDto[company=Acme Corp, role=Backend Engineer, appliedDate=2026-08-01]` — readable, for free.

---

## Step 4 — The difference that actually matters

Readable `toString` is nice. **Value equality** is the real reason records exist. Prove it with a test.

- [ ] Create `src/test/java/com/connorjensen/jobtracker/ApplicationTest.java`:

```java
package com.connorjensen.jobtracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ApplicationTest {

    private static final LocalDate DATE = LocalDate.of(2026, 8, 1);

    @Test
    void twoClassesWithIdenticalValuesAreNotEqual() {
        Application a = new Application("Acme Corp", "Backend Engineer", DATE);
        Application b = new Application("Acme Corp", "Backend Engineer", DATE);

        assertNotEquals(a, b);
    }

    @Test
    void twoRecordsWithIdenticalValuesAreEqual() {
        ApplicationDto a = new ApplicationDto("Acme Corp", "Backend Engineer", DATE);
        ApplicationDto b = new ApplicationDto("Acme Corp", "Backend Engineer", DATE);

        assertEquals(a, b);
    }
}
```

- [ ] Run the tests

```bash
mvn test
```

Both should pass — and the first one passing is the surprising part.

**Why:** a plain class inherits `equals()` from `Object`, which compares *identity* — "are these the same object in
memory?" Two separately-constructed objects never are. A record compares *values*.

This bites people constantly: `list.contains(app)` returns false for a class even when an identical object is in the
list, because `contains` uses `equals()`. You'll rely on record equality in Lesson 2 and again when writing test
assertions.

> `@Test` is your first annotation <sup>[J5](NOTES.md#annotations)</sup>. It's just metadata — it doesn't *do*
> anything. Surefire scans
> compiled classes, finds methods marked with it, and calls them. Keep that in mind for Lesson 4,
> where Spring does the same trick at much larger scale.

---

## Step 5 — So which do I use?

The practical rule for this project:

| Use a **class**                                        | Use a **record**                          |
|--------------------------------------------------------|-------------------------------------------|
| JPA entities (Lesson 6)                                | DTOs — request/response bodies (Lesson 7) |
| Anything with mutable state                            | Anything you just want to carry values    |
| Anything a framework must construct empty and populate | Anything short-lived and immutable        |

**Why can't a JPA entity be a record?** Hibernate needs to create an object *before* it has the data (it constructs it,
then fills fields as rows come back), and it needs to mutate fields to track changes. Records have no no-arg constructor
and no setters, so they're structurally incompatible. That's exactly why `Application` stays a class and
`ApplicationDto` is a record — the split you just built is the split the real app keeps.

That table is a genuinely common interview question, and "records can't be JPA entities because Hibernate needs a no-arg
constructor and mutable fields" is a much better answer than "records are shorter."

---

## Self-check

- [ ] Answered all five without opening the answers

1. Why must `main` be `static`?
2. What does `private` on a field actually prevent?
3. A record's accessor for `company` — what's it called?
4. Two `Application` objects with identical field values. Is `a.equals(b)` true? Why?
5. Why can't you make your JPA entity a record?

<details>
<summary>Answers</summary>

1. No objects exist when the JVM starts, so the entry point can't require an instance.
2. Code outside the class can't read or write it directly — access has to go through methods, which lets the class
   enforce rules (like "company can't change").
3. `company()` — records don't use the `get` prefix.
4. **False.** A plain class inherits identity-based `equals()` from `Object`. You'd have to override
   `equals()`/`hashCode()` yourself.
5. Hibernate needs a no-arg constructor and mutable fields to instantiate and populate entities; records have neither.

</details>

---

## Where this lands you

You now have a compiling project with a domain class, a record, and a passing test — no framework anywhere. Lesson 2
turns `status` into an `enum`, puts these in a `List`, and filters them with streams, still with zero Spring.

XP is banked once at the end of the chapter, after the capstone — keep going.

---

**Previous:** [Lesson 0 — Build System & Project Shape](00-build-system.md) ·
**Next:** [Lesson 2 — Packages, Enums, Collections, Optional](02-types-enums-collections.md) ·
**Version history:** [NOTES.md](NOTES.md)
