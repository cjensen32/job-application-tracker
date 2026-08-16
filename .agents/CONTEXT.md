# Project agent context

This is the canonical repository guidance for every coding agent. Keep context, skills, local agent settings, and agent-managed worktrees under `.agents/`. Codex, Claude, and OpenCode discover this file through their user-level global bootstrap instructions.

Do not create provider-specific repository context files or aliases such as`AGENTS.md`, `CLAUDE.md`, `OPENCODE.md`, `.claude/`, `.codex/`, or `.opencode/`.

## Project state

This is currently a Java 21, Maven, and JUnit 5 project. Chapter 1 builds the plain-Java core: the `Application`/`Status` model, repository interface and in-memory implementation, service layer, and a console composition root.

No Spring Boot yet: it arrives in Chapter 2 of the learning plan. Do not add Spring, JPA, a database, or other future-stack dependencies ahead of the lesson sequence.

Leave `maven-surefire-plugin` pinned in `pom.xml`. Maven's default 2.12.4 binding silently skips JUnit 5 tests, so unpinning makes the suite "pass" by running nothing.

## Sources of truth

- `PROJECT.md` defines the target domain model, stack, milestones, and v1.
- `LEARNING.md` defines the teaching sequence and records lesson progress.
- Each chapter's `README.md`, lessons, `GLOSSARY.md`, `NOTES.md`, and`CAPSTONE.md` define that chapter's work.
- `lessons/AUTHORING.md` defines how lessons and capstones are written.
- `pom.xml` defines the dependencies and Java/build configuration that exist now. Do not treat the planned stack in `PROJECT.md` as already installed.

When these disagree, preserve the learner's current working code and resolve the documentation conflict explicitly instead of guessing from the roadmap.

## This is a teaching repo first

The user is teaching themselves Java through this project for interview practice, using the CodeSensei plugin (`/code-sensei:*`). **Do not write the project's source code for them unless they explicitly ask.** Lessons and capstone specs are yours to write; the implementation is theirs to type. Favor explaining *why* behind non-trivial choices over making changes silently.

When coaching a capstone, do not open or use either copy of its grader as a solution guide:

- `lessons/**/capstone/ChapterNNCapstoneTest.java`
- `src/test/**/capstone/ChapterNNCapstoneTest.java`

Work from `CAPSTONE.md` and the Maven test output. Reading or changing a grader is appropriate only when the user explicitly asks to author or revise lesson or capstone infrastructure.

`LEARNING.md` (repo root) sequences PROJECT.md's milestones into 6 chapters / 18 lessons and drives the order of work. Its conventions:

- **Explain before automating.** The first time a tool or concept appears, walk through it manually before reaching for the thing that generates it — hand-written `pom.xml` before start.spring.io, manual constructor wiring before `@Autowired`, a console loop before `@RestController`.
- **Revisit, don't restart.** Apply new concepts to code that already exists rather than greenfield examples.
- **No throwaway work.** Every capstone builds part of the real project. Where something is temporary (in-memory repo, console UI), say up front what replaces it and what interface survives.
- **Tests from Lesson 1**, not PROJECT.md's Milestone 7; formal deep-dive in Chapter 4.

Update the progress table at the bottom of LEARNING.md as lessons complete.

### Authoring lessons

**Writing or revising a chapter?** [`lessons/AUTHORING.md`](../lessons/AUTHORING.md) is the spec — read it before writing one, and revise it rather than silently deviating. The `authoring-lessons` skill carries the rules that are easiest to get wrong.

Background for calibrating explanations: one university Java course (exercises, never a full app), but real project experience with React/TSX and Python Flask/uvicorn. HTTP, REST, and JSON are familiar; Java idioms and the Spring layer are not. Analogies to Flask/React land well. Pacing after Chapter 1 can be faster than Lessons 0–1.

## Navigation and verification

This repository is indexed by CodeGraph. When `.codegraph/` exists, use it before text search or broad file reading to locate or understand Java code:

```sh
codegraph explore "<symbol names or question>"
```

Use `rg`/`rg --files` for follow-up text and file searches. Do not regenerate, delete, or commit CodeGraph state unless the user asks.

There is no Maven wrapper; use the installed `mvn`. The standard full verification command is:

```sh
mvn test
```

Run the narrowest useful check while iterating, then `mvn test` before handing off Java or build changes. A green Maven invocation must report a non-zero JUnit test count.

There is no `exec-maven-plugin` and no IDE run configuration in version control, so the console app runs off the plain classpath:

```sh
mvn compile
java -cp target/classes com.connorjensen.jobtracker.Main
```

Chapter 1's capstone is done when `mvn test` is green *and* that command runs, so check both before calling the chapter finished.

Before editing, inspect `git status` and preserve unrelated user work. Local agent settings and agent-managed worktrees belong under `.agents/`; the repository ignores `.agents/settings.local.json` and `.agents/worktrees/`.

## Intended architecture

Full-stack CRUD job-application tracker (`Application` + nested `Interview`), built to practice Full Stack Java Developer interview skills. **`PROJECT.md` is the source of truth** for the domain model, stack, and milestone list — read it rather than trusting a summary here.

Follow PROJECT.md's milestone order rather than jumping ahead (e.g. don't add auth before CRUD endpoints exist).
