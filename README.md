# Job Application Tracker — Chapter 1

This repository is a completed Java 21 learning project: a plain-Java command-line tracker with full CRUD behavior, layered boundaries, deterministic console output, input recovery, tests, and Maven quality gates.

The project is intentionally closed at the end of Chapter 1. More advanced framework work belongs in a separate repository.

## Three repository surfaces

- **Code and build:** `src/` plus [`pom.xml`](pom.xml) and `config/`.
- **Learning and lessons:** [`lessons/`](lessons/README.md), containing the self-contained Chapter 1 course and capstone.
- **Agent backbone:** [`.agents/`](.agents/README.md), containing project authority, maintenance rules, and closeout evidence.

These surfaces have separate ownership. Code builds independently; lessons may reference code; `.agents/` may coordinate both.

## Implemented application

- Create, list, filter, edit, and delete job applications.
- Validate menu choices, positive IDs, dates, statuses, and absolute HTTP(S) URLs.
- Preserve required fields on blank edits and clear optional fields with `-`.
- Render deterministic rectangular text tables without mutating caller data.
- Exit safely on quit or EOF without saving partial input.

## Build and verify

Requirements: JDK 21 and Maven.

```bash
mvn verify
mvn compile
java -cp target/classes com.connorjensen.jobtracker.Main
```

The final verification target is 49 passing JUnit tests plus green Checkstyle, Spotless, and JaCoCo gates.

## Start with

- To run or review the application, open [`src/main/java/com/connorjensen/jobtracker/Main.java`](src/main/java/com/connorjensen/jobtracker/Main.java).
- To review the learning path, open [`lessons/README.md`](lessons/README.md).
- To inspect the delivered scope and architecture, open [`.agents/PROJECT.md`](.agents/PROJECT.md).
