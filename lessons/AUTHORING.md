# Lesson & Capstone Authoring Spec

*How chapters in this repo get built. **This file is meant to be edited** — when something works badly, change the rule here rather than quietly doing it differently next chapter.*

Last revised: 2026-08-25 (defined standalone interludes, and made lessons author their own tests with owner-naming assertion messages).

Previously: 2026-08-19 (required method-level refactor destinations and inherited method inventories in minimal Initialization shells).

Previously: 2026-08-19 (added a pre-step Initialization section that establishes each lesson's file paths and compilable working shells from the previous recorded state).

Previously: 2026-08-18 (made unwrapped prose a repository-wide Markdown rule and clarified which Java fences are complete files, edit excerpts, API signatures, or standalone demonstrations).

Previously: 2026-08-17 (added concise-bullet and sentence-wrapping rules during the Chapter 1 capstone recalibration).

Previously: 2026-08-14 (added the **Verify** block — every step must end with a command that proves it landed. Prompted by Lesson 2 Step 3, where the step's only compile instruction was buried in a prose aside and the reader finished the step with a red build and no way to know).

Previously: 2026-08-14 (added the checkbox convention — see *Checkboxes* below).

Previously: 2026-08-13 (after Chapter 1 — pacing feedback: "about right, can speed up as we progress").

---

## Markdown source formatting

- Apply this rule to every Markdown file in the repository, including project documentation, lesson files, agent context, skills, and notes.
- Keep each prose paragraph, list item, and blockquote paragraph on one physical source line, regardless of length. Let the renderer handle visual wrapping.
- Never insert a physical line break in the middle of a sentence.
- Keep physical line breaks only when they are structurally or semantically intentional, such as a paragraph boundary, heading, new list item, table row, fenced or indented code line, HTML block boundary, or explicit Markdown hard break.

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

## Standalone interludes

An interlude may sit between chapter capstones when durable material belongs in the course sequence but not in either neighboring chapter. Give it its own `README.md`, numbered lesson files, and `GLOSSARY.md`; do not add a capstone, hidden grader, or separate XP award. Keep global lesson numbers continuous, state the completed chapter capstone as its prerequisite, and point its final lesson at the next chapter. Interludes follow every sub-lesson, Initialization, Verify, checkbox, source-formatting, and learner-ownership rule in this document.

---

## Sub-lessons

- Prefer concise bullets over long prose when a sequence, rule, or comparison can be scanned.
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

## Lesson initialization

Every sub-lesson begins with `## Initialization` after the goal, prerequisite, Progress list, and first divider, but before `## Step 1`. This is a pre-step that establishes the common working zone; it is not part of the lesson's assessed progress.

- Start from the last recorded state: normally the completed previous lesson, or the named chapter/commit baseline when no previous lesson changed the relevant code.
- Include only new assumptions and setup deltas needed for this lesson. Do not restate unchanged files or repeat the previous lesson's finished implementation.
- Name every created or edited file at least once with its full repository-relative path, such as `src/main/java/com/example/Thing.java`.
- For a new Java file, provide a complete compilable shell: package, imports, enclosing type, dependency fields, constructor initialization, and only the method stubs needed to establish the lesson's starting shape.
- A minimal shell may omit repeated implementations, but it must not erase work established by earlier lessons. Name every inherited operation that still exists after the refactor and show its new owning class, using stubs for coordinating methods and a concise comment inventory for repetitive helper families.
- For an existing class whose API or role changes, show the new assumption and include a transition comment in the code, such as `// toTable() -> render(): return text instead of printing it.` The optional reason should explain ownership or behavior, not narrate syntax.
- Keep the initialization dependency-closed: every new collaborator must be constructed or received somewhere, every changed constructor or method must have its callers accounted for, and the entry point must show the complete new object graph. Do not leave shells disconnected and make the learner guess how they are used.
- When responsibilities move between existing classes, include a conversion map from each old method or method section to a concrete method-level destination. Use suggested helper names or signatures instead of labels such as "input flow" or "output flow," and split mixed methods by responsibility instead of pretending the entire old method moves intact.
- Include one end-to-end flow sketch for a representative mixed operation. Show where prompting, service calls, and display handoffs occur without supplying the learner's implementation.
- Preserve the assignment: constructors may initialize fields, `void` method bodies stay empty, and non-void stubs only signal unfinished work. For a repetitive method family, show the first structural example and then add a comment naming the remaining methods the learner must complete instead of supplying repeated implementations.
- Keep incomplete behavior honest. A non-void stub may throw `UnsupportedOperationException("TODO")`; do not return a plausible value that could hide unfinished work.
- Use inline action checkboxes for setup the learner performs, but do not add Initialization to the top Progress list and do not add a learner-facing Verify block. The author still validates every shell against a throwaway copy of the stated prior state before publishing the lesson.

## Java code fences

Introduce every `java` fence according to what the reader should do with it. Do not present a fragment as though it were a complete file.

- **Whole file:** Name the destination path and include the package, imports, enclosing type, and complete braces. Outside Initialization, its Verify block must compile it; Initialization shells are author-validated under the rule above.
- **Edit excerpt:** Name the existing file and insertion point. Show the real enclosing type when placement would otherwise be ambiguous, and use a legal comment such as `// ... existing fields ...` for omitted code.
- **API signature:** A constructor or method signature may remain a fragment when the lesson is specifying shape rather than asking the reader to compile it independently. Label it as a contract.
- **Standalone demonstration:** Include all imports and a runnable type that needs only the JDK. Verify it in the scratchpad and show actual output.

## Verify blocks

**Every `## Step` ends with a `### Verify` section.** No exceptions — a step the reader can finish without knowing whether it worked is a broken step. This is the same "prove it with a command" rule as above, applied at a fixed place so it can't be forgotten.

A Verify block is exactly three things:

1. A checkbox and the command, unindented so it copy-pastes.
2. The **actual** output, trimmed — verified by running it against a scratchpad copy, never predicted. If the step produces no visible output, the verify is the build state itself.
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

## Lesson-authored tests

From Lesson 9 onward, **a lesson that adds behavior has the learner write the test for it**, not just read one. The capstone grader is then a harder version of something already written rather than an alien artifact. The [test-writing conventions](testing-standards/README.md) hold the full ownership table; the rules an author must apply are:

- Put the test file in the lesson's Initialization as a compilable shell with its `@DisplayName`, and grow it one test per step alongside the production code it covers.
- The learner writes fixture plumbing and single-fact assertions. Multi-step ordered scenarios, structural reflection checks, and adversarial inputs stay in the grader.
- **Every assertion the learner writes carries an `Owner:` / `Expected:` message.** An assertion failure discards the production frame before JUnit throws, so the assertion message is the only thing that can name the responsible method. Say this explicitly the first time a lesson asks for it rather than presenting it as a style preference.
- Where the production method can honestly check its own invariant, have the learner add the throw as well, and contrast the two failure shapes with real captured output. This is a checkpoint: break it, run it, then explain it.
- Never let a lesson test import from, duplicate, or reveal a chapter's hidden grader.

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

### Failure reports (Chapter 2 onward)

A grader failure must name the learner's method, not just the assertion line. Chapter 1's grader is frozen and keeps its original shape; every grader from Chapter 2 on uses this format.

```text
R-14 FAILED — statusFilteringNormalizesSpacesAndReportsNoMatches
Owner:    ConsoleApplication.filterApplications()
Expected: "No applications found for status OFFER.\n"

Matched through char 214, then diverged. Transcript ±3 lines:
     5 | 3 Edit application
     6 | 4 Delete application
     7 | 5 Quit
  ▸  8 | > No applications exist to filter & list!
     9 | 0 Create application
```

- **`R-##` tag** ties the failure to a numbered requirement in `CAPSTONE.md`, so the failure reads as a checklist item.
- **`Owner:`** names the production method responsible. This is a deliberate partial spoiler, accepted because the alternative is unnavigable.
- **`Expected:`** states the literal expectation and nothing else. No prose restating the contract — `CAPSTONE.md` already carries that.
- **A narrow window, never a full dump.** Show a few units of context either side of the divergence.
- The reporter is **course-owned infrastructure**, identical across chapters. Learners never write it.

Two constraints follow from the window:

- **One ordered assertion per test.** A window needs an anchor, and for a *missing* string the only honest anchor is the cursor from an ordered match — "everything before here matched, this was expected next." Independent `assertTrue(output.contains(x))` lambdas inside `assertAll` carry no cursor, cannot be windowed, and report as unlabeled `Multiple Failures (n failures)`. Collapse output checks into a single ordered assertion; keep state assertions over persisted objects separate, so tests read as two deliberate phases.
- **Anchor on character offsets, render lines.** Console prompts omit their trailing newline, so `> ` shares a physical line with whatever prints next. A line-based window is silently off by one at exactly the prompts that matter most.

### What the learner writes versus what the grader brings

From Chapter 2, lessons walk the learner through building that chapter's test harness, and the grader imports it. The capstone stays one standard beyond what the lessons required.

| Layer | Learner writes (lessons) | Capstone brings |
|-------|--------------------------|-----------------|
| Plumbing | build fixture, send input, capture output | — |
| Assertions | single-fact (`assertStatus`, `assertJsonField`) | ordered multi-step scenario composition |
| Reporting | plain JUnit messages | `R-##` / `Owner:` / windowed output |
| Structure | — | reflection checks on class and method shape |
| Inputs | happy path plus one edge | EOF, malformed, unknown ids, retries, adversarial |

The learner never writes the hard layer but can read it, because every primitive underneath is one they typed.

**A harness is chapter-local.** Chapter 1's console harness (`script`, `session`, `assertAppearsInOrder`) does not survive the move to Spring and HTTP; Chapter 2 builds its own from scratch. What carries between chapters is the report format and this ownership split, never the harness code. Do not contrive continuity by keeping a dead harness alive.

**Because the grader now imports learner code, `CAPSTONE.md` must publish the exact `TestSupport` signatures it calls,** and a lesson step must verify them. A missing method makes the grader fail to *compile* rather than fail a test, which is a worse experience than no ownership split at all. Publishing the contract that consumers depend on is itself the lesson.

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
