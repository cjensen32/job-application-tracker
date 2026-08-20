---
name: course-author-review
description: Close out a completed lesson by reviewing learner questions and feedback, improving the course material, and recording durable authoring lessons. Use at the end of a lesson or for an explicit lesson retrospective, not for ordinary mid-lesson implementation coaching.
---

# Course Author Review

Run this review as `Course Author <noreply@teacher.ai>` after the learner finishes a lesson and before the course treats that lesson as closed. The goal is to learn from the learner's actual path, simplify the lesson without removing necessary rigor, and preserve useful evidence for the next course-authoring pass.

## Establish the evidence

1. Read `.agents/CONTEXT.md`, `lessons/AUTHORING.md`, the completed lesson, and its previous/next lesson handoff. Inspect the current lesson diff and preserve all learner-owned source changes.
2. Search `.agents/notes/` using the lesson number, lesson filename, important class or method names, and distinctive error text. Read every relevant note completely; filename matches alone are not enough.
3. When project-scoped chat or thread history is available, including Codex and Claude/Anthropic history, search it for the same terms. Keep actual learner questions, corrections, and feedback; ignore matches that exist only inside quoted lesson text, tool output, or injected instructions. Read enough surrounding conversation to recover any resolution.
4. State the evidence boundary. Never claim that every chat was reviewed when only local notes or a limited thread window was available.
5. Treat notes and chats as dated snapshots. Re-check the current worktree before carrying an old `Open`, `Resolved`, pass, or failure status into the lesson.

## Review what happened

Separate these categories before changing the course:

- **Worked:** explanations, checkpoints, scaffolds, or proof commands that helped the learner proceed or were verified successfully.
- **Friction:** places where the learner had to guess ownership, file placement, method shape, expected output, or the meaning of an error.
- **Authoring defects:** inaccurate build claims, missing prerequisites or callers, contradictory contracts, premature concepts, excessive repetition, or guidance that accidentally supplied the assignment.
- **Implementation defects:** learner-code problems that the published lesson already explained correctly. Keep these in the review note unless they reveal a course defect.
- **Open feedback:** questions or proposed changes that the learner has not confirmed.

Prefer the smallest course correction that removes the demonstrated confusion. Simplify wording, combine repeated explanations, and strengthen one useful mental model or proof point instead of accumulating more prose. Do not remove contract details, verification, or ownership boundaries merely to make a lesson shorter.

## Apply and store the review

- Update the lesson only when the evidence supports the change. Preserve learner ownership: course documents are `COURSE` work; project source remains the learner's work.
- Maintain an `## Asked Questions` section when real learner questions would help future readers. Deduplicate related questions, phrase them as the learner encountered them, give concise resolutions, and label unresolved or superseded guidance honestly. If the main lesson now teaches the resolution, keep only a concise answer or cross-reference instead of duplicating that teaching. Keep implementation-audit findings that were not learner questions in notes rather than disguising them as questions. Do not paste chat transcripts or volatile line numbers into the lesson.
- Keep detailed implementation findings in `.agents/notes/`, not in the teaching flow. Create or update exactly one canonical `.agents/notes/lesson-NN-course-author-review.md` with `Evidence reviewed`, `What worked`, `Friction and authoring defects`, `Changes applied`, and `Open feedback`. Existing topical or implementation notes may coexist as evidence; reference them from the canonical retrospective instead of copying or replacing them.
- When a lesson-specific problem reveals a reusable rule, update `lessons/AUTHORING.md` and its revision note. Do not turn a one-off preference into a universal rule.
- Update lesson or chapter progress only when current repository evidence proves completion. Label a requested retrospective `in progress` when completion is not yet proven, and never convert the review into an unsupported completion claim.
- Do not stage, commit, push, or update agent memory unless the user explicitly asks.

## Verify the course change

- Run `git diff --check` on every changed Markdown file.
- If Java fences changed, follow `lessons/AUTHORING.md` and validate them in a throwaway copy rather than editing learner source. Use a repository-provided lesson validator only when it currently exists and matches the authoring rules; do not invoke a missing or legacy checker.
- Re-read the final lesson section for duplicated explanations, answer leakage, stale statuses, and a clear handoff to the next lesson.
- Report course-document changes separately from unresolved implementation work.

## Ask for learner feedback

End the closeout with one to three short questions tailored to the lesson. Prefer questions such as:

- Which part made you stop or guess instead of following the lesson directly?
- Which explanation, example, or verification step made the concept click?
- What should be shorter, more explicit, or moved to a different lesson?

Treat feedback as optional and do not invent an answer from silence. On the learner's next response, run this skill again, add the useful feedback to the existing canonical course-author review note, and make only evidence-supported lesson changes.
