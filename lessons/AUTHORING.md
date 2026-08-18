# Lesson & Capstone Authoring Spec

*How chapters in this repo get built. **This file is meant to be edited** — when something works badly, change the rule here rather than quietly doing it differently next chapter.*

Last revised: 2026-08-17 (added concise-bullet and sentence-wrapping rules during the Chapter 1 capstone recalibration).

Previously: 2026-08-14 (added the **Verify** block — every step must end with a command that proves it landed. Prompted by Lesson 2 Step 3, where the step's only compile instruction was buried in a prose aside and the reader finished the step with a red build and no way to know).

Previously: 2026-08-14 (added the checkbox convention — see *Checkboxes* below).

Previously: 2026-08-13 (after Chapter 1 — pacing feedback: "about right, can speed up as we progress").

---

## Chapter layout

```
lessons/chNN-slug/
├── README.md                            chapter map, what survives to v1, the through-line table
├── NN-topic.md                          sub-lessons, numbered continuously across all chapters
├── GLOSSARY.md                          terms + Python/JS equivalents + answers to have cold
├── NOTES.md                             version history / language-evolution notes (optional but wanted)
├── CAPSTONE.md                          the assignment
└── capstone/
    └── ChapterNNCapstoneTest.java       the grader
```

---

## Sub-lessons

- Prefer concise bullets over long prose when a sequence, rule, or comparison can be scanned.
- Do not insert hard line breaks inside sentences in newly written or materially revised Markdown.
- Let renderers wrap prose naturally; use one source line per paragraph or list item.
- One idea per lesson. **Harder topics get split into more sub-lessons, not longer ones.** Security is three; testing is two.
- Teach the concept with a *small* amount of guided code. The bulk of the typing belongs in the capstone.
- **Explain before automating.** First appearance of any tool or concept gets a manual pass. Once that's done, scaffolding is fine and wanted.
- **Revisit, don't restart.** Apply the new idea to code that already exists. Lesson 2 re-packages Lesson 1's classes; Chapter 3 swaps storage under Chapter 1's service.
- Analogies to Flask/React/TS land well. HTTP, REST and JSON are already known — don't re-teach them.
- Every lesson ends with: self-check questions, collapsed answers, and a "where this lands you" pointing at the next one.
- Flag things that will be replaced later, and say what replaces them. Nothing should feel like it might be wasted effort.
- **Never assert a build state without a command that proves it.** Any claim that code compiles, fails, passes or breaks must be paired with the command to run and the *actual* output — verified by running it, not predicted. "The code won't compile until you fix three things" is a claim the reader will test, and being wrong about it costs more trust than the lesson buys.
  - Verify against a throwaway copy of the project in the scratchpad, never by editing the user's source. Paste real compiler output, trimmed.
  - Where the intuitive answer is wrong, make it a **checkpoint**: have the reader run the command that contradicts their model *before* explaining why. Surprise first, mechanism second.
  - Sequence steps so each one's failure is observed in isolation. If three edits are needed, don't list all three and then compile — compile after the edit that actually breaks the build, so the error message maps to one cause.
  - When a mechanism is easier to see by contrast, give the command that succeeds in one state and fails in the other, and put it in a `<details>` block so it stays optional.

## Verify blocks

**Every `## Step` ends with a `### Verify` section.** No exceptions — a step the reader can finish without knowing whether it worked is a broken step. This is the same "prove it with a command" rule as above, applied at a fixed place so it can't be forgotten.

A Verify block is exactly three things:

1. A checkbox and the command, unindented so it copy-pastes.
2. The **actual** output, trimmed — verified by running it against a scratchpad copy, never
   predicted. If the step produces no visible output, the verify is the build state itself.
3. One line naming the most likely failure and what it means.

```markdown
### Verify

- [ ] Compile and run

​```bash
mvn -q clean compile && java -cp target/classes com.connorjensen.jobtracker.Main
​```

​```
Acme Corp - Backend Engineer [APPLIED]
​```

If you get `cannot find symbol: class List`, you're missing an `import` — see the note above the fence.
```

Rules:

- **`mvn compile` is not enough on its own once there's something to run.** A step that changes behaviour verifies by running the thing and diffing the output; a step that only changes structure verifies with the build.
- **`mvn compile` does not compile tests.** Any step that touches `src/test` verifies with`mvn test`, and the expected `Tests run:` line goes in the output block.
- **Never leave the required command inside explanatory prose.** "You'll need `import java.util.List`" at the end of a paragraph is not an instruction the reader will act on — it needs its own checkbox, or it belongs in the code fence they copy.
- Steps whose whole point is an observed failure (Lesson 2's checkpoints) already satisfy this; they don't need a second block, but the step must still end green.

## Checkboxes

Lessons are worked through with a file open and things getting ticked off, so **every lesson and capstone carries two tiers of `- [ ]`**:

1. **A `**Progress**` list at the top**, directly after the goal / prerequisite block and before the first `---`. One item per `## Step`, phrased with the step's own wording so the two read as the same list, plus a final item for the self-check (capstones: a final item for the quiz).
2. **Inline boxes on every action inside a step** — anything the reader *does*: create a file, edit a file, run a command, observe an output. A box goes on its own line immediately above the code fence it belongs to; the fence stays unindented so it's still copy-pasteable.

Rules that keep it useful rather than noisy:

- **Actions only.** Explanation, tables of concepts, and "here's what that printed" prose get no box. If ticking it wouldn't mean something happened, it isn't a box.
- **Existing numbered instruction lists become checkbox lists** (capstone contract behaviours, supported commands, stretch goals, interview questions). Reference tables stay tables — checkbox syntax doesn't render inside a table cell.
- A checkbox item that wraps gets its continuation lines indented two spaces, or the list breaks.
- Prefer the imperative voice already in the text (`Create …`, `Run …`, `Finish the class yourself`) so converting a line to a box is usually just prefixing `- [ ]`.

Chapter 1 is the reference example.

## Capstones

- **Always builds part of the real project.** No exercises that get deleted. Where a piece is genuinely temporary (in-memory repo, console UI), name its replacement up front and make sure the *interface* around it survives.
- Name methods the way the eventual framework will name them, so the later swap is free — `findById`/`save`/`findByStatus` in Chapter 1 are Spring Data's names on purpose.
- Leave deliberate seams for later chapters, and say so in a callout (`IllegalArgumentException` → `ApplicationNotFoundException` → HTTP 404).
- End with: a "what the next chapter does to this code" table, and the interview questions the work earns.
- Stretch goals must also be real project features, tagged with where they come back.

## Capstone tests

- **The user does not read the test file.** It lives in `capstone/` outside `src/` so it never breaks `mvn test` mid-chapter; `CAPSTONE.md` gives the `cp` command to install it.
- Because it isn't read, **`CAPSTONE.md` must specify every class name, package, method signature and behavior the test asserts.** A test failure the spec didn't warn about is an authoring bug.
- Assertion messages must say what was expected without showing the implementation.
- Header comment: a clear spoiler warning plus the install command.
- Group with `@Nested` + `@DisplayName` so failures read as a checklist.
- Include at least one test that can only pass if the *concept* was understood, not just the API — Chapter 1 injects a foreign repository implementation to prove the service doesn't build its own.
- **Always validate before handing it over:** write a throwaway reference implementation in the scratchpad, run it, confirm green, delete it.

## Moving the goalposts

If the user solves a capstone in a different but valid direction, or it lands too easy or too hard, **rewrite the test and instructions to match.** The target is a working project, not a particular solution. Record any recalibration in this file's revision note.

---

## XP and quizzes

**XP is banked once per chapter, after the capstone** — not per lesson. Sub-lessons end with self-check questions only. The capstone's "done when" section is what triggers `/code-sensei:quiz`, covering the whole chapter's concepts at once.

Rationale: quizzing per lesson rewarded reading; quizzing after the capstone rewards having built the thing. Concepts still count as mastered at 3 correct answers.

---

## Version-history notes (`NOTES.md`)

Java's backward compatibility makes "x became y because z" unusually well-documented, and the user works in deprecated codebases, so this is useful as well as interesting.

**Keep it out of the main content.** Lessons carry a tag only:

```markdown
Java's `enum` <sup>[J5](NOTES.md#enums)</sup> is a real class with a fixed set of instances...
```

`[J5]` = introduced in Java 5, linking to the `NOTES.md` section. Nothing else goes inline.

`NOTES.md` entries should cover:

1. What version introduced it
2. What the code looked like before — real, compilable-looking old code, not a description
3. **Why it changed**, especially where the answer is "backward compatibility forced this design"(type erasure, default methods, `java.time` shipping alongside `java.util.Date`)
4. Backport difficulty, anchored on **Java 8** as the realistic legacy target, **Java 5** as the deep tier, **1.x** as curiosity

End each chapter's notes with a quick-reference table: feature → introduced in → Java 8 equivalent.

Chapter 1's `NOTES.md` is the reference example.

---

## Progress tracking

Update the progress table at the bottom of `LEARNING.md` and the chapter `README.md` as lessons complete. Commit lesson work separately from project source, tagged `LESSONS:`.
