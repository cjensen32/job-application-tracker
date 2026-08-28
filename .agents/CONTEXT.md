# Project Agent Context

This repository is closed at the end of Chapter 1. It is a completed Java 21, Maven, and JUnit 5 learning artifact, not an active roadmap. Do not add another chapter, Spring, persistence, a frontend, interview-specific material, or unrelated features here; start that work in the successor project.

## Repository boundaries

The repository has three ownership surfaces:

- Code and build: `src/`, `pom.xml`, `config/`, and portable root tooling.
- Learning: learner-facing material under `lessons/`.
- Agent backbone: project authority, operating rules, maintenance guidance, and durable notes under `.agents/`.

Dependency direction is one-way: `.agents/` may coordinate code and lessons; lessons may reference code paths; code/build must not depend on either documentation surface. Lessons must remain understandable without opening `.agents/`.

Do not create canonical provider-specific context such as `AGENTS.md`, `CLAUDE.md`, `OPENCODE.md`, `.claude/`, `.codex/`, or `.opencode/`. Tool-mandated local caches may exist only when ignored and are not repository authority.

## Sources of truth

- `.agents/PROJECT.md` defines the delivered product scope and technical architecture.
- `.agents/CONTEXT.md` defines agent behavior and repository boundaries.
- `.agents/README.md` maps the agent backbone and its consolidation policy.
- `.agents/LESSON_MAINTENANCE.md` defines how to correct the frozen Chapter 1 curriculum.
- `.agents/notes/PROJECT_CLOSEOUT.md` records closeout evidence and rollback information.
- `lessons/README.md` is the learner-facing Chapter 1 map and completion record.
- `lessons/ch01-java-foundations/` owns the lessons, references, glossary, notes, history, capstone contract, and frozen grader.
- `pom.xml` owns the actual build configuration and dependencies.

When authorities disagree, preserve learner Java, verify the current checkout, and resolve the conflict explicitly. Do not infer current behavior from historical notes.

## Learner ownership

The learner owns Java under `src/` and lesson-progress edits. Agents may maintain course documents and test infrastructure, but must not rewrite learner production code unless explicitly asked. Never use the capstone grader as a solution guide during coaching; work from `CAPSTONE.md` and Maven output.

## Commit messages

`.githooks/prepare-commit-msg` prepends `C###`. Use these groups:

- `COURSE(scope)` for agent-authored course, repository, and agent-backbone material; author `Course Author <noreply@teacher.ai>`.
- `TRACKER(scope)` for learner Java.
- `PROGRESS(scope)` for learner progress records.
- `STYLE(scope)` for formatting-only changes.
- `FIX(scope)` for learner corrections to earlier Java.

Bodies use `DESCRIPTION:`, `FILES:`, optional `NOTES(subject):`, and `VERIFY(command):` in that order. Keep each statement on one physical line and no longer than 120 characters.

## Verification

Chapter 1 is complete only when all three commands succeed:

```bash
mvn verify
mvn compile
java -cp target/classes com.connorjensen.jobtracker.Main
```

`mvn verify` must run 49 tests and pass Checkstyle, Spotless, and JaCoCo. Leave `maven-surefire-plugin` pinned; Maven's default binding can silently skip JUnit 5 tests.

The repository is indexed locally when `.codegraph/` exists. Use CodeGraph before broad code searches, but do not commit, delete, or regenerate its state without explicit approval.

Before editing, inspect `git status`, registered worktrees, and the relevant authority file. Agent worktrees live under ignored `.agents/worktrees/`. Preserve unrelated and uncommitted work.

## Maintenance only

Allowed work is narrowly scoped to defect correction, security or dependency maintenance, documentation repair, and restoration from the closeout archive. Use `.agents/LESSON_MAINTENANCE.md` for lesson corrections. Record durable closeout changes in `.agents/notes/PROJECT_CLOSEOUT.md`; transient task notes remain ignored.
