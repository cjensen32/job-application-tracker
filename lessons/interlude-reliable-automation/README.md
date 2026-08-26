# Interlude — Reliable Automation

*Standalone plain-Java and design practice after the Chapter 1 capstone and before Spring.*

This interlude turns the tracker core into interview-ready practice for data processing and automation design without pretending the project uses Python, SQL, AWS, Snowflake, or Blue Prism. The implementation stays in Java 21; the cross-language and cloud material is vocabulary translation and system-design reasoning.

**Prerequisite:** complete the [Chapter 1 capstone](../ch01-java-foundations/CAPSTONE.md), or use Lesson 9's fast track once the current checkout compiles and exposes `ApplicationService.listAll()` and `Status`.

## Lessons

| #  | Lesson                                                                                  | Durable outcome                                                        | Target time |
|----|-----------------------------------------------------------------------------------------|------------------------------------------------------------------------|-------------|
| 9  | [Data processing and reconciliation](09-data-processing-and-reconciliation.md)          | Tested counts-by-status summary for the future dashboard                | 85–100 min  |
| 10 | [Designing reliable automation workflows](10-reliable-automation-workflows.md)           | Implementation-ready design for stale-application reminders            | 60–75 min   |

Use [GLOSSARY.md](GLOSSARY.md) for the terms to have cold. There is no interlude capstone or separate XP award; these lessons extend the completed tracker and then hand the course to Spring.

## Through-line

| Interlude work                         | Durable project seam                                              |
|----------------------------------------|-------------------------------------------------------------------|
| Count applications by every status     | Future dashboard summary                                          |
| Write the tests for your own feature   | The test-ownership split every later chapter uses                 |
| Reconcile transformed output to input  | Validation rule for reports and batch jobs                        |
| Translate the aggregation to SQL       | Persistence-layer reasoning without adding a database early       |
| Specify stale-reminder behavior        | Requirements for the reminder stretch goal                        |
| Design retries, idempotency, and audit | Operational contract before any local, AWS, or RPA implementation |

**Next chapter:** Spring Boot and the HTTP layer, beginning with Lesson 11.
