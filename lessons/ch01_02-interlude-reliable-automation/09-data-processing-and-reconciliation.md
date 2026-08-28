# Lesson 9 — Data Processing and Reconciliation

**Goal:** turn the tracker's applications into a trustworthy status summary, prove every input was accounted for, and translate the same aggregation into Python/Pandas and SQL vocabulary.

**Prerequisite:** complete the [Chapter 1 capstone](../ch01-java-foundations/CAPSTONE.md). **Fast track:** the current checkout may begin this lesson once `mvn compile` succeeds and `ApplicationService.listAll()` plus `Status.values()` are available.

**Progress**

- [ ] Define a stable and read-only summary contract
- [ ] Count every status with an explicit loop
- [ ] Reconcile edge cases and translate the operation
- [ ] Read a failure back to the method that owns it
- [ ] Complete the self-check

---

## Initialization

This lesson adds one production type, one service operation, and one focused learner-visible test class. It does not change CRUD behavior, the Chapter 1 grader, the console, or any existing constructor.

- [x] Create `src/main/java/com/connorjensen/jobtracker/service/ApplicationSummary.java` with this dependency-closed shell.

```java
package com.connorjensen.jobtracker.service;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import com.connorjensen.jobtracker.model.Status;

public record ApplicationSummary(long total, Map<Status, Long> countsByStatus) {
  public ApplicationSummary {
    EnumMap<Status, Long> copy = new EnumMap<>(Status.class);
    copy.putAll(countsByStatus);
    countsByStatus = Collections.unmodifiableMap(copy);
  }
}
```

The compact constructor copies into an `EnumMap` before wrapping it. Callers cannot mutate the record through either the original map or the returned accessor, and enum iteration order remains the declaration order from `Status`.

- [x] Add this unfinished contract to the existing `ApplicationService` after `listAll()`; preserve every existing field, constructor, and operation.

```java
public ApplicationSummary summarize() {
  throw new UnsupportedOperationException("TODO");
}
```

- [ ] Create the compilable test shell at `src/test/java/com/connorjensen/jobtracker/service/ApplicationSummaryTest.java`.

```java
package com.connorjensen.jobtracker.service;

import org.junit.jupiter.api.DisplayName;

@DisplayName("application summary")
class ApplicationSummaryTest {
}
```

The service already owns the repository boundary, so `summarize()` belongs beside `listAll()` rather than in the console or repository. Later, a controller can expose the same result without moving the aggregation out of the service.

### Who writes which tests

You wrote no tests during the Chapter 1 capstone — the grader arrived finished, and its failures pointed only at itself. From this lesson forward the course splits that work, and you own the near half. The [test-writing conventions](../ch00-testing-standards/README.md) carry the full rule.

| Layer      | You write in lessons                                     | A capstone grader brings                          |
|------------|----------------------------------------------------------|---------------------------------------------------|
| Plumbing   | build the fixture, call the operation, capture the value | —                                                 |
| Assertions | one fact per assertion                                   | ordered multi-step scenario composition           |
| Reporting  | `Owner:` and `Expected:` on the assertion message        | the same fields plus a windowed output transcript |
| Inputs     | happy path plus one edge case                            | EOF, malformed input, unknown ids, retries        |

Every assertion you write in this lesson carries a two-line message:

```java
"""
Owner:    ApplicationService.summarize()
Expected: a total equal to the number of rows in the repository snapshot"""
```

`Owner:` names the production method that must change when the assertion fails. `Expected:` states the requirement in words, not in code. Step 4 shows why that message is the only thing standing between you and an unreadable failure.

## Step 1 — Define a stable and read-only summary contract

A dashboard consumer should always receive one key per `Status`, even when its count is zero. Missing and zero are different messages: missing could mean the category was not calculated, while zero says it was calculated and no rows matched.

- [ ] In `ApplicationSummaryTest`, add `copiesCountsAtConstruction()` using a mutable `EnumMap`, construct the record, mutate the original map, and prove the record retained the original count.
- [ ] In the same test, use `assertThrows(UnsupportedOperationException.class, ...)` to prove `summary.countsByStatus().put(...)` is rejected.
- [ ] Give both assertions an `Owner:` / `Expected:` message naming the `ApplicationSummary` compact constructor.

Keep the test focused on the record boundary. It should not construct a repository or call `summarize()` yet.

### Verify

- [ ] Run the record contract test.

```bash
mvn -Dtest='ApplicationSummaryTest#copiesCountsAtConstruction' test
```

```text
[INFO] Running application summary
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.033 s -- in application summary
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

Surefire prints `Running application summary` rather than the class name because `pom.xml` configures phrased test names; the `@DisplayName` in the shell is what produces it. If Maven reports `No tests matching pattern`, check the class, method, package, and `@Test` annotation before changing production code.

## Step 2 — Count every status with an explicit loop

Start with the mechanism you can debug one line at a time. A stream can express this aggregation later, but it does not remove the need to decide the initial categories, source of truth, or correctness rule.

- [ ] Add `summarizesMixedStatusesAndIncludesZeros()` to `ApplicationSummaryTest`.
- [ ] Construct a real `InMemoryApplicationRepository` and inject it into `ApplicationService`.
- [ ] Create three applications through the service, move two to `INTERVIEWING` with `updateStatus(...)`, and assert `total()` is `3`.
- [ ] Build the five expected counts in an `EnumMap` and compare that whole map in one `assertEquals`: `APPLIED=1`, `PHONE_SCREEN=0`, `INTERVIEWING=2`, `OFFER=0`, `REJECTED=0`.
- [ ] Give both assertions an `Owner:` / `Expected:` message naming `ApplicationService.summarize()`.
- [ ] Replace the `UnsupportedOperationException` in `ApplicationService.summarize()` with your own implementation using this algorithm, not copied production code.

```text
1. Read the repository once and keep that list as the source snapshot.
2. Create an EnumMap keyed by Status.
3. Seed every value from Status.values() with 0L.
4. Walk the applications once and increment the matching status.
5. Return ApplicationSummary with the snapshot size and counts.
```

Assert the whole map in one `assertEquals` rather than five separate `assertEquals` calls on individual keys. Five independent assertions report only the first key that differs and say nothing about the other four; one map comparison prints both maps side by side. Use an `EnumMap`, not `Map.of(...)`, for the expected value so a failure prints both sides in `Status` declaration order; `Map.of(...)` equality is correct but its iteration order is unspecified. Use `long`/`Long` counts because `ApplicationSummary` declares them.

### Verify

- [ ] Run the record and mixed-status tests together.

```bash
mvn -Dtest='ApplicationSummaryTest' test
```

```text
[INFO] Running application summary
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.031 s -- in application summary
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

If a zero-count assertion returns `null`, seed every enum value before processing applications rather than adding keys only when rows exist.

## Step 3 — Reconcile edge cases and translate the operation

Aggregation is not trustworthy merely because it returned numbers. Reconciliation compares independently meaningful facts: the source snapshot size and the sum of all category counts.

- [ ] Add `summarizesAnEmptyRepository()` and assert total zero and `Status.values().length` keys, each with its own `Owner:` / `Expected:` message.
- [ ] Add `countsReconcileWithTotal()` using at least four applications across three statuses, then sum `summary.countsByStatus().values()` and compare it with `summary.total()`.
- [ ] Add the same invariant inside `summarize()`. Throw `IllegalStateException` if the sum of the counts does not equal the source snapshot size, and put both numbers in the message.
- [ ] Explain the translation aloud using the table below without claiming that this project executed Python or SQL.

| Decision           | Java in this lesson                   | Python/Pandas vocabulary                   | SQL vocabulary                                    |
|--------------------|---------------------------------------|--------------------------------------------|---------------------------------------------------|
| Source rows        | `repository.findAll()`                | list of objects or DataFrame rows          | `applications` table                              |
| Grouping key       | `application.getStatus()`             | key function or `groupby("status")`        | `GROUP BY status`                                 |
| Aggregate          | increment an `EnumMap` count          | `Counter` or `.size()`                     | `COUNT(*)`                                        |
| Missing categories | seed `Status.values()` with zero      | reindex with all categories and fill zero  | join grouped counts to a status reference set     |
| Reconciliation     | sum counts and compare with list size | grouped sum compared with source row count | grouped-count sum compared with `SELECT COUNT(*)` |

Paper SQL for the grouped part is:

```sql
SELECT status, COUNT(*) AS application_count
FROM applications
GROUP BY status;
```

That query normally omits absent statuses, so the deterministic five-key shape needs an additional status reference set plus an outer join, or it can be completed in the application layer. At database scale, let the database aggregate instead of loading every row into JVM memory, then reconcile the grouped result against a total-count query from the same consistent snapshot.

### Verify

- [ ] Run all four focused tests.

```bash
mvn -Dtest='ApplicationSummaryTest' test
```

```text
[INFO] Running application summary
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.027 s -- in application summary
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

If `countsReconcileWithTotal` passes but the guard inside `summarize()` never fires in Step 4, the guard is comparing the counts against themselves instead of against the snapshot size.

## Step 4 — Read a failure back to the method that owns it

Break the code first and look at what the failure actually names. Read the explanation afterwards.

- [ ] Delete the loop that seeds every `Status` with `0L`, so `summarize()` returns only the statuses that occur.
- [ ] Run the mixed-status test and read the stack trace.

```bash
mvn -Dtest='ApplicationSummaryTest#summarizesMixedStatusesAndIncludesZeros' test
```

```text
org.opentest4j.AssertionFailedError:
Owner:    ApplicationService.summarize()
Expected: one entry per Status, with 0 for statuses no application currently has ==> expected: <{APPLIED=1, PHONE_SCREEN=0, INTERVIEWING=2, OFFER=0, REJECTED=0}> but was: <{APPLIED=1, INTERVIEWING=2}>
	at org.junit.jupiter.api.AssertionFailureBuilder.build(AssertionFailureBuilder.java:151)
	at org.junit.jupiter.api.AssertionFailureBuilder.buildAndThrow(AssertionFailureBuilder.java:132)
	at org.junit.jupiter.api.AssertEquals.failNotEqual(AssertEquals.java:197)
	at org.junit.jupiter.api.AssertEquals.assertEquals(AssertEquals.java:182)
	at org.junit.jupiter.api.Assertions.assertEquals(Assertions.java:1156)
	at com.connorjensen.jobtracker.service.ApplicationSummaryTest.summarizesMixedStatusesAndIncludesZeros(ApplicationSummaryTest.java:79)
```

The bug is in `ApplicationService.summarize()`. **No frame in that stack trace is `ApplicationService.summarize()`.** The only file of yours in it is the test.

That is not a defect in the trace. Two different events end a test, and only one of them records your code:

- **An exception:** something threw. The JVM captured the live method chain at the moment of the throw, so every one of your methods on the way down appears by file and line.
- **An assertion failure:** nothing threw. `summarize()` ran to completion and returned a value the test disliked. JUnit throws at the assert line, *after* your method already returned and its frame was discarded.

Most failures you will chase are the second kind, including every capstone failure that made the grader look unhelpful. The stack trace is structurally incapable of naming the guilty method, so the `Owner:` line is a manufactured replacement for a frame that cannot exist — not a decoration on top of one.

Now compare it with the failure shape your own guard produces.

- [ ] Restore the zero-seeding loop, then make the counting loop skip `Status.APPLIED` so the counts genuinely disagree with the snapshot.
- [ ] Run the same test again.

```bash
mvn -Dtest='ApplicationSummaryTest#summarizesMixedStatusesAndIncludesZeros' test
```

```text
java.lang.IllegalStateException: Summary counted 2 of 3 source rows
	at com.connorjensen.jobtracker.service.ApplicationService.summarize(ApplicationService.java:77)
	at com.connorjensen.jobtracker.service.ApplicationSummaryTest.summarizesMixedStatusesAndIncludesZeros(ApplicationSummaryTest.java:58)
```

`ApplicationService.java:77` — the exact line. Your own line number will differ; what matters is that the file is yours. Surefire also reclassifies the result as `Failures: 0, Errors: 1` instead of `Failures: 1, Errors: 0`. This is the second reason the reconciliation invariant earns its place: it catches dropped rows, and it converts a silently wrong answer into a throw that names its own address.

- [ ] Restore both edits so `summarize()` seeds every status and counts every application.

### Verify

- [ ] Run the focused tests through the complete Maven lifecycle, formatting, and static-analysis gates.

```bash
mvn -Dtest='ApplicationSummaryTest' verify
```

```text
[INFO] --- checkstyle:3.6.0:check (validate-style) @ job-application-tracker ---
[INFO] --- spotless:3.10.0:check (default) @ job-application-tracker ---
[INFO] Running application summary
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.031 s -- in application summary
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Analyzed bundle 'job-application-tracker' with 13 classes
[WARNING] Coverage checks have not been met. See log for details.
[INFO] BUILD SUCCESS
```

The coverage warning is expected in this focused run: `-Dtest='ApplicationSummaryTest'` deliberately excludes the console tests while `pom.xml` still reports missed console methods, and that reporting rule has `haltOnFailure` disabled. The four summary tests, Checkstyle, and Spotless must still pass. If Spotless rejects the multi-line assertion messages, run `mvn spotless:apply`; text blocks are reformatted, not forbidden. If you used the fast track before finishing the Chapter 1 capstone, this focused command isolates the new feature while existing capstone failures remain. Before marking the interlude complete, return to the capstone and pass its plain `mvn verify` completion gate.

## Self-check

- [ ] Answer these without looking back.

1. Why compare the sum of status counts with the source total?
2. Why return all five statuses instead of omitting zero-count categories?
3. When is the explicit loop clearer than a stream collector?
4. What does SQL `GROUP BY status` do before `COUNT(*)` is calculated?
5. How would you validate a transformed report against its source system?
6. What changes when the application dataset no longer fits comfortably in memory?
7. Why can an assertion failure never name the production method that caused it, and what replaces that missing frame?

<details>
<summary>Answers</summary>

1. The invariant exposes dropped, duplicated, or misclassified rows even when individual numbers look reasonable.
2. A stable shape is easier for dashboards and distinguishes a measured zero from a category that was never calculated.
3. Prefer the loop when initialization, edge cases, or debugging steps matter more than compact expression; use a collector once the team can read and test it just as clearly.
4. It partitions rows into one group per distinct status, after which `COUNT(*)` counts rows inside each group.
5. Name the source of truth, compare input and output counts, account for skipped and failed rows, sample business-critical values, and retain evidence tied to a run and input version.
6. Push aggregation to the database or process bounded pages/streams; preserve a consistent snapshot and the same reconciliation invariant.
7. The production method returned normally and its frame was discarded before JUnit threw at the assert line, so the trace can only show the test. The `Owner:` line in the assertion message supplies the address the trace cannot, and an in-method invariant that throws restores a real frame where one is worth having.

</details>

## Where This Lands You

You now have a reusable dashboard seam, an assertion style that names its own owner, and a concrete story about counting, validation, deterministic outputs, and SQL-shaped reasoning. Lesson 10 moves from one trustworthy transformation to the complete workflow that schedules, retries, audits, and supports an automation.

**Previous:** [Chapter 1 Capstone — The Tracker Core](../ch01-java-foundations/CAPSTONE.md) · **Next:** [Lesson 10 — Designing Reliable Automation Workflows](10-reliable-automation-workflows.md) · **Interlude:** [README](README.md)
