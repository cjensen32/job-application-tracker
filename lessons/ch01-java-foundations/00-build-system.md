# Lesson 0 — Build System & Project Shape

**Goal:** Understand what a JVM project *is* before a generator makes one for you. **You'll be able to answer:** *"Walk
me through what happens when you run `mvn package`."*

---

## Why Maven exists at all

In Flask, you `pip install flask`, then `import flask`. Python resolves that import **at runtime**
by scanning `site-packages`. There's no build step — the interpreter finds things when it needs them.

Java can't work that way. It's compiled ahead of time, so the **compiler** needs to know where every class lives, and
then the **JVM** needs to know again at startup. That list of locations is called the **classpath**, and managing it by
hand is miserable — a real app has 60+ jars with their own transitive dependencies.

Maven does three jobs:

1. **Fetches dependencies** (into `~/.m2/repository`, shared across all your projects)
2. **Builds the classpath** so you never type it
3. **Runs a standard build lifecycle** — the same commands work on every Maven project on earth

That third one matters more than it sounds. `npm run build` means whatever a project's author decided; `mvn package`
means the same thing everywhere.

---

## Step 1 — The directory layout

Maven is **convention over configuration**: it doesn't ask where your code is, it *requires* a specific layout. Create
it:

```bash
mkdir -p src/main/java/com/connorjensen/jobtracker
mkdir -p src/main/resources
mkdir -p src/test/java/com/connorjensen/jobtracker
```

| Path                 | Holds                                                    |
|----------------------|----------------------------------------------------------|
| `src/main/java`      | Your source code                                         |
| `src/main/resources` | Non-code files (config, later: `application.properties`) |
| `src/test/java`      | Tests — a *separate* source tree, compiled separately    |
| `target/`            | Build output. Generated. Never committed.                |

Two rules Java enforces that Python doesn't:

- **Package name must match directory path.** Code in `src/main/java/com/connorjensen/jobtracker`
  must declare `package com.connorjensen.jobtracker;`
- **File name must match the public class name.** `Main.java` must contain `public class Main`.

`com.connorjensen.jobtracker` is the reverse-domain convention — it's just a namespace, nothing needs to actually exist
at that domain.

> Remember this rigidity — it's the same philosophy that later lets Spring Boot auto-configure your
> whole app without config. Frameworks can make assumptions when layout is guaranteed.

---

## Step 2 — Write a minimal `pom.xml`

Create `pom.xml` in the repo root. Type it rather than pasting, and read the annotations below:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.connorjensen</groupId>
    <artifactId>job-application-tracker</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.release>21</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

</project>
```

**What each piece is:**

- **`<modelVersion>`** — the version of the POM *format*, not your project. It has been `4.0.0` for twenty years. Always
  this.
- **Coordinates** (`groupId` + `artifactId` + `version`) — the globally unique address of your artifact, like an npm
  `name` but namespaced to avoid collisions. Every dependency you ever add is identified by these same three fields.
- **`-SNAPSHOT`** — means "in development, contents may change." Maven re-checks snapshots for updates; released
  versions are treated as immutable forever. This is why you never republish a release version.
- **`<packaging>`** — what to produce. `jar` is the default (a zip of compiled classes + metadata).
- **`<maven.compiler.release>21</...>`** — compile targeting Java 21. This is what unlocks `record`
  in Lesson 1. <sup>[J9](NOTES.md#mavencompilerrelease)</sup>

---

## Step 3 — Compile and run something

Create `src/main/java/com/connorjensen/jobtracker/Main.java`:

```java
package com.connorjensen.jobtracker;

public class Main {
    public static void main(String[] args) {
        System.out.println("Job Application Tracker — build works.");
    }
}
```

Then:

```bash
mvn compile
```

Now look at what appeared:

```bash
find target -type f
```

You should see `target/classes/com/connorjensen/jobtracker/Main.class` — **bytecode**, not source. The directory
structure inside `target/classes` mirrors your package structure, because that's how the JVM locates a class at runtime.

Run it directly, without Maven:

```bash
java -cp target/classes com.connorjensen.jobtracker.Main
```

That `-cp target/classes` **is the classpath** — you're telling the JVM "start looking for classes here." Then you name
the class by its fully-qualified name, not its file path. This is the thing Maven builds for you once there are 60 jars
instead of one directory.

---

## Step 4 — Add a dependency, and see scope

Add this inside `<project>`, after `</properties>`:

```xml

<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.11.3</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>3.5.2</version>
    </plugin>
</plugins>
</build>
```

**`<scope>test</scope>`** is the concept here. It means JUnit is on the classpath when compiling and running *tests*,
but is **excluded from your shipped jar**. Your production artifact shouldn't carry a test framework. The scopes you'll
actually meet:

| Scope      | Meaning                                                    |
|------------|------------------------------------------------------------|
| `compile`  | Default. Available everywhere, shipped.                    |
| `test`     | Test code only, not shipped.                               |
| `provided` | Needed to compile, but supplied at runtime by the server.  |
| `runtime`  | Not needed to compile, needed to run (e.g. a JDBC driver). |

**Why the surefire plugin block?** Surefire is the plugin that runs your tests. Maven's built-in default binding is
ancient (2.12.4) and **silently ignores JUnit 5 tests** — they don't fail, they just don't run, which is a genuinely
nasty first bug. Pinning 3.5.2 fixes it. This is the only plugin config you need until Spring Boot arrives.

Verify nothing broke:

```bash
mvn test
```

You'll see `No tests to run` — correct, you haven't written one. Lesson 1 does.

---

## Step 5 — Package it

```bash
mvn package
find target -name "*.jar"
```

You now have `target/job-application-tracker-1.0-SNAPSHOT.jar` — note the filename is literally
`artifactId-version.jar`.

Try to run it:

```bash
java -jar target/job-application-tracker-1.0-SNAPSHOT.jar
```

**It fails**: `no main manifest attribute`. That's expected and worth understanding — a plain jar is just a zip of
classes with no record of which one to start. Making it executable requires extra config, and later the Spring Boot
Maven plugin will do exactly that (plus bundle every dependency inside). Now you'll know what that plugin is *for*
instead of it being magic.

---

## The lifecycle (this is your interview answer)

Maven phases run **in order, cumulatively**. Naming a phase runs everything before it too:

```
validate → compile → test → package → verify → install → deploy
```

So `mvn package` actually:

1. **validate** — check the POM is coherent
2. **compile** — `src/main/java` → `target/classes`
3. **test** — compile `src/test/java` → `target/test-classes`, then run them via surefire (**a failing test stops the
   build here**)
4. **package** — zip `target/classes` into `target/*.jar`

`mvn install` goes further and copies the jar into `~/.m2/repository` so *other* local projects can depend on it.
`mvn clean` isn't in this list — it's a separate lifecycle that deletes `target/`, which is why you often see
`mvn clean package`.

---

## Self-check

Answer these out loud before moving on. (No quiz yet — XP is banked once at the end of the chapter,
after the capstone.)

1. Why does Java need a classpath when Python doesn't?
2. What's the difference between `target/classes` and `target/*.jar`?
3. If you add a dependency with `<scope>test</scope>`, can your `Main.java` import it?
4. You run `mvn package` and a test fails. Do you get a jar?
5. What are the three coordinates that uniquely identify any Maven artifact?

<details>
<summary>Answers</summary>

1. Java compiles ahead of time — both `javac` and the JVM need to be told where classes live. Python resolves imports at
   runtime by scanning `site-packages`.
2. `target/classes` is loose `.class` files (what you compiled); the jar is those files zipped with metadata, ready to
   distribute.
3. **No.** `test` scope puts it only on the test classpath — `src/main/java` can't see it. That's the whole point.
4. **No.** `test` runs before `package`, and a failure halts the lifecycle.
5. `groupId`, `artifactId`, `version`.

</details>

---

**Next:** [Lesson 1 — Classes, Objects, and Records](01-classes-and-records.md) ·
**Version history:** [NOTES.md](NOTES.md)
