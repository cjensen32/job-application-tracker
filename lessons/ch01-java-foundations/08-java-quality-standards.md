# Lesson 8 — Java Quality Standards

**Goal:** use shared project conventions and automated checks to make review feedback consistent and actionable.

**Prerequisite:** Lesson 7's testable CLI architecture.

**Progress**

- [x] Distinguish Java rules from Maven course conventions
- [x] Read and fix Checkstyle categories
- [x] Use compiler warnings and a review checklist
- [x] Complete the self-check

---

## Step 1 — Distinguish Java rules from Maven course conventions

Java can compile files from many layouts when given the right paths. This course adopts [Maven's standard directory layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout) so people and tools know where to look.

- [x] Keep production packages beneath `src/main/java`.
- [x] Keep JUnit tests beneath `src/test/java`.
- [x] Keep build output beneath `target`.
- [x] Keep package declarations aligned with source paths.
- [x] Read the [course standards](../course-standards/README.md).

Calling the layout a convention matters. It explains why Maven discovers files automatically without incorrectly teaching that the Java language requires Maven.

### Verify

- [x] Ask Maven to compile the conventional source tree.

```bash
mvn -q -Dcheckstyle.skip -DskipTests compile && echo "layout: recognized"
```

```text
layout: recognized
```

If Maven reports no sources, check the source path before changing the POM.

## Step 2 — Read and fix Checkstyle categories

The repository pins Maven Checkstyle Plugin 3.6.0 and Checkstyle 13.9.0. Checkstyle 13.x requires JDK 21 and understands Java 21 syntax. The check runs in `validate`, includes test sources, and uses `config/checkstyle/checkstyle.xml`.

It enforces:

- Two-space indentation and 100-column Java lines.
- Conventional package, type, method, variable, and constant names.
- Ordered explicit imports with no wildcard, redundant, or unused imports.
- Consistent braces and whitespace.
- One statement per line.
- No empty catch blocks or accidental switch fall-through.
- Paired `equals` and `hashCode` overrides.
- Hidden constructors for utility classes.

Checkstyle reports deterministic violations. It does not automatically rewrite code and cannot judge whether a class has a good responsibility.

### Verify

- [x] Run the style gate by itself after the capstone refactor.

```bash
mvn -q checkstyle:check && echo "checkstyle: ok"
```

```text
checkstyle: ok
```

If a message names a line and module, fix that category first and rerun before mixing in behavior changes.

## Step 3 — Use compiler warnings and a review checklist

The compiler runs with `-Xlint:all`. Warnings remain review work even when they do not block the build.

- [x] Remove unused code rather than suppressing warnings reflexively.
- [x] Confirm every catch block translates or deliberately propagates a failure.
- [x] Confirm required fields cannot become blank and IDs stay positive.
- [x] Confirm public API matches the capstone contract and helpers remain hidden.
- [x] Confirm the renderer does not mutate inputs or write global output.
- [x] Confirm `Main` only composes dependencies and calls `run()`.
- [x] Confirm tests cover retry, missing-data, quit, and EOF paths.

### Verify

- [x] Run the complete build gate over the work this chapter has finished.

The capstone grader `Chapter01CapstoneTest` is already installed under `src/test/java`, and it fails on purpose until the [capstone](CAPSTONE.md) is written. Exclude it to gate the lessons you have actually completed.

```bash
mvn verify -Dtest='!com.connorjensen.jobtracker.capstone.**' -DfailIfNoSpecifiedTests=false
```

```text
[INFO] BUILD SUCCESS
```

The unfiltered command is the capstone's completion gate, not this lesson's:

```bash
mvn verify
```

Run it now anyway and read the failure. Checkstyle passes in `validate`, compilation passes, and every failure lands in the test phase from console behavior the capstone has not built yet. That ordering is the point of the gate.

If `verify` fails, work from the first lifecycle failure: validation before compilation, compilation before tests, and tests before reports.

## Self-check

1. Why does Checkstyle run in `validate` rather than only from an IDE?
2. Why is Maven's directory layout a convention rather than a Java requirement?
3. What is the difference between a formatter and Checkstyle in this course?
4. Why are compiler warnings still review work when they are non-blocking?

<details>
<summary>Answers</summary>

1. Every developer and CI run gets the same gate before later build phases.
2. Java tools accept explicit source and class paths; Maven supplies predictable defaults.
3. A formatter rewrites layout, while Checkstyle reports configured violations deterministically.
4. A warning may signal a bug or maintainability issue even when compilation can continue.

</details>

## Where this lands you

Chapter 1's concepts now include language, design, console boundaries, tests, and quality gates. The capstone combines them into one deliberately challenging behavioral contract.

**Previous:** [Lesson 7 — Testing Console Applications](07-testing-console-applications.md) · **Next:** [Chapter 1 Capstone — The Tracker Core](CAPSTONE.md) · **Chapter:** [README](README.md)
