# Project agent context

This is the canonical repository guidance for every coding agent. Keep context, skills, local agent settings, and agent-managed worktrees under `.agents/`. Codex, Claude, and OpenCode discover this file through their user-level global bootstrap instructions.

Do not create provider-specific repository context files or aliases such as`AGENTS.md`, `CLAUDE.md`, `OPENCODE.md`, `.claude/`, `.codex/`, or `.opencode/`.

## Project state

This is currently a Java 21, Maven, and JUnit 5 project. Chapter 1 has nine lessons and builds the complete plain-Java core: model, repository, service requests, role-specific console classes, tests, and quality gates. A two-lesson reliable-automation interlude follows its capstone before Spring begins at Lesson 11.

No Spring Boot yet: it arrives in Chapter 2 of the learning plan. Do not add Spring, JPA, a database, or other future-stack dependencies ahead of the lesson sequence.

Leave `maven-surefire-plugin` pinned in `pom.xml`. Maven's default 2.12.4 binding silently skips JUnit 5 tests, so unpinning makes the suite "pass" by running nothing.

## Commit messages

Subject line: `C### GROUP(scope): summary`. `.githooks/prepare-commit-msg` prepends the counter.

Groups separate authorship:

- `COURSE(scope)` - agent-written course material. Author is `Course Author <noreply@teacher.ai>`.
- `TRACKER(scope)` - the learner's Java under `src/`. Author is the learner.
- `PROGRESS(scope)` - the learner's edits to lesson markdown, such as ticking checkboxes off.
- `STYLE(scope)` - formatting only, no behavior change.
- `FIX(scope)` - the learner correcting their own earlier Java.

The body uses labelled sections in this order, omitting any that do not apply:

- `DESCRIPTION:` - one or two sentences on what the commit is for.
- `FILES:` - one bullet per file, written as `- path: what it is`.
- `NOTES(subject):` - explanatory content that is not a file summary. Repeatable with different subjects.
- `VERIFY(command):` - numbered steps and the real fenced output of that command.

Never wrap a sentence or a bullet across lines. Keep every full statement on one line, at most 120 characters.

## Sources of truth

- `PROJECT.md` defines the target domain model, stack, milestones, and v1.
- `LEARNING.md` defines the teaching sequence and records lesson progress.
- Each chapter's `README.md`, lessons, `GLOSSARY.md`, `NOTES.md`, and`CAPSTONE.md` define that chapter's work.
- `lessons/AUTHORING.md` defines how lessons and capstones are written.
- `pom.xml` defines the dependencies and Java/build configuration that exist now. Do not treat the planned stack in `PROJECT.md` as already installed.

When these disagree, preserve the learner's current working code and resolve the documentation conflict explicitly instead of guessing from the roadmap.

## Interview-focused course adaptation

Role-specific interview context lives under `.agents/job-profiles/<role>/`. Use it to add or tailor chapter lessons for
likely technical interview topics without replacing the course sources of truth, skipping the lesson sequence, or
overstating the learner's experience.

The active FNBO Analyst II, Banking Automation Engineer profile supports the interview on Thursday, 2026-08-27. Read
its three files before preparing tailored lessons:

- `job_profile.md` - the role, requirements, fit, gaps, and original posting.
- `interview_annotated_resume.md` - the resume that was submitted and interview-focused notes.
- `application_questions.md` - the answers submitted with the application.

## This is a teaching repo first

The user is teaching themselves Java through this project for interview practice, using the CodeSensei plugin (`/code-sensei:*`). **Do not write the project's source code for them unless they explicitly ask.** Lessons and capstone specs are yours to write; the implementation is theirs to type. Favor explaining *why* behind non-trivial choices over making changes silently.

When coaching a capstone, do not open or use either copy of its grader as a solution guide:

- `lessons/**/capstone/ChapterNNCapstoneTest.java`
- `src/test/**/capstone/ChapterNNCapstoneTest.java`

Work from `CAPSTONE.md` and the Maven test output. Reading or changing a grader is appropriate only when the user explicitly asks to author or revise lesson or capstone infrastructure.

`LEARNING.md` (repo root) sequences PROJECT.md's milestones into 6 chapters / 23 numbered lessons (0–22) and drives the order of work. Its conventions:

- **Explain before automating.** The first time a tool or concept appears, walk through it manually before reaching for the thing that generates it — hand-written `pom.xml` before start.spring.io, manual constructor wiring before `@Autowired`, a console loop before `@RestController`.
- **Revisit, don't restart.** Apply new concepts to code that already exists rather than greenfield examples.
- **No throwaway work.** Every capstone builds part of the real project. Where something is temporary (in-memory repo, console UI), say up front what replaces it and what interface survives.
- **Tests from Lesson 1**, not PROJECT.md's Milestone 7; formal deep-dive in Chapter 4.

Update the progress table at the bottom of LEARNING.md as lessons complete.

### Authoring lessons

**Writing or revising a chapter?** [`lessons/AUTHORING.md`](../lessons/AUTHORING.md) is the spec — read it before writing one, and revise it rather than silently deviating. The `authoring-lessons` skill carries the rules that are easiest to get wrong.

[`lessons/testing-standards/README.md`](../lessons/testing-standards/README.md) is the course-wide test reference, including how to diagnose a failure that cannot produce a stack trace.

**Chapter 1's capstone grader is frozen.** It keeps its original assertion shape so the goalposts do not move mid-capstone. Learner-written `Owner:` / `Expected:` assertion messages begin in Lesson 9; the grader-owned `R-##` tags, windowed-output report, one-ordered-assertion rule, and imported learner harness begin in Chapter 2. See `AUTHORING.md` § Lesson-authored tests and § Capstone tests.

### End-of-lesson course review

When the learner finishes a lesson, asks to close it out, or explicitly requests a lesson retrospective, read [`.agents/skills/course-author-review/SKILL.md`](skills/course-author-review/SKILL.md) completely and follow it before marking the lesson complete or closing the retrospective. Run this review as `Course Author <noreply@teacher.ai>`; search lesson-related `.agents/notes/` and available project chat history, improve and simplify the course only where the evidence supports it, store the retrospective locally, and ask the learner for feedback. When the learner later answers those feedback questions, run the skill again and update the same retrospective. Do not run this closeout for ordinary mid-lesson implementation coaching.

### End-of-task notes

Before the final response for every user-requested task, chat, or answered question, create or update one descriptively named Markdown note under `.agents/notes/`. Recap the request and important context, evidence inspected, decisions, actions or file changes, verification and outcome, and any unresolved items or useful next steps. Keep the note concise and durable; do not store secrets, raw chat transcripts, or unfiltered tool output.

Treat follow-ups within the same task as one continuing record and update the existing note instead of creating duplicates. When another workflow already creates a canonical note, such as `lesson-NN-course-author-review.md`, that note also satisfies this requirement. These notes are local agent context and ignored by Git; do not stage or commit them unless the user explicitly asks.

Background for calibrating explanations: one university Java course (exercises, never a full app), but real project experience with React/TSX and Python Flask/uvicorn. HTTP, REST, and JSON are familiar; Java idioms and the Spring layer are not. Analogies to Flask/React land well. Pacing after Chapter 1 can be faster than Lessons 0–1.

## Navigation and verification

This repository is indexed by CodeGraph. When `.codegraph/` exists, use it before text search or broad file reading to locate or understand Java code:

```sh
codegraph explore "<symbol names or question>"
```

Use `rg`/`rg --files` for follow-up text and file searches. Do not regenerate, delete, or commit CodeGraph state unless the user asks.

There is no Maven wrapper; use the installed `mvn`. The standard full verification command is:

```sh
mvn verify
```

Run `mvn -Dcheckstyle.skip test` while a structural capstone refactor is missing required types, then use `mvn verify` for final tests, Checkstyle, and JaCoCo reporting. A green Maven invocation must report a non-zero JUnit test count.

There is no `exec-maven-plugin` and no IDE run configuration in version control, so the console app runs off the plain classpath:

```sh
mvn compile
java -cp target/classes com.connorjensen.jobtracker.Main
```

Chapter 1's capstone is done only when `mvn verify`, `mvn compile`, and that command all succeed, so check all three before calling the chapter finished.

Before editing, inspect `git status` and preserve unrelated user work. Local agent settings and agent-managed worktrees belong under `.agents/`; the repository ignores `.agents/settings.local.json` and `.agents/worktrees/`.

## Intended architecture

Full-stack CRUD job-application tracker (`Application` + nested `Interview`), built to practice Full Stack Java Developer interview skills. **`PROJECT.md` is the source of truth** for the domain model, stack, and milestone list — read it rather than trusting a summary here.

Follow PROJECT.md's milestone order rather than jumping ahead (e.g. don't add auth before CRUD endpoints exist).
