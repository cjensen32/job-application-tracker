# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project state

Plain Maven project, no Spring Boot yet — Spring arrives in Chapter 2 of the learning plan. Java 21 (sdkman-managed Temurin), Maven 3.9, JUnit 5 with `maven-surefire-plugin` pinned to 3.5.2 (Maven's default 2.12.4 binding silently skips JUnit 5 tests).

```bash
mvn compile
mvn test
mvn test -Dtest=ClassName#methodName
java -cp target/classes com.connorjensen.jobtracker.Main
```

Add `./mvnw spring-boot:run` and frontend commands here once those exist.

## This is a teaching repo first

The user is teaching themselves Java through this project for interview practice, using the CodeSensei plugin (`/code-sensei:*`). **Do not write the project's source code for them unless they explicitly ask.** Lessons and capstone specs are yours to write; the implementation is theirs to type. Favor explaining *why* behind non-trivial choices over making changes silently.

`LEARNING.md` (repo root) sequences PROJECT.md's milestones into 6 chapters / 18 lessons and drives the order of work. Its conventions:

- **Explain before automating.** The first time a tool or concept appears, walk through it manually before reaching for the thing that generates it — hand-written `pom.xml` before start.spring.io, manual constructor wiring before `@Autowired`, a console loop before `@RestController`.
- **Revisit, don't restart.** Apply new concepts to code that already exists rather than greenfield examples.
- **No throwaway work.** Every capstone builds part of the real project. Where something is temporary (in-memory repo, console UI), say up front what replaces it and what interface survives.
- **Tests from Lesson 1**, not PROJECT.md's Milestone 7; formal deep-dive in Chapter 4.

Update the progress table at the bottom of LEARNING.md as lessons complete.

### Authoring lessons

**[`lessons/AUTHORING.md`](../lessons/AUTHORING.md) is the spec for building chapters — read it before writing one, and revise it rather than silently deviating.** It covers the folder layout, sub-lesson and capstone rules, how the hidden capstone tests work, the XP/quiz gate, and the version-notes convention. Highlights that are easy to get wrong:

- The capstone test lives **outside `src/`** so it can't break `mvn test` mid-chapter, and **the user does not read it** — so `CAPSTONE.md` must spell out every class name, method signature and behaviour it asserts. Always validate a new capstone test against a throwaway reference implementation in the scratchpad, then delete it.
- **XP is banked once per chapter, after the capstone**, not per lesson. Sub-lessons end with self-check questions only.
- Java version-history material (`[J8]`-style tags → the chapter's `NOTES.md`) stays out of the main lesson flow. The user works in deprecated codebases and finds language evolution genuinely interesting, so it's wanted — just sidelined.

Background for calibrating explanations: one university Java course (exercises, never a full app), but real project experience with React/TSX and Python Flask/uvicorn. HTTP, REST, and JSON are familiar; Java idioms and the Spring layer are not. Analogies to Flask/React land well. Pacing after Chapter 1 can be faster than Lessons 0–1.

## Intended architecture (from PROJECT.md)

Full-stack CRUD app for tracking job applications, built to practice Full Stack Java Developer interview skills: REST API design, JPA relationships, DTOs/validation, auth, and a frontend consuming the API.

**Stack:** Java 21, Spring Boot, Spring Data JPA, Spring Security (JWT), PostgreSQL, Maven, JUnit 5/Mockito/Spring Boot Test (MockMvc). Frontend is React (or Thymeleaf as a backend-only fallback).

**Core domain model:**
- `Application` — company, role, status (`APPLIED`, `PHONE_SCREEN`, `INTERVIEWING`, `OFFER`, `REJECTED`), appliedDate, notes, jobUrl
- `Interview` — date, round, notes; `@OneToMany` from `Application`, exposed via nested endpoints (`POST /api/applications/{id}/interviews`)

**Planned build order** (see PROJECT.md for full detail): skeleton API + Postgres connection → CRUD endpoints with DTOs/validation → filtering/sorting/pagination (`GET /api/applications?status=...`, `Pageable`) → `Interview` relationship → centralized error handling (`@ControllerAdvice` + custom exceptions like `ApplicationNotFoundException`) → JWT auth scoping applications to a user → test coverage across service/controller/repository layers → React frontend → stretch goals (status history, dashboard, reminders, deployment).

When implementing against this plan, follow the milestone order in PROJECT.md rather than jumping ahead (e.g. don't add auth before CRUD endpoints exist).

## Git and worktrees

Background Claude sessions isolate their work into `.claude/worktrees/<name>/`. These are **real git checkouts inside the repo**, not scratch copies — they share the same `.git` object store and their branches are ordinary branches. `.claude/worktrees/` is gitignored so they don't surface as untracked files in the parent checkout.

Four behaviours that reliably cause confusion:

- **A worktree branch with no commits is invisible to branch diffs.** Edits sitting in a worktree's working tree are uncommitted, so the branch still points at whatever it was created from and `git diff main..<branch>` is legitimately empty. Commit *inside the worktree* (`cd .claude/worktrees/<name> && git add -A && git commit`) before expecting the changes to appear anywhere else. This is the usual cause of "the branch exists but shows no changes".
- **You cannot check out a branch that a worktree already has checked out.** Git allows one working tree per branch, so `git checkout <branch>` fails with `already used by worktree at ...`. Work on it by `cd`-ing into the worktree instead. Sessions also `lock` their worktree while running; `git worktree list` shows which.
- **New worktrees default to branching from `origin/<default-branch>`, not local `HEAD`.** When local `main` is ahead of origin, that silently bases the work on stale files. Create from local HEAD explicitly when that matters: `git worktree add -b <name> .claude/worktrees/<name> HEAD`.
- **`git worktree remove` discards uncommitted work** in that worktree with no recovery path. Check `git -C .claude/worktrees/<name> status` first.

Prefer committing inside the worktree and merging from the parent over copying files between the two by hand.
