# Job Application Tracker

A full-stack CRUD app to track your own job search — built for interview practice with Spring Boot, JPA/Hibernate, and a simple frontend.

## Goal

Practice the core skills a Full Stack Java Developer interview will probe: REST API design, JPA entity relationships, DTOs/validation, auth, and a frontend that consumes the API — using a dataset (your own applications) you already care about.

## Tech Stack

- **Backend:** Java 21, Spring Boot, Spring Data JPA, Spring Security (JWT)
- **Database:** PostgreSQL
- **Frontend:** React (or plain Thymeleaf if you want to stay backend-focused)
- **Build:** Maven
- **Testing:** JUnit 5, Mockito, Spring Boot Test (MockMvc)

## Core Entity

`Application`
- company, role, status (`APPLIED`, `PHONE_SCREEN`, `INTERVIEWING`, `OFFER`, `REJECTED`), appliedDate, notes, jobUrl
- one-to-many `Interview` (date, round, notes) — practice a JPA relationship beyond the root entity

## Milestones (build in this order)

1. **Skeleton API** — Spring Boot project via start.spring.io, `Application` entity, Postgres connection, `ApplicationRepository`.
2. **CRUD endpoints** — `POST/GET/PUT/DELETE /api/applications`, request/response DTOs, `@Valid` input validation, proper HTTP status codes.
3. **Query features** — filter by status (`GET /api/applications?status=INTERVIEWING`), sort by appliedDate, pagination (`Pageable`).
4. **Related entity** — add `Interview` as `@OneToMany` on `Application`; nested endpoints (`POST /api/applications/{id}/interviews`).
5. **Error handling** — `@ControllerAdvice` + custom exceptions (e.g. `ApplicationNotFoundException`) returning consistent JSON error bodies.
6. **Auth** — Spring Security + JWT so applications are scoped to a logged-in user; register/login endpoints.
7. **Tests** — unit tests for service layer (Mockito), integration tests for controllers (MockMvc), repository tests (`@DataJpaTest`).
8. **Frontend** — minimal React app: list view with status filter, add/edit form, delete button, login screen.
9. **Stretch goals** — status-change history/timeline, dashboard (counts by status), email reminder for stale applications, deploy (Render/Railway + Vercel).

## Definition of Done for v1

- Can add, edit, delete, and filter applications through the UI, backed by the real API and Postgres — with auth in front of it.