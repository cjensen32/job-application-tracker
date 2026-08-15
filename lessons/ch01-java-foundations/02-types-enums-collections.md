# Lesson 2 — Packages, Enums, Collections, and Optional

**Goal:** Give the project a real package structure, replace stringly-typed status with an `enum`, and
hold many applications in a collection you can query.
**You'll be able to answer:** *"What problem does `Optional` solve, and when is it the wrong tool?"*

**Prerequisite:** Lesson 1 (you have `Application`, `ApplicationDto`, `Main`, and a passing test).

**Progress** — tick these off as you go:

- [x] [Step 1](#step-1--packages-stop-putting-everything-in-one-bucket) — Packages (all three checkpoints, ending green)
- [x] [Step 2](#step-2--the-enum-your-first-real-type) — The `enum`: your first real type (ending green)
- [x] [Step 3](#step-3--collections-list-map-and-generics) — Collections: `List`, `Map`, and generics (ending green)
- [ ] [Step 4](#step-4--streams-filtering-without-a-loop) — Streams: filtering without a loop
- [ ] [Step 5](#step-5--optional-the-return-type-that-admits-it-might-be-empty) — `Optional`: the return type that admits it might be empty
- [ ] Self-check answered

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

- [x] Make the move

```bash
mkdir -p src/main/java/com/connorjensen/jobtracker/{model,repository,service,dto}
git mv src/main/java/com/connorjensen/jobtracker/Application.java     src/main/java/com/connorjensen/jobtracker/model/
git mv src/main/java/com/connorjensen/jobtracker/ApplicationDto.java  src/main/java/com/connorjensen/jobtracker/dto/
```

(Plain `mv` works too if you haven't committed these yet.)

### Checkpoint 1 — moving the files breaks nothing

- [x] Before you change a single line of Java, run:

```bash
mvn clean compile
```

**It succeeds.** That is the surprise worth sitting with, because the obvious mental model — "the
folder is the package" — has just failed to predict reality.

- [x] Look at where the output landed

```bash
find target/classes -name "*.class"
```

```
target/classes/com/connorjensen/jobtracker/Application.class      ← still flat
target/classes/com/connorjensen/jobtracker/ApplicationDto.class   ← not model/ or dto/
target/classes/com/connorjensen/jobtracker/Main.class
```

The `.class` files followed the **`package` declaration at the top of each file**, which you haven't
touched — not the directory you just moved them into. Think of the directory as the address on the
envelope and `package` as the return address written inside the letter. Java reads the letter.

And since all three files still *declare* `package com.connorjensen.jobtracker;`, they are still in
the same package as far as the compiler is concerned, so `Main` still sees `Application` with no
import at all.

> **"But doesn't javac check that the directory matches the package?"** No — and this trips up people
> who've used Java for years. The directory convention is how the compiler **finds** a class it
> wasn't handed. Maven globs every `.java` under `src/main/java` and passes the whole list to javac
> explicitly, so javac never has to go looking, and never consults the directory. (Some IDEs and
> linters flag the mismatch. `javac` itself does not.)
>
> <details>
> <summary>Prove it: one command that succeeds before the move and fails after</summary>
>
> Compile *only* `Main.java`, and tell javac where to hunt for the classes it's missing:
>
> ```bash
> javac -sourcepath src/main/java -d /tmp/out src/main/java/com/connorjensen/jobtracker/Main.java
> ```
>
> **Before the move:** succeeds silently. javac needs `Application`, resolves the name
> `com.connorjensen.jobtracker.Application` to the path `com/connorjensen/jobtracker/Application.java`
> under the sourcepath, finds it, compiles it too.
>
> **After the move:** `error: cannot find symbol — class Application`. Same command, same package
> declarations, same code. The only thing that changed is the folder, and the lookup now misses
> because there's no `Application.java` at that path any more.
>
> So the directory matters for **finding**, and not for **validating**. Maven skips the finding step,
> which is why `mvn compile` still passes and this command doesn't.
> </details>

### Checkpoint 2 — changing the `package` line is what breaks it

*Now* do the real work. Two edits:

- [x] `Application.java` — change the first line to `package com.connorjensen.jobtracker.model;`
- [x] `ApplicationDto.java` — change it to `package com.connorjensen.jobtracker.dto;`

Stop there, before touching `Main.java`, and compile:

- [x] Compile, and read the failure

```bash
mvn clean compile
```

```
[ERROR] .../jobtracker/Main.java:[7,5] cannot find symbol
[ERROR]   symbol:   class Application
[ERROR]   location: class com.connorjensen.jobtracker.Main
[ERROR] .../jobtracker/Main.java:[11,5] cannot find symbol
[ERROR]   symbol:   class ApplicationDto
[ERROR]   location: class com.connorjensen.jobtracker.Main
[INFO] BUILD FAILURE
```

Read that `location:` line — it's telling you exactly what went wrong. `Main` is still in
`com.connorjensen.jobtracker`, `Application` has left for `...jobtracker.model`, and Java gives a
class **no** implicit access to a different package. Not even a child one: `...jobtracker.model` is
not "inside" `...jobtracker` in any way the compiler cares about. Package names look hierarchical and
aren't.

- [x] `Main.java` — say where they went, with two `import` lines above the class:

```java
package com.connorjensen.jobtracker;

import com.connorjensen.jobtracker.dto.ApplicationDto;
import com.connorjensen.jobtracker.model.Application;

import java.time.LocalDate;

public class Main {
    // ... unchanged ...
}
```

- [x] Compile again, and check the output tree

```bash
mvn clean compile && find target/classes -name "*.class"
```

```
target/classes/com/connorjensen/jobtracker/dto/ApplicationDto.class    ← now nested
target/classes/com/connorjensen/jobtracker/model/Application.class
target/classes/com/connorjensen/jobtracker/Main.class
```

Green, and the output tree finally mirrors the source tree — because the `package` lines and the
directories agree for the first time.

### Checkpoint 3 — the test has the same problem

- [x] Run the tests, and watch them fail the same way

```bash
mvn test
```

```
[ERROR] .../jobtracker/ApplicationTest.java:[16,5] cannot find symbol
```

Same cause: `ApplicationTest` declares `package com.connorjensen.jobtracker` and the classes it
tests no longer live there. Tests live in a package that matches the code they test, so move it to
mirror the main tree:

- [x] Move the test file

```bash
mkdir -p src/test/java/com/connorjensen/jobtracker/model
git mv src/test/java/com/connorjensen/jobtracker/ApplicationTest.java src/test/java/com/connorjensen/jobtracker/model/
```

- [x] ...then set its package to `com.connorjensen.jobtracker.model` and import `ApplicationDto` from
  `...jobtracker.dto`. (It needs no import for `Application` — same package now.)
- [x] Run the tests once more

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

- [x] Create `src/main/java/com/connorjensen/jobtracker/model/Status.java`:

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

Now wire it into `Application`.

- [x] Add the field, default it in the constructor, and give it a getter and setter

```java
public class Application {

    // ... company, role, appliedDate from Lesson 1 ...

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

    // ... getters from Lesson 1 unchanged ...
}
```

That default in the constructor is a **business rule enforced by the type system**. It is not
possible to construct an `Application` with no status. Compare to a Python dict, where
`app["status"]` might just... not be there.

- [x] While you're in the file, add the two remaining fields from PROJECT.md — `notes` and `jobUrl`,
  both `String`, both with getter and setter, neither in the constructor (they're optional at
  creation time)
- [x] And add an `id`:

```java
public class Application {

    private Long id;

    // ... the rest of the class ...
}
```

with `getId()` / `setId(Long id)`. Note **`Long`**, not `long` <sup>[J5](NOTES.md#long-vs-long-and-autoboxing)</sup> — the capital-L version is an
*object* and can be `null`, which is exactly what you need to mean "not saved yet." The lowercase
`long` is a primitive and would default to `0`. Chapter 3's database layer depends on this
distinction.

### Verify

- [x] Build and run the existing tests

```bash
mvn clean test
```

```
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

- [x] Prove the two rules you just encoded — the constructor default, and `Long` being nullable

```bash
mvn -q compile && jshell --class-path target/classes -q <<'EOF'
import com.connorjensen.jobtracker.model.*;
var a = new Application("Acme", "Backend Engineer", java.time.LocalDate.of(2026,8,1));
System.out.println(a.getStatus() + " / id=" + a.getId());
/exit
EOF
```

```
jshell> jshell> var a = new Application(...)jshell> System.out.println(...)APPLIED / id=null
```

(Piping into `jshell` echoes its prompt between statements — ignore the noise, the answer is
`APPLIED / id=null` at the end.)

`jshell` <sup>[J9](NOTES.md#jshell)</sup> is Java's REPL — the closest thing to `python3` at a prompt, and it will load your own
compiled classes if you point `--class-path` at them. Handy for exactly this: checking one method
without writing a `main`.

If `getStatus()` prints `null`, the constructor is missing its `this.status = Status.APPLIED;`. If
`id=0` rather than `id=null`, you declared `long` instead of `Long`.

---

## Step 3 — Collections: `List`, `Map`, and generics

You need somewhere to put many applications. Java's collections are typed:

```java
import java.util.ArrayList;
import java.util.List;

public class ListExample {

    /** Stand-in for the real Application, so this example runs on its own. */
    record Application(String company) { }

    public static void main(String[] args) {
        List<Application> applications = new ArrayList<>();
        applications.add(new Application("Acme Corp"));
        applications.add(new Application("Globex"));

        System.out.println(applications.size());
        System.out.println(applications.get(0));

        // applications.add("oops");   // uncomment this and the file stops compiling
    }
}
```

- [x] Run it without touching your project — save it as `ListExample.java` anywhere and run the file
  directly

```bash
java ListExample.java
```

```
2
Application[company=Acme Corp]
```

> `java SomeFile.java` runs a single source file with no `javac` step and no classpath
> <sup>[J11](NOTES.md#single-file-source-launch)</sup>. It's the right tool for a throwaway
> experiment — and it keeps scratch code *out* of `src/`, where a stray file would break
> `mvn compile` for the whole project.

Read `List<Application>` as "a List of Application." The `<Application>` is a **generic type parameter**
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
import java.util.HashMap;
import java.util.Map;

public class MapExample {

    /** Stand-in for the real Application, so this example runs on its own. */
    record Application(String company) { }

    public static void main(String[] args) {
        Map<Long, Application> byId = new HashMap<>();
        byId.put(1L, new Application("Acme Corp"));   // 1L is a long literal; plain 1 is an int

        Application found = byId.get(1L);
        System.out.println(found);

        System.out.println(byId.get(99L));            // absent key
    }
}
```

- [x] Same deal — save as `MapExample.java` and run it

```bash
java MapExample.java
```

```
Application[company=Acme Corp]
null
```

That second line is the one to remember: **`Map.get` returns `null` for a missing key**, it doesn't
throw. Python's `dict[k]` raises `KeyError`; Java hands you a `null` that stays quiet until you call
a method on it several lines later. Step 5 is about the fix.

`Map` is how you'll fake a database table in the capstone: id → row.

- [x] Try it in `Main` — replace the body with:

```java
package com.connorjensen.jobtracker;

import com.connorjensen.jobtracker.model.Application;
import com.connorjensen.jobtracker.model.Status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Application> applications = new ArrayList<>();
        applications.add(new Application("Acme Corp", "Backend Engineer", LocalDate.of(2026, 8, 1)));
        applications.add(new Application("Globex", "Platform Engineer", LocalDate.of(2026, 8, 5)));
        applications.add(new Application("Initech", "Java Developer", LocalDate.of(2026, 8, 9)));

        applications.get(1).setStatus(Status.INTERVIEWING);

        for (Application app : applications) {
            System.out.println(app.summary() + " [" + app.getStatus() + "]");
        }
    }
}
```

`for (Application app : applications)` is the **enhanced for loop** <sup>[J5](NOTES.md#enhanced-for-loop)</sup> — Java's `for x in list`.

Note the five imports above the class. `Status` needs one now even though `Application` already had
one — they're separate classes, and Java imports classes, not packages-worth-of-classes. (`import
com.connorjensen.jobtracker.model.*;` exists and is legal, but every style guide and IDE default
prefers the explicit list, because `*` hides where a name came from.)

### Verify

- [x] Compile and run it — this is the first step whose output you can actually read

```bash
mvn -q clean compile && java -cp target/classes com.connorjensen.jobtracker.Main
```

```
Acme Corp - Backend Engineer [APPLIED]
Globex - Platform Engineer [INTERVIEWING]
Initech - Java Developer [APPLIED]
```

Three lines, and only index 1 is `INTERVIEWING` — that's the `List` preserving insertion order and
`get(1)` being zero-based.

If you see `cannot find symbol: class List` or `variable Status`, you skipped an import. The error's
`location:` line tells you which file, and the `symbol:` line tells you which name to import.

---

## Step 4 — Streams: filtering without a loop

You could filter with a `for` loop and an `if`. Java's idiomatic answer is a **stream** <sup>[J8](NOTES.md#lambdas-streams-and-method-references)</sup>, and it
will look extremely familiar:

- [ ] Try it in `Main`

```java
public class Main {

    public static void main(String[] args) {
        // ... the List<Application> from Step 3 ...

        List<Application> interviewing = applications.stream()
                .filter(app -> app.getStatus() == Status.INTERVIEWING)
                .toList();
    }
}
```

That's JS `.filter()` with different punctuation. `app -> ...` is a **lambda** — an anonymous
function. `.stream()` opens a pipeline, `.filter()` describes a step, and `.toList()` **terminates**
it and produces a real list.

The one non-obvious rule: a stream is **lazy and single-use**. Nothing happens until a terminal
operation (`.toList()`, `.count()`, `.findFirst()`) runs, and you can't reuse the stream afterward.
It's a pipeline description, not a collection.

The other operations you'll actually use, in a file you can run as-is:

```java
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class StreamOpsExample {

    record Application(String company, LocalDate appliedDate) { }

    public static void main(String[] args) {
        List<Application> applications = List.of(
                new Application("Initech", LocalDate.of(2026, 8, 9)),
                new Application("Acme Corp", LocalDate.of(2026, 8, 1)),
                new Application("Globex", LocalDate.of(2026, 8, 5)));

        // transform each element
        List<String> companies = applications.stream()
                .map(Application::company)
                .toList();
        System.out.println(companies);

        // sort by a field
        List<String> byDate = applications.stream()
                .sorted(Comparator.comparing(Application::appliedDate))
                .map(Application::company)
                .toList();
        System.out.println(byDate);

        // count what survives a filter
        System.out.println(applications.stream()
                .filter(app -> app.company().startsWith("G"))
                .count());
    }
}
```

```bash
java StreamOpsExample.java
```

```
[Initech, Acme Corp, Globex]
[Acme Corp, Globex, Initech]
1
```

`Application::company` is a **method reference** — shorthand for `app -> app.company()`. (It's
`company()` and not `getCompany()` here only because this example uses a `record`; your real
`Application` is a class, so it's `Application::getCompany`.)

### Verify

- [ ] Print the filtered list's size, then compile and run

```java
public class Main {

    public static void main(String[] args) {
        // ... the stream from above ...

        System.out.println("interviewing: " + interviewing.size());
    }
}
```

```bash
mvn -q clean compile && java -cp target/classes com.connorjensen.jobtracker.Main
```

```
Acme Corp - Backend Engineer [APPLIED]
Globex - Platform Engineer [INTERVIEWING]
Initech - Java Developer [APPLIED]
interviewing: 1
```

One, not three — the filter ran. If you get a compile error on `.toList()`, that method is Java 16+;
on an older JDK it's `.collect(Collectors.toList())`.

- [ ] Now confirm the single-use rule for yourself — run a second terminal op on the *same* stream

Do this one in a scratch file rather than in `Main`, so you don't have to remember to undo it:

```java
import java.util.List;
import java.util.stream.Stream;

public class StreamReuseExample {

    public static void main(String[] args) {
        List<String> companies = List.of("Acme Corp", "Globex", "Initech");

        Stream<String> s = companies.stream();
        s.toList();          // terminal operation #1 — consumes the stream

        try {
            s.count();       // terminal operation #2 — on an already-closed stream
        } catch (IllegalStateException e) {
            System.out.println("caught: " + e.getMessage());
        }
    }
}
```

```bash
java StreamReuseExample.java
```

```
caught: stream has already been operated upon or closed
```

Without the `try`, that's an uncaught exception that kills the program. A stream is consumed by its
terminal operation — if you need to traverse twice, call `.stream()` twice.

---

## Step 5 — `Optional`: the return type that admits it might be empty

Here's the problem. You want "find the application with this id." Sometimes there isn't one. In
Python you return `None` and hope the caller checks. In Java, returning `null` has the same flaw,
and it's the single most common source of production crashes — `NullPointerException`.

`Optional<T>` <sup>[J8](NOTES.md#optional)</sup> is a box that either contains a value or doesn't, and **the type
signature says so**:

- [ ] Try it in `Main`, and handle the empty case at least two of the ways below

```java
public class Main {

    public static void main(String[] args) {
        // ... the List<Application> from Step 3 ...

        Optional<Application> found = applications.stream()
                .filter(app -> app.getCompany().equals("Globex"))
                .findFirst();
    }
}
```

`.findFirst()` returns `Optional<Application>` because it genuinely might not find one. Now the
caller *cannot* accidentally use the value without confronting the empty case. Here are the five
ways, in one file you can run:

```java
import java.util.List;
import java.util.Optional;

public class OptionalExample {

    record Application(String company, String role) {
        String summary() {
            return company + " - " + role;
        }
    }

    public static void main(String[] args) {
        List<Application> applications = List.of(
                new Application("Acme Corp", "Backend Engineer"),
                new Application("Globex", "Platform Engineer"));

        Optional<Application> found = applications.stream()
                .filter(app -> app.company().equals("Globex"))
                .findFirst();

        // 1. check, then unwrap — the verbose way
        if (found.isPresent()) {
            System.out.println(found.get().summary());
        }

        // 2. same thing with no branch at all
        found.ifPresent(app -> System.out.println(app.summary()));

        // 3. supply a fallback
        System.out.println(found.map(Application::company).orElse("not found"));

        // 4. or blow up loudly, with a message you chose
        Application required = found.orElseThrow(
                () -> new IllegalArgumentException("No Globex application"));
        System.out.println(required.company());

        // 5. the case that actually matters — nothing found, and nothing crashes
        Optional<Application> missing = applications.stream()
                .filter(app -> app.company().equals("Nope Inc"))
                .findFirst();
        System.out.println(missing.map(Application::company).orElse("not found"));
    }
}
```

```bash
java OptionalExample.java
```

```
Globex - Platform Engineer
Globex - Platform Engineer
Globex
Globex
not found
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

### Verify

The interesting case is the *empty* one — a search that finds nothing has to not crash.

- [ ] Search for a company that isn't there, and handle it without an `if`

```java
public class Main {

    public static void main(String[] args) {
        // ... everything from Step 4 ...

        Optional<Application> missing = applications.stream()
                .filter(app -> app.getCompany().equals("Nope Inc"))
                .findFirst();
        System.out.println("missing: " + missing.map(Application::getCompany).orElse("not found"));
    }
}
```

- [ ] Compile and run (you'll need `import java.util.Optional;`)

```bash
mvn -q clean compile && java -cp target/classes com.connorjensen.jobtracker.Main
```

```
Acme Corp - Backend Engineer [APPLIED]
Globex - Platform Engineer [INTERVIEWING]
Initech - Java Developer [APPLIED]
interviewing: 1
found: Globex - Platform Engineer
missing: not found
```

`.map()` on an empty `Optional` doesn't run the function and doesn't throw — it just stays empty, so
`.orElse()` supplies the fallback. That's the whole point: no null check, no `NullPointerException`,
and the compiler wouldn't have let you skip it.

- [ ] Finally, make sure you didn't break Lesson 1's tests

```bash
mvn clean test
```

```
Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

(`mvn compile` never compiles `src/test` — a step that leaves the tests broken will still look green
until you run `mvn test`. Worth remembering.)

---

## Self-check

- [ ] Answered all seven without opening the answers

1. You `git mv` a class into a new directory and change nothing else. Does `mvn compile` fail? Where
   does its `.class` file end up, and why?
2. What two lines of code must change to genuinely move a class to a new package, and where?
3. Why is `Status.APPLIED == Status.APPLIED` safe when `"APPLIED" == "APPLIED"` is not?
4. Why `List<Application> x = new ArrayList<>()` instead of `ArrayList<Application> x = ...`?
5. What happens if you call `.toList()` on the same stream twice?
6. Give one place `Optional` should *not* be used, and why.
7. Why `Long id` and not `long id`?

<details>
<summary>Answers</summary>

1. No — it compiles fine. The `.class` file lands at the path matching the **`package` declaration**,
   not the new directory. Maven hands javac an explicit list of every source file, so javac never has
   to search the directory tree and never notices the mismatch.
2. The `package` declaration in the moved file, and an `import` in every file that referenced it.
3. Each enum constant is a single instance, so identity comparison is value comparison. `String`s
   with the same content can be separate objects, so `==` may be false — use `.equals()`.
4. Declaring the interface type means the implementation can be swapped without changing any other
   code, and it stops you from depending on `ArrayList`-specific methods.
5. `IllegalStateException` — a stream is consumed by its terminal operation and cannot be reused.
6. As a field (not serializable, extra object per instance), as a parameter (noisy for callers), or
   wrapping a collection (an empty collection already conveys "none").
7. `Long` is an object and can be `null`, meaning "no id assigned yet." The primitive `long` would
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
