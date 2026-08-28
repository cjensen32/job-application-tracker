# Chapter 1 Lesson Maintenance

This is agent-only guidance for correcting the frozen Chapter 1 curriculum. `lessons/` remains learner-facing and must not depend on this file. Do not add another chapter or repurpose this repository for the successor project.

## Ownership and evidence

- Preserve learner Java and progress edits. Agents own course prose, references, capstone contracts, and grader infrastructure.
- Inspect the current lesson, adjacent handoff, learner code, Git diff, relevant durable notes, and actual Maven output before changing guidance.
- Treat notes and prior chats as dated evidence. State the evidence boundary and re-check the live checkout before repeating old pass/fail claims.
- Separate authoring defects from implementation defects. Correct the course only when its contract, prerequisite, ownership, wording, or verification is wrong.
- Prefer the smallest correction that removes demonstrated confusion. Do not accumulate repeated explanations.

## Learner-facing boundary

- Learner material belongs under `lessons/`; agent process, review evidence, and maintenance policy belong under `.agents/`.
- Lessons may reference code paths and other learner-facing lesson files, but must not link to `.agents/` or require agent context to be understood.
- Keep detailed implementation findings in transient notes. Record only durable closeout evidence in `.agents/notes/PROJECT_CLOSEOUT.md`.

## Markdown and structure

- Keep each prose paragraph, list item, and blockquote paragraph on one physical line. Use line breaks only for real Markdown structure.
- Chapter 1 keeps `README.md`, Lessons 0-8, `GLOSSARY.md`, `NOTES.md`, `RECALIBRATION.md`, `CAPSTONE.md`, and `capstone/Chapter01CapstoneTest.java`.
- Prefer concise bullets, one idea per section, and examples grounded in the tracker.
- Preserve explanation before automation, revisit existing code instead of restarting, and identify any temporary component plus the boundary that survives it.

## Lesson instructions

- A lesson initialization names every created/edited path and starts from the recorded previous state.
- New Java files use dependency-closed, compilable shells. Account for constructors, collaborators, callers, moved responsibilities, and the complete object graph.
- Preserve the assignment: constructors may initialize fields, `void` stubs stay empty, and unfinished non-void stubs throw `UnsupportedOperationException("TODO")` instead of returning plausible values.
- Label every Java fence as a whole file, edit excerpt, API contract, or standalone demonstration. Whole files include package/imports/enclosing type; excerpts name their insertion point; standalone examples are scratch-verified.
- Every numbered step ends with a Verify section containing an action checkbox, the real command, trimmed actual output, and the most likely failure interpretation.
- Behavioral changes require execution evidence; structural-only changes require a build. Tests require `mvn test` or the appropriate focused/full verification, not `mvn compile`.
- Progress lists map one item to each step plus the self-check. Inline checkboxes represent learner actions only.

## Capstone maintenance

- `CAPSTONE.md` publishes every asserted type, signature, behavior, and stable output because the learner does not read the grader while solving it.
- The canonical grader remains outside `src/` until installed. Keep both grader copies synchronized and preserve the spoiler warning and install command.
- The Chapter 1 grader is frozen. Change it only for an explicitly requested infrastructure defect, and update the contract in the same reviewed change.
- A capstone test must prove concepts, including repository substitutability, exact console behavior, EOF safety, request records, and pure table rendering.
- Validate grader changes against a temporary reference implementation, run the full gates, and delete the temporary implementation.
- XP is awarded once after the capstone. Java version-history material stays in `NOTES.md` and is linked with version tags rather than interrupting the lesson flow.

## Retrospective workflow

When explicitly correcting a completed lesson, review evidence as `Course Author <noreply@teacher.ai>` and classify what worked, friction, authoring defects, implementation defects, and open feedback. Maintain concise learner questions only when they help future readers; do not paste transcripts or volatile line numbers. Update completion only when current evidence supports it.

Verify lesson changes with `git diff --check`, a relative-link audit, and scratch validation for changed Java fences. Re-read for answer leakage, duplicated teaching, stale status, and a clear Chapter 1 handoff. Commit course maintenance separately from learner code.
