# Lesson 10 — Designing Reliable Automation Workflows

**Goal:** design a scheduled stale-application reminder that you can explain in five minutes and defend under questions about failures, retries, security, auditability, cloud deployment, and RPA.

**Prerequisite:** complete [Lesson 9](09-data-processing-and-reconciliation.md). This lesson creates a design artifact, not production reminder code or an AWS deployment.

**Progress**

- [ ] Turn a vague request into an intake contract
- [ ] Draw the workflow and failure model before choosing tools
- [ ] Add controls, reconciliation, and operational ownership
- [ ] Compare execution boundaries and rehearse the design
- [ ] Complete the self-check

---

## Initialization

Create one durable artifact at `docs/automation/stale-application-reminders.md`. No Java, dependency, cloud account, credential, or RPA installation changes in this lesson.

The design is intentionally implementation-neutral. Its decisions should survive whether the first version runs locally, on EC2, in Lambda, or through a UI automation boundary.

## Step 1 — Turn a vague request into an intake contract

"Remind me about stale applications" leaves the most important behavior undefined. Intake converts that sentence into rules that a product owner, implementer, reviewer, and support owner can all challenge before code exists.

- [ ] Create `docs/automation/stale-application-reminders.md` with this complete scaffold and replace the bracketed prompts in the first six sections.

```markdown
# Stale Application Reminder Workflow

## Goal and users

[Who needs this, what decision should it improve, and how will success be measured?]

## Trigger and frequency

[When does a run begin, in which time zone, and who may start an on-demand run?]

## Source of truth and inputs

[Which system is authoritative, which snapshot or version identifies the input, and which fields are required?]

## Stale rule

[State the age threshold, included or excluded statuses, date boundary, and time-zone rule precisely.]

## Outputs

[Name the reminder, recipients or destination, run summary, and retained audit evidence.]

## Non-goals

[Name what this first version will not update, infer, or send.]

## Happy path

[Fill in during Step 2.]

## Failure model and retries

[Fill in during Step 2.]

## Idempotency

[Fill in during Step 3.]

## Reconciliation

[Fill in during Step 3.]

## Audit record

[Fill in during Step 3.]

## Security and sensitive data

[Fill in during Step 3.]

## Observability and ownership

[Fill in during Step 3.]

## Execution-boundary decision

[Fill in during Step 4.]

## Open questions

[Fill in during Step 4.]
```

Write explicit non-goals. For this tracker, sensible examples include not changing application statuses automatically, not sending messages to employers, and not inferring rejection from age.

### Verify

- [ ] Confirm the intake headings exist in the design artifact.

```bash
rg -n '^## (Goal and users|Trigger and frequency|Source of truth and inputs|Stale rule|Outputs|Non-goals)$' docs/automation/stale-application-reminders.md
```

```text
3:## Goal and users
7:## Trigger and frequency
11:## Source of truth and inputs
15:## Stale rule
19:## Outputs
23:## Non-goals
```

If a heading is missing, restore the exact scaffold heading before continuing so later review commands remain useful.

## Step 2 — Draw the workflow and failure model before choosing tools

Technology does not decide what happens when one reminder fails after nine succeed. Model the business stages and failure categories first; only then choose an execution boundary.

- [ ] Replace `Happy path` with numbered stages for schedule, input read, validation, stale filtering, reminder preparation, delivery, and run completion.
- [ ] For every stage, name its input, output, and durable evidence.
- [ ] Under `Failure model and retries`, classify at least one input/validation failure, transient dependency failure, authorization failure, and business-rule ambiguity.
- [ ] State which failures are retryable, the retry limit and delay strategy, where terminal failures go, and who reviews them.

Use one run-level state and one item-level state. A run can finish `COMPLETED_WITH_FAILURES` when valid applications succeeded but specific items were routed to review; hiding partial success behind a single green status makes reconciliation impossible.

### Verify

- [ ] Confirm the artifact names every stage and failure decision.

```bash
rg -ni '^([0-9]+\..*(schedule|input|validat|filter|reminder|deliver|complet)|.*retry.*terminal.*manual review)' docs/automation/stale-application-reminders.md
```

```text
29:1. The schedule creates a run with a unique run id.
30:2. The input stage reads one identified source snapshot.
31:3. Validation separates usable records from records requiring review.
32:4. Filtering applies the documented stale rule.
33:5. Reminder preparation creates one deterministic delivery key per eligible application and window.
34:6. Delivery records success or a classified failure for each item.
35:7. Run completion reconciles all item outcomes and publishes the run state.
39:Transient failures retry with a bounded delay; exhausted retries become terminal failures in the manual review queue.
```

If the command finds a tool name but no business stage, rewrite the design around the work first; service selection comes in Step 4.

## Step 3 — Add controls, reconciliation, and operational ownership

Reliable automation assumes duplicate triggers, partial failures, expiring credentials, malformed data, and staff turnover will eventually happen. Controls make those events diagnosable instead of mysterious.

- [ ] Define an idempotency key such as `(applicationId, reminderWindow, ruleVersion)` and state where successful delivery is recorded before a retry can duplicate it.
- [ ] Define the reconciliation equation `input = eligible + skipped + failed-validation` and the delivery equation `eligible = succeeded + failed-delivery`; explain how a mismatch blocks a clean completion state.
- [ ] List audit fields for run id, timestamps, trigger, input version, rule/code version, actor or execution identity, counts, item outcomes, and error category without storing unnecessary sensitive content.
- [ ] State least-privilege access, secret storage and rotation ownership, encryption expectations, and which logs must redact or exclude sensitive values.
- [ ] Name structured log fields, metrics, alert thresholds, dashboard owner, on-call or support owner, and the manual replay procedure.

Auditability is not "log everything." It is retaining enough bounded evidence to reproduce what the automation decided while minimizing sensitive data and controlling access to the evidence itself.

### Verify

- [ ] Confirm every reliability control is explicit.

```bash
rg -n '^## (Idempotency|Reconciliation|Audit record|Security and sensitive data|Observability and ownership)$' docs/automation/stale-application-reminders.md
```

```text
41:## Idempotency
45:## Reconciliation
49:## Audit record
53:## Security and sensitive data
57:## Observability and ownership
```

If `Reconciliation` contains only a total, account separately for eligible, skipped, validation-failed, delivery-succeeded, and delivery-failed records.

## Step 4 — Compare execution boundaries and rehearse the design

Map the finished workflow to tools only after its requirements are visible. This is conceptual selection, not a claim that you have deployed these services.

| Boundary                    | Good fit when                                                                 | Pressure or cost to name                                                  |
|-----------------------------|-------------------------------------------------------------------------------|---------------------------------------------------------------------------|
| Local scheduled process     | Personal prototype, one owner, low consequence                                | Machine availability, credentials, weak centralized monitoring            |
| Existing EC2 service or job | Existing managed host, longer process, OS/runtime control required            | Patching, capacity, process supervision, deployment ownership              |
| EventBridge Scheduler + Lambda | Short, event-driven job with bounded runtime and little server management  | Runtime/service limits, stateless design, IAM, external durable state      |
| EventBridge Scheduler + Step Functions | Multiple explicit steps, branches, waits, or independently retried tasks | More orchestration state and cost than a simple single-function job needs |
| RPA process                 | The only supported boundary is a human-facing UI                              | UI brittleness, selectors, session state, credential handling, monitoring  |

AWS currently documents EventBridge Scheduler as a managed scheduler that invokes a target and supports retry policies plus dead-letter queues. Step Functions coordinates distributed components as explicit workflow states. Blue Prism separates a high-level process from reusable business objects that interact with external systems; its own Object Studio guidance notes that UI-oriented objects are typically for applications without a suitable API. Read those as service-selection concepts, not résumé experience: [EventBridge Scheduler](https://docs.aws.amazon.com/scheduler/latest/UserGuide/managing-schedule.html), [Step Functions](https://docs.aws.amazon.com/step-functions/), and [Blue Prism Object Studio](https://documentation.blueprism.com/bp-7-3/en-us/helpObjectStudio.htm).

- [ ] Under `Execution-boundary decision`, choose a first implementation and reject at least two alternatives with requirement-based reasons.
- [ ] Prefer a stable API or direct integration over RPA when one is supported; choose RPA only when the UI is the required boundary and name its operational costs.
- [ ] Under `Open questions`, write at least one question for a product owner, compliance reviewer, data owner, and operations/support owner.
- [ ] Rehearse a five-minute explanation in this order: goal, source and stale rule, happy path, failures and retries, idempotency and reconciliation, audit/security, then execution choice.
- [ ] Bridge to your real experience truthfully: use iMotions-DataCleaner as evidence of decomposing manual work, and explicitly say that Lambda, EventBridge Scheduler, Step Functions, Snowflake, and Blue Prism are conceptual gaps rather than shipped experience.

### Verify

- [ ] Check the final artifact for unresolved prompts and whitespace errors.

```bash
test -z "$(rg -n '\[Fill in|\[Who|\[When|\[Which|\[State|\[Name|\[What' docs/automation/stale-application-reminders.md)" && ! rg -n '[[:blank:]]+$' docs/automation/stale-application-reminders.md
```

```text
```

No output means the scaffold prompts and trailing whitespace are gone. If the command prints a line, finish the bracketed prompt or remove the reported trailing spaces and rerun it.

## Self-check

- [ ] Answer these without looking back, then deliver the five-minute walkthrough once without notes.

1. What is the workflow's source of truth, and how is one input snapshot identified?
2. Why does the idempotency key include both the application and reminder window?
3. Which failures retry, which do not, and where do exhausted failures go?
4. How do the two reconciliation equations expose dropped work?
5. What evidence must exist for an audit without logging unnecessary sensitive data?
6. When is Lambda preferable to EC2, and when is Step Functions unnecessary?
7. Why is RPA a boundary choice rather than a synonym for automation?

<details>
<summary>Answers</summary>

1. The design must name one authoritative system plus a snapshot, version, or timestamp that makes the processed input reproducible.
2. The application prevents duplicate items within a run; the window prevents repeated triggers or retries from sending the same business reminder again while allowing a later legitimate reminder.
3. Retry bounded transient failures. Validation, authorization, and ambiguous business rules usually need correction or human review; exhausted retries become visible terminal failures with ownership.
4. Every source row must become eligible, skipped, or validation-failed, and every eligible item must become delivery-succeeded or delivery-failed. Any difference names missing or duplicated work.
5. Retain run identity, time, trigger, input and rule versions, execution identity, categorized outcomes, and counts while minimizing payloads, redacting secrets, and restricting access.
6. Lambda fits short event-driven work when server management is undesirable; EC2 fits longer or host-controlled workloads. A single bounded function with simple retries does not need a state-machine service merely because one exists.
7. Automation is the goal; RPA is one integration mechanism for UI-only boundaries. APIs and direct integrations are usually less brittle and easier to test and observe.

</details>

## Where This Lands You

You have an implementation-ready reminder design and an honest five-minute system-design story covering intake, failure handling, validation, auditability, AWS service selection, and RPA tradeoffs. Chapter 2 can now introduce Spring without mixing it with this workflow-design vocabulary.

**Previous:** [Lesson 9 — Data Processing and Reconciliation](09-data-processing-and-reconciliation.md) · **Next:** Chapter 2, Lesson 11 — Annotations and the Spring Container · **Interlude:** [README](README.md)
