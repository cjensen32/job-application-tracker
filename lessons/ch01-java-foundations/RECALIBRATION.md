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
| Expand Chapter 1 from Lessons 0–3 to Lessons 0–8 | The existing capstone had already grown into a console application without first teaching its control flow, I/O, package design, test boundaries, or quality gates. | [Chapter map](README.md), [learning plan](../../LEARNING.md) |
| Add control-flow and exception-boundary instruction | Menu retries, parse failures, guard clauses, and EOF are Java boundary skills required by the executable CLI contract. | [Lesson 4](04-control-flow-and-exception-boundaries.md), [capstone menu behavior](CAPSTONE.md#menu-behavior) |
| Add console I/O and validation instruction | `Scanner`, `PrintStream`, UTF-8, `LocalDate`, enums, IDs, and HTTP(S) URI policy must be understood before they are tested. | [Lesson 5](05-console-io-and-validation.md), [stable CLI text](CAPSTONE.md#stable-cli-text) |
| Add refactoring and package-design instruction | The previous `util` grouping mixed coordination, input, output, and rendering; role-specific classes make ownership and dependency direction visible. | [Lesson 6](06-refactoring-and-package-design.md), [CLI type contract](CAPSTONE.md#cli-types-and-exact-public-api) |
| Add console-testing instruction | Injected streams, exact rendering, full sessions, subprocess startup, EOF, and cleanup are needed to test the real boundary without global-state leaks. | [Lesson 7](07-testing-console-applications.md), [test-writing conventions](../testing-standards/README.md) |
| Add Java quality instruction | Two-space formatting, import discipline, warnings, and Maven quality phases need an explanation before they become required gates. | [Lesson 8](08-java-quality-standards.md), [course standards](../course-standards/README.md) |
| Renumber later lessons through Lesson 22 | Inserting five Chapter 1 lessons requires stable unique lesson numbers; Spring now starts at Lesson 9. | [Learning plan](../../LEARNING.md) |
| Make Chapter 4 the dedicated test-writing and robustness chapter | Chapter 1 introduces testing mechanics, while a later chapter must teach maintainable test design, JUnit, Mockito, integration boundaries, and Spring slices in depth. | [Chapter 4 roadmap](../../LEARNING.md#chapter-4--test-writing-and-robustness-milestones-5-7), [test-writing conventions](../testing-standards/README.md) |
| Add course-wide architecture and quality conventions | Maven layout, dependency direction, visibility, testing, and framework timing are repository decisions that later chapters need to reference consistently. | [Course standards](../course-standards/README.md), [Maven standard layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout) |
| Tighten Markdown authoring rules | Concise bullets and natural paragraph wrapping make lesson diffs easier to review and source easier to maintain. | [Authoring spec](../AUTHORING.md) |
| Rewrite the capstone as a compact behavioral contract | The hidden grader may assert only types, signatures, behavior, and stable output fully disclosed to the learner. | [Capstone contract](CAPSTONE.md), [authoring spec](../AUTHORING.md#capstone-tests) |
| Keep `Main` as a composition root | Startup wiring should be independently reviewable; input, CRUD, and rendering belong in testable collaborators. | [Main contract](CAPSTONE.md#main), [course dependency direction](../course-standards/README.md#dependency-direction) |
| Replace vague CLI utilities with four role-specific classes | `ConsoleApplication`, `ConsolePrompter`, `ConsoleView`, and pure `TextTable` each have one reason to change. | [Target architecture](README.md#target-architecture), [Lesson 6 cohesion table](06-refactoring-and-package-design.md#step-1--split-by-reasons-to-change) |
| Add create/update request records and service overloads | A named immutable operation input avoids six positional parameters while compatibility methods remain useful and stable. | [Request record contract](CAPSTONE.md#request-records), [service contract](CAPSTONE.md#applicationservice) |
| Define complete create/list/filter/edit/delete behavior | Chapter 1 now owns the complete plain-Java CRUD experience before HTTP and Spring replace the boundary. | [Menu behavior](CAPSTONE.md#menu-behavior), [later replacements](CAPSTONE.md#what-later-chapters-replace) |
| Make table rendering pure and rectangular | Returning text makes exact tests independent of global output; null, whitespace, and ragged rows expose real boundary mistakes. | [TextTable contract](CAPSTONE.md#pure-texttable-contract), [Lesson 7 edge cases](07-testing-console-applications.md#step-2--test-tables-and-full-sessions-at-the-right-boundaries) |
| Add Checkstyle 13.9.0 through Maven Checkstyle Plugin 3.6.0 | A pinned validate-phase check makes source rules deterministic for production and test code on Java 21. | [`pom.xml`](../../pom.xml), [`checkstyle.xml`](../../config/checkstyle/checkstyle.xml), [Checkstyle](https://checkstyle.org/) |
| Add compiler lint warnings and retarget JaCoCo | Warnings create review evidence, and coverage must follow the new CLI class names rather than deleted utilities. | [`pom.xml`](../../pom.xml), [quality standards](08-java-quality-standards.md) |
| Preserve learner-owned production code | The assignment must remain the learner's implementation work; infrastructure can intentionally make the checkout red until the contract is implemented. | [Project agent context](../../.agents/CONTEXT.md), [capstone constraints](CAPSTONE.md#constraints) |

## Suggested commit grouping

These are review boundaries, not commits created by this change record. Commit messages follow the repository's existing `AREA(scope) - description` language and stay ordered by importance: groundwork, additional work, then minor synchronization.

### Groundwork

#### 1. `BUILD(ch 1) - add quality and authoring standards for expanded capstone`

Include `pom.xml`, `config/checkstyle/checkstyle.xml`, `lessons/AUTHORING.md`, `lessons/course-standards/README.md`, `lessons/testing-standards/README.md`, and the applicable baseline and rationale from this record. These six groundwork files establish the historical starting point, Java 21 compiler warnings, Checkstyle validation, test-source enforcement, JaCoCo targets, lesson-writing rules, course conventions, and reusable test structure.

#### 2. `LESSONS(ch 1) - add control flow validation and package design`

Include Lessons 4–6, `GLOSSARY.md`, and the applicable rationale from this record. These five files teach control flow, EOF, console validation, constructor-injected streams, refactoring, request records, package dependencies, and the terms needed to discuss them.

### Additional work

#### 3. `LESSONS(ch 1) - add testing quality lessons and renumber roadmap`

Include Lessons 7–8, `LEARNING.md`, `NOTES.md`, and the applicable rationale from this record. These five files add testing and Java quality instruction, make Chapter 4 the dedicated test-writing chapter, renumber the roadmap through Lesson 22, and record the associated Java version history.

#### 4. `CAPSTONE(ch 1) - recalibrate architecture and behavior contract`

Include `CAPSTONE.md`, the chapter `README.md`, the narrow cross-reference updates in Lessons 1 and 3, `.agents/CONTEXT.md`, and the applicable rationale from this record. These six files define the exact target architecture and behavior, connect earlier lessons to the expanded sequence, preserve learner ownership, and synchronize repository guidance.
