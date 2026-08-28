# Agent Backbone

All canonical project authority and agent-specific content lives under `.agents/`. The code and learner-facing curriculum remain separate.

## Authority map

| File                                                     | Sole responsibility                                                                |
|----------------------------------------------------------|------------------------------------------------------------------------------------|
| [`CONTEXT.md`](CONTEXT.md)                               | Agent operating rules, ownership boundaries, verification, and maintenance limits  |
| [`PROJECT.md`](PROJECT.md)                               | Delivered product scope, technical architecture, definition of done, and non-goals |
| [`LESSON_MAINTENANCE.md`](LESSON_MAINTENANCE.md)         | Agent-only rules for correcting the frozen Chapter 1 curriculum                    |
| [`notes/PROJECT_CLOSEOUT.md`](notes/PROJECT_CLOSEOUT.md) | Closeout evidence, archive checksums, reconstruction, and rollback record          |
| `README.md`                                              | This backbone map and consolidation policy                                         |

Do not duplicate a normative rule across authorities. Keep the rule in its owner and link to it elsewhere.

## Ownership manifests

### Code and build

`src/**`, `pom.xml`, `config/**`, `.editorconfig`, `.gitignore`, `.githooks/**`, and the neutral root `README.md`.

### Learning and lessons

`lessons/README.md` and `lessons/ch01-java-foundations/**`.

### Agent backbone

`.agents/README.md`, `.agents/CONTEXT.md`, `.agents/PROJECT.md`, `.agents/LESSON_MAINTENANCE.md`, and `.agents/notes/PROJECT_CLOSEOUT.md`.

Every tracked path belongs to exactly one manifest. The root `README.md` is a neutral entry point classified with portable repository tooling; it may link to every surface but owns no agent rule or lesson contract.

## Dependency direction

`.agents -> lessons -> code` is the allowed documentation direction. `.agents/` may coordinate both other surfaces; lessons may teach against code; code/build must not depend on either documentation surface. Lessons must not link to `.agents/`.

## Durable and transient state

- Track the five authority documents above.
- Ignore transient `.agents/notes/*` except `PROJECT_CLOSEOUT.md`.
- Ignore `.agents/settings.local.json` and `.agents/worktrees/`.
- Keep only registered active worktrees under `.agents/worktrees/`; final closeout leaves it empty.
- Archive job profiles, scratch notes, provider settings, and retired workflow files outside the repository before removal.
- Tool-mandated root caches such as `.codegraph/` are local ignored state, not authority.

## Consolidation procedure

1. Inventory every `.agents/` file as authority, reusable workflow, durable evidence, transient note, job context, generated state, or worktree state.
2. Compare overlapping instructions and assign each durable rule to exactly one authority.
3. Replace necessary repetition with links and record removed sources in `notes/PROJECT_CLOSEOUT.md`.
4. Verify every link and named path, scan for duplicated normative language, and confirm the ownership manifests cover every tracked file once.
5. Export raw notes/settings before pruning. Never delete a dirty or unmerged worktree.
6. Keep agent changes, lesson changes, and learner code in separate commits.

This repository is closed. Consolidation is a maintenance rule, not permission to restart the roadmap.
