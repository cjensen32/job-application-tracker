# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project state

This repository currently contains only `PROJECT.md` (the spec/plan) and IDE metadata — no Spring Boot project has been scaffolded yet (no `pom.xml`, no source tree). There are no build, lint, or test commands to run until the skeleton from Milestone 1 exists. Once a Maven project is generated (e.g. via start.spring.io), update this file with the actual commands (`./mvnw spring-boot:run`, `./mvnw test`, `./mvnw test -Dtest=ClassName#method`, etc.) and any frontend tooling commands.

The user is using this project for interview practice and is teaching themselves the codebase via the CodeSensei plugin (`/code-sensei:*` commands) — favor explaining *why* behind non-trivial choices (JPA mappings, DTO/validation patterns, JWT auth flow) rather than just making changes silently, since that supports their learning goal.

## Learning plan drives the work

`LEARNING.md` (repo root) sequences PROJECT.md's milestones into 14 lessons with concept goals and build goals, and should drive the order of work. Two of its conventions matter when writing code here:

- **Explain before automating.** The first time a tool or concept appears, walk through it manually before reaching for the thing that generates it — hand-written `pom.xml` before start.spring.io, manual constructor wiring before `@Autowired`.
- **Tests start at Lesson 5**, not at PROJECT.md's Milestone 7 — write them alongside each layer instead of backfilling.

Background for calibrating explanations: one university Java course (exercises, never a full app), but real project experience with React/TSX and Python Flask/uvicorn. HTTP, REST, and JSON are familiar; Java idioms and the Spring layer are not. Analogies to Flask/React land well.

Update the progress table at the bottom of LEARNING.md as lessons complete.

## Intended architecture (from PROJECT.md)

Full-stack CRUD app for tracking job applications, built to practice Full Stack Java Developer interview skills: REST API design, JPA relationships, DTOs/validation, auth, and a frontend consuming the API.

**Stack:** Java 21, Spring Boot, Spring Data JPA, Spring Security (JWT), PostgreSQL, Maven, JUnit 5/Mockito/Spring Boot Test (MockMvc). Frontend is React (or Thymeleaf as a backend-only fallback).

**Core domain model:**
- `Application` — company, role, status (`APPLIED`, `PHONE_SCREEN`, `INTERVIEWING`, `OFFER`, `REJECTED`), appliedDate, notes, jobUrl
- `Interview` — date, round, notes; `@OneToMany` from `Application`, exposed via nested endpoints (`POST /api/applications/{id}/interviews`)

**Planned build order** (see PROJECT.md for full detail): skeleton API + Postgres connection → CRUD endpoints with DTOs/validation → filtering/sorting/pagination (`GET /api/applications?status=...`, `Pageable`) → `Interview` relationship → centralized error handling (`@ControllerAdvice` + custom exceptions like `ApplicationNotFoundException`) → JWT auth scoping applications to a user → test coverage across service/controller/repository layers → React frontend → stretch goals (status history, dashboard, reminders, deployment).

When implementing against this plan, follow the milestone order in PROJECT.md rather than jumping ahead (e.g. don't add auth before CRUD endpoints exist).
