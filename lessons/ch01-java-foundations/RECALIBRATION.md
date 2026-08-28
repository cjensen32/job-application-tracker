# Chapter 1 Recalibration Record

This document explains why the original short foundation chapter expanded into Lessons 0-8 and a stricter capstone. It is historical learner-facing context, not an active roadmap.

## Final baseline

The chapter grew around the tracker the learner actually built. Its final boundary is a complete plain-Java CRUD console application with role-specific CLI classes, request records, local input recovery, exact output, a pure text renderer, JUnit coverage, Checkstyle, Spotless, and JaCoCo.

## Durable decisions

| Decision                                        | Reason                                                                                                                                       | Evidence                                                                                  |
|-------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------|
| Expand from Lessons 0-3 to Lessons 0-8          | The capstone required control flow, console I/O, package design, test boundaries, and quality gates that the shorter sequence had not taught | [Chapter map](README.md)                                                                  |
| Teach console control flow and EOF explicitly   | A menu loop is incomplete unless invalid input retries locally and exhausted input terminates safely                                         | [Lesson 4](04-control-flow-and-exception-boundaries.md), [capstone](CAPSTONE.md)          |
| Teach validation before refactoring             | Dates, statuses, IDs, URLs, optional fields, and retry ownership need one consistent input boundary                                          | [Lesson 5](05-console-io-and-validation.md)                                               |
| Split the CLI by responsibility                 | Prompting, coordination, output, and rendering have different reasons to change and different test boundaries                                | [Lesson 6](06-refactoring-and-package-design.md)                                          |
| Add console-testing instruction                 | Injected streams, exact rendering, full sessions, subprocess startup, EOF, and cleanup prove the real boundary without global-state leaks    | [Lesson 7](07-testing-console-applications.md), [testing standards](TESTING_STANDARDS.md) |
| Add Java quality instruction                    | Formatting, names, imports, warnings, and Maven phases need explanation before becoming required gates                                       | [Lesson 8](08-java-quality-standards.md), [course standards](COURSE_STANDARDS.md)         |
| Keep `Main` as a composition root               | Startup wiring stays reviewable while input, CRUD, and rendering live in testable collaborators                                              | [Main contract](CAPSTONE.md#main)                                                         |
| Publish a complete behavioral capstone contract | The grader may assert only types, signatures, behavior, and stable output disclosed to the learner                                           | [Capstone](CAPSTONE.md)                                                                   |
| Keep two synchronized grader copies             | The course copy remains installable while the `src/test` copy executes in the finished project                                               |                                                                                           |)                                                                                        |
| Preserve learner-owned production code          | Course revisions must clarify or verify the assignment without taking over its implementation                                                | [Capstone constraints](CAPSTONE.md#constraints)                                           |

## Final verification record

- `mvn verify`: 49 tests, zero failures, errors, or skips; Checkstyle, Spotless, and JaCoCo pass.
- `mvn compile`: success.
- Real CLI clean quit: complete header/menu, one prompt, `Goodbye.`, successful exit.
- Clean EOF: complete header/menu, `Goodbye.`, successful exit.

The finished repository preserves this chapter as a self-contained learning artifact. Framework, database, browser, and automation work deliberately moved to a separate project.
