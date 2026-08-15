# CLAUDE.md

## Project state

No Spring Boot yet — it arrives in Chapter 2 of the learning plan. Leave `maven-surefire-plugin` pinned in `pom.xml`: Maven's default 2.12.4 binding silently skips JUnit 5 tests, so unpinning makes the suite "pass" by running nothing.

## This is a teaching repo first

The user is teaching themselves Java through this project for interview practice, using the CodeSensei plugin (`/code-sensei:*`). **Do not write the project's source code for them unless they explicitly ask.** Lessons and capstone specs are yours to write; the implementation is theirs to type. Favor explaining *why* behind non-trivial choices over making changes silently.

`LEARNING.md` (repo root) sequences PROJECT.md's milestones into 6 chapters / 18 lessons and drives the order of work. Its conventions:

- **Explain before automating.** The first time a tool or concept appears, walk through it manually before reaching for the thing that generates it — hand-written `pom.xml` before start.spring.io, manual constructor wiring before `@Autowired`, a console loop before `@RestController`.
- **Revisit, don't restart.** Apply new concepts to code that already exists rather than greenfield examples.
- **No throwaway work.** Every capstone builds part of the real project. Where something is temporary (in-memory repo, console UI), say up front what replaces it and what interface survives.
- **Tests from Lesson 1**, not PROJECT.md's Milestone 7; formal deep-dive in Chapter 4.

Update the progress table at the bottom of LEARNING.md as lessons complete.

### Authoring lessons

**Writing or revising a chapter?** [`lessons/AUTHORING.md`](../lessons/AUTHORING.md) is the spec — read it before writing one, and revise it rather than silently deviating. The `authoring-lessons` skill carries the rules that are easiest to get wrong.

Background for calibrating explanations: one university Java course (exercises, never a full app), but real project experience with React/TSX and Python Flask/uvicorn. HTTP, REST, and JSON are familiar; Java idioms and the Spring layer are not. Analogies to Flask/React land well. Pacing after Chapter 1 can be faster than Lessons 0–1.

## Intended architecture

Full-stack CRUD job-application tracker (`Application` + nested `Interview`), built to practice Full Stack Java Developer interview skills. **`PROJECT.md` is the source of truth** for the domain model, stack, and milestone list — read it rather than trusting a summary here.

Follow PROJECT.md's milestone order rather than jumping ahead (e.g. don't add auth before CRUD endpoints exist).
