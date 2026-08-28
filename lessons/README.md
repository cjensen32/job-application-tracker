# Chapter 1 Learning Record

This directory contains the completed learner-facing course for the plain-Java Job Application Tracker. It ends at the Chapter 1 capstone; advanced framework work continues in a separate project.

## How the chapter works

- Explain a Java mechanism before relying on automation or a framework equivalent.
- Revisit the same tracker instead of building disposable exercises.
- Keep temporary components honest and preserve the interfaces or habits that outlive them.
- Verify every meaningful step with real commands and output.
- Bank XP once, after the completed capstone and chapter quiz.

Use the chapter's [course standards](ch01-java-foundations/COURSE_STANDARDS.md), [test-writing standards](ch01-java-foundations/TESTING_STANDARDS.md), [glossary](ch01-java-foundations/GLOSSARY.md), and [Java version notes](ch01-java-foundations/NOTES.md) as references.

## Chapter 1 — Java Foundations

Goal: build enough Java, design, console I/O, testing, and build discipline to understand a complete application without a production framework or third-party runtime library.

| # | Lesson | Concepts | Status |
|---|---|---|---|
| 0 | [Build system and project shape](ch01-java-foundations/00-build-system.md) | POM, coordinates, scopes, classpath, lifecycle, bytecode | ☑ |
| 1 | [Classes, objects, and records](ch01-java-foundations/01-classes-and-records.md) | constructors, static members, beans, records, equality | ☑ |
| 2 | [Packages, enums, collections, and Optional](ch01-java-foundations/02-types-enums-collections.md) | packages, enums, generics, collections, streams, Optional | ☑ |
| 3 | [Interfaces and dependency injection](ch01-java-foundations/03-interfaces-and-di.md) | contracts, constructor injection, final fields, composition roots | ☑ |
| 4 | [Control flow and exception boundaries](ch01-java-foundations/04-control-flow-and-exception-boundaries.md) | loops, switch dispatch, guards, parsing, retries, EOF | ☑ |
| 5 | [Console I/O and validation](ch01-java-foundations/05-console-io-and-validation.md) | Scanner, PrintStream, UTF-8, dates, enums, IDs, URI | ☑ |
| 6 | [Refactoring and package design](ch01-java-foundations/06-refactoring-and-package-design.md) | cohesion, visibility, injection, request records, dependencies | ☑ |
| 7 | [Testing console applications](ch01-java-foundations/07-testing-console-applications.md) | injected streams, subprocesses, exact output, cleanup, coverage | ☑ |
| 8 | [Java quality standards](ch01-java-foundations/08-java-quality-standards.md) | Maven layout, names, imports, formatting, Checkstyle, warnings | ☑ |
| ★ | [The Tracker Core capstone](ch01-java-foundations/CAPSTONE.md) | complete CRUD CLI, validation, EOF safety, tests, quality gates | ☑ |

## Completion evidence

The final Chapter 1 tree passes 49 JUnit tests plus Checkstyle, Spotless, JaCoCo, explicit compilation, and a real CLI run. The capstone progress list and quiz are complete. Inline lesson checkboxes remain the historical worksheet record; this table is the final chapter-level completion authority.

**End of course:** [Chapter 1 overview](ch01-java-foundations/README.md) · [Capstone](ch01-java-foundations/CAPSTONE.md)
