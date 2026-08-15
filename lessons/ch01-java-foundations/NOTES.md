# Chapter 1 — Version Notes

*Why the Java in this chapter looks the way it does, and what it looked like before.*

None of this is required to finish the chapter. It's here because knowing "this used to be X, and changed to Y because
Z" is genuinely useful when you land in a codebase pinned to Java 8 — which is most of them.

**How to read the tiers:**

| Tier                 | Version | Why it matters                                                                                 |
|----------------------|---------|------------------------------------------------------------------------------------------------|
| **Modern**           | 16–21   | What this project targets                                                                      |
| **Practical legacy** | 8       | Still the single most-deployed version. If you have to backport, this is the realistic target. |
| **Deep legacy**      | 5       | The last time the language changed this much at once                                           |
| **Curiosity**        | 1.1–1.4 | Pre-generics, pre-collections. Fun, rarely encountered.                                        |

**The rule that explains most of this file:** the JDK will accept ugly language design before it will break code that
already compiles. Almost every "why is it like that" answer below bottoms out there.

---

## Records

`record ApplicationDto(String company, String role, LocalDate appliedDate) {}` — **Java 16** (2021), previewed in 14 and 15.

**Before (Java 8 and earlier):** the same thing by hand — private final fields, an all-args constructor, accessors, and hand-written `equals`, `hashCode`, and `toString`. Roughly 60 lines for three fields, and `equals`/`hashCode` written by hand is a classic source of subtle bugs (forget a field in `hashCode` and your `HashMap` lookups silently fail). IDEs generated it; Lombok's`@Value` annotation automated it; Java eventually absorbed the idea.

**Why accessors are `company()` and not `getCompany()`:** this was contentious. Records deliberately broke the JavaBean convention because a record is a *transparent carrier for its state*, not an object hiding fields behind an API. The practical consequence you'll hit: Jackson needed explicit record support (added 2.12) because its default logic looks for `getX()`.

**Backport difficulty:** trivial and mechanical. This is the easiest thing in the chapter to write for Java 8.

---

## Enums

`enum Status { APPLIED, ... }` — **Java 5** (2004).

**Before:** the *typesafe enum pattern*, popularized by Joshua Bloch in *Effective Java* — and the language feature is essentially that pattern promoted into syntax:

```java
public final class Status {
    public static final Status APPLIED = new Status("APPLIED");
    public static final Status OFFER = new Status("OFFER");

    private final String name;

    private Status(String name) {
        this.name = name;
    }   // private = nobody else can make one

    public String toString() {
        return name;
    }
}
```

That's why enum constants work with `==`: they always were singletons. The language just stopped making you write it.

**Before *that* (Java 1.x):** `public static final int STATUS_APPLIED = 0;`. Type-unsafe — nothing stopped you passing`7`, or passing a day-of-week constant where a status was expected, and printing one gave you `0` instead of a name. This is exactly the "stringly typed" problem Lesson 2 describes, and it's why the pattern was invented.

**Backport difficulty:** moderate. The pattern above works, but you lose `switch` support, `values()`, and `valueOf()`unless you write them.

---

## Generics

`List<Application>` — **Java 5**.

**Before:** collections held `Object`, and every read required a cast:

```java
List applications = new ArrayList();
applications.

add(app);

Application a = (Application) applications.get(0);   // ClassCastException at runtime if wrong
```

**The `z` in "x changed to y because z":** Java 5 had to let Java 1.4 code and Java 5 code share the same jars in the same JVM. The solution was **type erasure** — generics exist at compile time and are *erased* in the bytecode, so`List<String>` and `List<Integer>` are both just `List` at runtime. Old code could pass a raw `List` to new code and vice versa.

The price, still paid today:

- You can't write `new T[]`.
- You can't overload on `List<String>` vs `List<Integer>` — same erased signature.
- `instanceof List<String>` is illegal.
- Raw types still compile, with a warning, purely for compatibility.

C# made the opposite choice two years later (reified generics, at the cost of breaking its 1.0 collections). Whether Java chose right is a genuinely good interview conversation.

**Backport difficulty:** easy but ugly — delete the type parameters and add casts.

---

## The diamond operator

`new ArrayList<>()` — **Java 7** (2011).

**Before:** `List<Map<String, List<Application>>> x = new ArrayList<Map<String, List<Application>>>();` Purely a typing-effort fix, no semantic change.

---

## Enhanced for loop

`for (Application app : applications)` — **Java 5**.

**Before:** with the Collections Framework (1.2+), an explicit `Iterator`:

```java
for(Iterator it = applications.iterator(); it.

hasNext(); ){
Application app = (Application) it.next();
}
```

**Before 1.2:** `Enumeration` with `hasMoreElements()`/`nextElement()`, over a `Vector`.

---

## Lambdas, streams, and method references

`.stream().filter(app -> ...).toList()` — **Java 8** (2014). `Application::getCompany` too.

**Before:** a loop with an `if`. For anything callback-shaped, an **anonymous inner class**:

```java
Collections.sort(applications, new Comparator() {
    public int compare (Object a, Object b){
        return ((Application) a).getAppliedDate().compareTo(((Application) b).getAppliedDate());
    }
});
```

Six lines of ceremony around one line of logic. Lambdas are, at the bytecode level, not just sugar for this — they compile to an `invokedynamic` instruction that builds the implementation at runtime, specifically so the JVM wasn't flooded with one extra class file per lambda.

**The compatibility story worth knowing:** adding `stream()` to `Collection` should have been impossible — every existing class implementing `Collection` would have stopped compiling. So Java 8 invented **default methods**(`interface Collection { default Stream<E> stream() { ... } }`) to allow adding methods to published interfaces. An entire language feature exists so the standard library could grow without breaking the world. That's the single best example of Java's compatibility priorities shaping the language.

**`.toList()` on a stream is newer than streams:** **Java 16**. From 8 to 15 it was
`.collect(Collectors.toList())`. You will see the old form constantly. Small difference with teeth —
`.toList()` returns an *unmodifiable* list, `Collectors.toList()` returns a mutable `ArrayList`.

**Backport difficulty to Java 8:** trivial (swap `.toList()` for `.collect(Collectors.toList())`). **To Java 7:**rewrite as loops.

---

## Optional

`Optional<Application>` — **Java 8**.

**Before:** return `null` and document it in a comment, or don't. This is the "billion dollar mistake" its inventor Tony Hoare apologized for — null references date to ALGOL W in 1965, and Java inherited them.

Google's Guava library shipped an `Optional` in 2011; the JDK's version arrived three years later, designed primarily to give `Stream` a return type for `findFirst()` that couldn't be `null`. That origin is why the official guidance is *return types only* — it wasn't designed as a general-purpose nullable field wrapper, which is exactly the misuse Lesson 2 warns about.

**Backport difficulty:** easy — return `null` and null-check. You lose the compiler's nagging, which was the entire point.

---

## `Long` vs `long`, and autoboxing

Wrapper types (`Long`, `Integer`) date to **Java 1.0**. **Autoboxing** — writing `Long id = 5L;` and letting the compiler convert — is **Java 5**.

**Before:** `Long id = new Long(5);` and `long raw = id.longValue();`, explicitly, every time.

Autoboxing removed the ceremony and introduced a famous trap that still bites:

```java
Long a = 127L, b = 127L;
a ==b;              // true  — small values are cached
        Long c = 128L, d = 128L;
c ==d;              // false — different objects
```

The `Integer`/`Long` cache for −128..127 is *required* by the spec, so the bug is deterministic and looks like magic. Always `.equals()` on wrappers. This is a very common interview trick question.

---

## `java.time` (`LocalDate`)

**Java 8**. Before that, `java.util.Date` and `Calendar`.

The old API was bad in specific, memorable ways: `Date` was mutable, months were **0-indexed** (December is 11), years counted from 1900, and `SimpleDateFormat` was not thread-safe — a genuinely common production bug when someone made one a static field.

`java.time` came from Joda-Time via JSR-310, written by the same author. The old classes couldn't be fixed in place because too much code depended on their behaviour, bugs included — so the fix was a whole new package, and`java.util.Date` remains, deprecated in parts, forever.

**Backport difficulty:** annoying. `Calendar` handling is verbose and the semantics differ. Joda-Time is the usual answer on Java 7 and below.

---

## Annotations

`@Test`, `@Override` — **Java 5**.

**Before:** JUnit 3 found tests by **method naming convention** — any method starting with `test`, in a class extending`TestCase`. Marker interfaces (`Serializable`, `Cloneable`) played a similar role:implement an empty interface to signal metadata.

Everything in Chapters 2 through 5 — every `@Service`, `@Entity`, `@GetMapping` — depends on this one Java 5 feature plus reflection. Spring predates annotations (2003) and originally did all its wiring in XML, which is why you'll still find `applicationContext.xml` in older enterprise code. If you work in deprecated codebases, you *will* meet XML-configured Spring.

`@Override` is worth a note: it's checked at compile time, but it's an ordinary annotation, not a keyword. Java 5 only allowed it on class-method overrides; **Java 6** extended it to interface implementations — so on Java 5, the`@Override` in Lesson 3's repository would have been a compile error.

---

## `maven.compiler.release`

The `<maven.compiler.release>21</...>` in your `pom.xml` is **Java 9+**.

**Before:** `<maven.compiler.source>` and `<target>`, which had a real flaw — you could compile targeting Java 6 bytecode while accidentally calling Java 8 library methods, producing a jar that compiled fine and threw`NoSuchMethodError` at runtime on an actual Java 6 JVM. `release` checks the API surface too, so it can't happen.

**If you ever backport this project, `release` is the one flag that makes it honest.** Set it to 8 and the compiler will reject every Java 9+ API you use.

---

## jshell

Java's REPL, **Java 9**. `jshell --class-path target/classes` drops you at a prompt with your own compiled classes loaded, and accepts bare expressions and statements with no enclosing class or`main`.

**Before:** there wasn't one. For eighteen years, "try one line of Java" meant writing a throwaway class with a`public static void main(String[] args)`, compiling it, and running it — which is a large part of why Java earned its ceremony reputation next to `python3` at a prompt. The workarounds were IDE "scratch files" (IntelliJ) and third-party shells like BeanShell and Groovy.

**Why it arrived so late:** it needed the JVM to support redefining classes and re-executing snippets cheaply, which is the same machinery Java 9's module system and `JEP 222` work made tractable. It's also the one Java 9 feature with *zero* backport story — it's a tool, not a language feature, so there's no "Java 8 equivalent" beyond installing Groovy.

**On a Java 8 project:** you can still use a newer JDK's `jshell` against Java 8-compiled classes. The tool doesn't care what compiled them.

---

## Quick reference — everything in Chapter 1

| Feature                          | Introduced | Java 8 equivalent                                      |
|----------------------------------|------------|--------------------------------------------------------|
| `record`                         | 16         | hand-written class with `equals`/`hashCode`/`toString` |
| `.toList()` on Stream            | 16         | `.collect(Collectors.toList())`                        |
| `var`                            | 10         | explicit types                                         |
| `List.of(...)`                   | 9          | `Collections.unmodifiableList(Arrays.asList(...))`     |
| `jshell`                         | 9          | a throwaway class with `main`, or Groovy/BeanShell     |
| `release` compiler flag          | 9          | `source` + `target`                                    |
| streams, lambdas, method refs    | 8          | loops, anonymous inner classes                         |
| `Optional`                       | 8          | `null` + null checks                                   |
| default interface methods        | 8          | abstract base class                                    |
| `java.time`                      | 8          | `Calendar`, or Joda-Time                               |
| diamond `<>`                     | 7          | repeat the type arguments                              |
| try-with-resources               | 7          | `finally { close(); }`                                 |
| `@Override` on interface methods | 6          | omit it                                                |
| generics                         | 5          | raw types + casts                                      |
| `enum`                           | 5          | typesafe enum pattern                                  |
| annotations                      | 5          | naming conventions, marker interfaces, XML             |
| enhanced for                     | 5          | explicit `Iterator`                                    |
| autoboxing                       | 5          | `new Long(x)` / `x.longValue()`                        |
| Collections Framework            | 1.2        | `Vector`, `Hashtable`, `Enumeration`                   |

---

**Chapter:** [Chapter 1 — Java Foundations](README.md) · **Terms:** [GLOSSARY.md](GLOSSARY.md)
