---
name: authoring-lessons
description: Rules for writing or revising a chapter, sub-lesson, or capstone in lessons/. Use when creating a new chapter folder, drafting a sub-lesson or CAPSTONE.md, writing or validating a hidden capstone test, or deciding where Java version-history material goes.
---

# Authoring lessons

**[`lessons/AUTHORING.md`](../../../lessons/AUTHORING.md) is the spec for building chapters — read it before writing one, and revise it rather than silently deviating.** It covers the folder layout, sub-lesson and capstone rules, how the hidden capstone tests work, the XP/quiz gate, and the version-notes convention.

Highlights that are easy to get wrong:

- The capstone test lives **outside `src/`** so it can't break `mvn test` mid-chapter, and **the user does not read it** — so `CAPSTONE.md` must spell out every class name, method signature and behaviour it asserts. Always validate a new capstone test against a throwaway reference implementation in the scratchpad, then delete it.
- **XP is banked once per chapter, after the capstone**, not per lesson. Sub-lessons end with self-check questions only.
- Java version-history material (`[J8]`-style tags → the chapter's `NOTES.md`) stays out of the main lesson flow. The user works in deprecated codebases and finds language evolution genuinely interesting, so it's wanted — just sidelined.

Update the progress table at the bottom of `LEARNING.md` as lessons complete.
