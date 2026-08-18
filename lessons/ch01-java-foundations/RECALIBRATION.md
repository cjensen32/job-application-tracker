# Chapter 1 Recalibration — Change and Rationale Record

**Date:** 2026-08-17

**Scope:** Curriculum, documentation, build rules, graders, and tests only. Learner-owned production implementation remains untouched.

This record explains why each change belongs in the Chapter 1 expansion and points reviewers to the contract or standard that supports it.

## Baseline when recalibration began

- Chapter 1 contained Lessons 0–3, while its capstone already expected a multi-path console application.
- The recorded `mvn test` baseline was 37 tests with two known failures: non-numeric menu recovery and rectangular rendering for ragged rows.
- Maven already compiled Java 21 and ran Surefire and JaCoCo, but it did not enforce compiler lint warnings or Checkstyle for production and test sources.
- Production classes grouped console coordination, prompting, output, and table rendering under broad `util` names.
- Learner-owned production code was frozen for this documentation, build, grader, and test recalibration.

## Change inventory

| Change | Why it belongs in this change set | Supporting document or evidence |
|--------|-----------------------------------|---------------------------------|
| Add control-flow and exception-boundary instruction | Menu retries, parse failures, guard clauses, and EOF are Java boundary skills required by the console application. | [Lesson 4](04-control-flow-and-exception-boundaries.md) |
| Add console I/O and validation instruction | `Scanner`, `PrintStream`, UTF-8, `LocalDate`, enums, IDs, and HTTP(S) URI policy must be understood before they are tested. | [Lesson 5](05-console-io-and-validation.md) |
| Add refactoring and package-design instruction | The previous `util` grouping mixed coordination, input, output, and rendering; role-specific classes make ownership and dependency direction visible. | [Lesson 6](06-refactoring-and-package-design.md), [course dependency direction](../course-standards/README.md#dependency-direction) |
| Add course-wide architecture and quality conventions | Maven layout, dependency direction, visibility, testing, and framework timing are repository decisions that later chapters need to reference consistently. | [Course standards](../course-standards/README.md), [Maven standard layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout) |
| Tighten Markdown authoring rules | Concise bullets and natural paragraph wrapping make lesson diffs easier to review and source easier to maintain. | [Authoring spec](../AUTHORING.md) |
| Add Checkstyle 13.9.0 through Maven Checkstyle Plugin 3.6.0 | A pinned validate-phase check makes source rules deterministic for production and test code on Java 21. | [`pom.xml`](../../pom.xml), [`checkstyle.xml`](../../config/checkstyle/checkstyle.xml), [Checkstyle](https://checkstyle.org/) |
| Add compiler lint warnings and retarget JaCoCo | Warnings create review evidence, and coverage must follow the new CLI class names rather than deleted utilities. | [`pom.xml`](../../pom.xml), [course quality gates](../course-standards/README.md#quality-gates) |

## Suggested commit grouping

These are review boundaries, not commits created by this change record. Commit messages follow the repository's existing `AREA(scope) - description` language and stay ordered by importance: groundwork, additional work, then minor synchronization.

### Groundwork

#### 1. `BUILD(ch 1) - add quality and authoring standards for expanded capstone`

Include `pom.xml`, `config/checkstyle/checkstyle.xml`, `lessons/AUTHORING.md`, `lessons/course-standards/README.md`, `lessons/testing-standards/README.md`, and the applicable baseline and rationale from this record. These six groundwork files establish the historical starting point, Java 21 compiler warnings, Checkstyle validation, test-source enforcement, JaCoCo targets, lesson-writing rules, course conventions, and reusable test structure.

#### 2. `LESSONS(ch 1) - add control flow validation and package design`

Include Lessons 4–6, `GLOSSARY.md`, and the applicable rationale from this record. These five files teach control flow, EOF, console validation, constructor-injected streams, refactoring, request records, package dependencies, and the terms needed to discuss them.
