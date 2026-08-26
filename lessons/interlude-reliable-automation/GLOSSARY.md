# Reliable Automation Glossary

## Data processing

| Term                | Meaning                                                                                                                   |
|---------------------|---------------------------------------------------------------------------------------------------------------------------|
| aggregation         | Reducing many input rows to summaries such as counts, totals, minima, or maxima.                                          |
| reconciliation      | Proving transformed output accounts for the source input, including successful, skipped, and failed records.             |
| source of truth     | Authoritative system or dataset against which another result is checked.                                                  |
| deterministic shape | Output with predictable categories and fields, including zero-count categories, so consumers do not infer missing data. |
| `GROUP BY`          | SQL operation that forms groups from equal column values so an aggregate can be calculated per group.                    |

## Workflow reliability

| Term            | Meaning                                                                                                                       |
|-----------------|-------------------------------------------------------------------------------------------------------------------------------|
| idempotency     | Property that makes repeating the same operation produce no additional business effect.                                      |
| orchestration   | Coordination of multiple workflow steps, decisions, retries, and state transitions.                                          |
| retry policy    | Rules for which failures may be attempted again, how often, and with what delay.                                              |
| terminal failure| Failure that has exhausted safe retries or requires human judgment.                                                           |
| audit record    | Durable evidence of what ran, when, against which input, under which version, and with what outcome.                          |
| RPA             | Robotic process automation: software operating through user-interface boundaries when a stable API or direct integration is unavailable. |

## Diagnosability

| Term                 | Meaning                                                                                                                        |
|----------------------|--------------------------------------------------------------------------------------------------------------------------------|
| assertion failure    | Test failure where nothing threw: the production method returned a disliked value and its stack frame was already discarded.  |
| exception failure    | Test failure where something threw, so the stack trace names every production method live at that moment.                     |
| `Owner:` line        | Assertion-message field naming the production method that must change; a hand-written substitute for the missing frame.       |
| invariant guard      | A check inside production code that throws when its own result is impossible, converting a wrong answer into a located error. |

## Vocabulary bridges

| Java tracker idea                 | Python/Pandas vocabulary       | SQL vocabulary                         |
|-----------------------------------|--------------------------------|----------------------------------------|
| `List<Application>`               | list or DataFrame rows         | table rows                             |
| `EnumMap<Status, Long>`           | `Counter` or grouped Series    | grouped result set                     |
| loop and increment                | `Counter(...)` or `groupby`    | `COUNT(*)` plus `GROUP BY status`      |
| `sum(counts) == total`            | aggregate validation           | reconcile grouped count to total count |

## Answers to have cold

1. Reconciliation detects dropped, duplicated, or misclassified records even when the output looks plausible.
2. Zero-count categories make the response shape predictable and distinguish "none" from "not calculated."
3. Idempotency matters because schedulers, networks, and operators can all cause a run or delivery to repeat.
4. Retry transient failures with a limit and delay; send exhausted or business-rule failures to a visible manual-review path.
5. Prefer an API or direct integration when available. RPA is valuable when the supported boundary is a user interface, but UI changes increase brittleness and monitoring cost.
6. EventBridge Scheduler answers when to start; compute answers where code runs; Step Functions is useful only when explicit multi-step state and orchestration justify it.
7. An assertion failure cannot name the production method that caused it, because that method returned before JUnit threw. The `Owner:` line supplies the address; an invariant that throws inside the method supplies a real one.

**Interlude:** [Reliable Automation](README.md)
