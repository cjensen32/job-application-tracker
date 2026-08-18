# Lesson 6 — Refactoring and Package Design

**Goal:** split the console code by responsibility, make dependencies visible, and keep the public API intentionally small.

**Prerequisite:** Lesson 5's injected I/O and validation rules.

**Progress**

- [ ] Replace the vague utility package with role-specific CLI classes
- [ ] Introduce request records and service operations
- [ ] Reduce visibility and keep Main as the composition root
- [ ] Complete the self-check

---

## Step 1 — Split by reasons to change

`util` often becomes a drawer for code whose responsibility has not been named. A package name should help predict why its classes change.

- [ ] Create `com.connorjensen.jobtracker.cli`.
- [ ] Move session coordination into `ConsoleApplication`.
- [ ] Move input, parsing, and local retry loops into `ConsolePrompter`.
- [ ] Move messages, details, and tables into `ConsoleView`.
- [ ] Replace stateful `TextTable` and `Centering` with pure `TextTable`.
- [ ] Remove the obsolete CLI utility classes only after every caller has moved.

The split follows cohesion:

| Class              | Reason to change                    |
|--------------------|-------------------------------------|
| ConsoleApplication | Menu workflow changes               |
| ConsolePrompter    | Input and validation policy changes |
| ConsoleView        | User-facing text changes            |
| TextTable          | Rendering algorithm changes         |

### Verify

- [ ] Confirm production code no longer imports the old CLI utilities.

```bash
if rg -n 'ConsoleExperience|ApplicationTextTable|Centering' src/main/java; then exit 1; else echo "old CLI: removed"; fi
```

```text
old CLI: removed
```

If a match remains, move that responsibility before deleting its old class.

## Step 2 — Introduce request records and service operations

A service method with six scalar parameters is easy to call in the wrong order and painful to extend. A request record gives the operation one named typed input.

- [ ] Add `CreateApplicationRequest` to the service package.
- [ ] Add `UpdateApplicationRequest` to the service package.
- [ ] Add `create(CreateApplicationRequest)` and `update(Long, UpdateApplicationRequest)`.
- [ ] Keep the three-argument create overload and `updateStatus` for compatibility and focused operations.

```java
public record CreateApplicationRequest(
    String company,
    String role,
    LocalDate appliedDate,
    String notes,
    String jobUrl) {
}
```

The update record additionally carries `Status`. It is an immutable instruction; `Application` remains the mutable domain object whose saved state changes.

### Verify

- [ ] Compile the service contract and request records.

```bash
mvn -q -Dcheckstyle.skip -DskipTests compile && echo "requests: ok"
```

```text
requests: ok
```

If the CLI mutates the record, move that mutation to the loaded `Application` inside the service.

## Step 3 — Reduce visibility and keep Main as the composition root

Public is a promise to every caller. Package-private helpers remain flexible and prevent tests from freezing incidental method names.

- [ ] Keep only constructors, `ConsoleApplication.run()`, and `TextTable.render(...)` public in the CLI package.
- [ ] Keep helper methods private or package-private.
- [ ] Make `Main.main` construct the repository, service, scanner, table, view, prompter, and application.
- [ ] Make `Main.main` do nothing else except call `run()`.

Dependency direction is:

```text
Main → cli → service → repository contract → model
                    ↘ model
repository implementation → repository contract + model
```

This is a course architecture convention, not a restriction of the Java compiler.

### Verify

- [ ] Run the API-shape test after installing the capstone grader.

```bash
mvn -q -Dcheckstyle.skip -Dtest='Chapter01CapstoneTest$ArchitectureTests#cliPublicApiIsMinimal' test && echo "visibility: ok"
```

```text
visibility: ok
```

If a helper is unexpectedly public, first ask which package-external caller actually needs it.

## Self-check

1. Why is `ConsoleView` not a generic utility?
2. What does a request record improve over six scalar parameters?
3. Why preserve the existing three-argument create method?
4. Why should tests avoid naming private or package-private helper methods?

<details>
<summary>Answers</summary>

1. It has a specific responsibility and changes with the CLI's presentation contract.
2. It names every value, prevents ordering mistakes, and lets the operation evolve as one type.
3. It preserves compatibility and remains useful for focused service operations and earlier lessons.
4. Helper names are implementation details; behavior tests should allow the learner to design them.

</details>

## Where this lands you

The architecture has explicit seams and a small surface. Lesson 7 uses those seams to test behavior without leaking global state.

**Previous:** [Lesson 5 — Console I/O and Validation](05-console-io-and-validation.md) · **Next:** [Lesson 7 — Testing Console Applications](07-testing-console-applications.md) · **Chapter:** [README](README.md)
