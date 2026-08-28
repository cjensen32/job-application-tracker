# Project Closeout Record

Status: candidate verified; canonical `main` and remote publication await approval.

## Decision

The repository closes at the completed Chapter 1 plain-Java boundary. Code/build, learner-facing lessons, and the agent backbone are separate ownership surfaces. Advanced work belongs in a separate project.

## Preservation archive

Archive directory: `/Users/connor/repos/JAVA/job-application-tracker-closeout-archive-20260828`

- `repository.bundle` — complete Git history and all 36 refs, including `archive/post-ch01-wip-20260828` at `cfd9b90`.
- `local-context.tar.gz` — ignored `.agents/notes/`, local agent/provider settings, and the scratch commit-message buffer.
- Bundle SHA-256: `416a530de7dbb8a8bd8c4780eba72f9f71cb59230f208728868c6e985b57028c`.
- Local-context SHA-256: `d9344b407b339e3fcf8273ce277448321aa105141de62396edb509af9308d09c`.
- `git bundle verify` result: complete history; bundle is valid.

The preservation branch contains two WIP commits and is intentionally excluded from the closeout branch:

- `aef261d` — unfinished `ApplicationSummary`, its initial test, and `ApplicationService.summarize()` stub.
- `cfd9b90` — checked Lesson 9 initialization progress.

## Chapter 1 reconstruction

The candidate starts at `78f0979` and replays only the Chapter 1 capstone work plus the later deterministic-test correction:

1. `3c27cda`
2. `ebc8022`
3. `5bb2777`
4. `097f027`
5. `50d6daf`
6. `dc31fd4`

This omits the FNBO profile, post-Chapter-1 testing guidance, reliable-automation interlude, and interlude-centered reorganization.

## Consolidation decisions

- Root `PROJECT.md` moved to `.agents/PROJECT.md` and now records delivered scope rather than a future roadmap.
- Root `LEARNING.md` moved to `lessons/README.md` and now records Chapter 1 only.
- Agent-only lesson-authoring and retrospective rules were consolidated into `.agents/LESSON_MAINTENANCE.md`.
- Learner-facing standards moved inside `lessons/ch01-java-foundations/`.
- Retired agent skills, future lesson plans, job-specific context, and tracked IDE metadata were removed from the candidate.
- The root `README.md` is a neutral entry point across code, lessons, and `.agents/`.

## Verification

- `mvn verify`: `BUILD SUCCESS`; 49 tests, 0 failures, 0 errors, and 0 skipped.
- Checkstyle: 0 violations. Spotless: all 18 Java files clean. JaCoCo: all coverage checks met.
- Canonical and installed Chapter 1 grader copies match after whitespace removal.
- Direct `5` and EOF process probes both print the menu once and end with `Goodbye.`.
- Relative Markdown links resolve, lessons contain no `.agents/` dependencies, and retired roadmap/path scans are empty.
- The tracked-path ownership audit assigns every path exactly once to code/build, lessons, or `.agents/`.
- `git diff --check` passes. The candidate worktree is clean after the three closeout commits.

## Rollback

Before publication, abandon `closeout/chapter-1` and leave `main` unchanged. After publication, restore refs from `repository.bundle` and update `main` only with an explicitly approved `--force-with-lease` operation.
