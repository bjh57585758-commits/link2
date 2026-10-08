# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

"밀양 맛집": a full-stack app where users list Miryang restaurants and leave reviews. Monorepo with two independent apps plus docs:

- `backend/` — Spring Boot 3.3, Java 17, Maven, Spring Data JPA, MySQL
- `frontend/` — React 19 + Vite + React Router (plain JS, no TypeScript)
- `docs/` — `DESIGN.md` (API/ERD), `DEPLOY.md` (deploy steps), `SUBMISSION.md` (assignment write-up)

User-facing text (UI strings, error messages, docs) is Korean; keep that convention.

## Commands

Backend (run from `backend/`):
- `mvn spring-boot:run` — starts on `:8080`; needs MySQL (see below)
- `mvn test` — integration tests use in-memory H2, no MySQL needed
- `mvn test -Dtest=RestaurantApiTest` — single test class
- `LC_ALL=C.UTF-8 mvn test -Dtest='RestaurantApiTest#*흐름'` — single test method (test names are Korean; without `LC_ALL=C.UTF-8` the filter matches 0 tests and Maven still reports BUILD SUCCESS, so check the "Tests run" count)
- `mvn -DskipTests package` — builds `target/app.jar` (finalName is `app`; the Dockerfile depends on this)

Frontend (run from `frontend/`):
- `npm install && npm run dev` — Vite dev server on `:5173`
- `npm run build` / `npm run lint` (oxlint) — there is no test runner configured

Local MySQL: `docker compose up -d` at the repo root (DB `miryang`, root/root on `:3306`). The `application.yml` defaults match it, so no env vars are needed locally. Copy `frontend/.env.example` to `.env` to point the UI at `http://localhost:8080`.

## Architecture

**Request flow:** React page → `frontend/src/api.js` (the only place that calls `fetch`; base URL from `VITE_API_URL`) → `RestaurantController` (`/api/**`) → `RestaurantService` → JPA repositories → MySQL. One controller and one service handle both restaurants and reviews.

**Domain:** `Restaurant` 1—N `Review`. Reviews are anonymous: there are no user accounts. The review author sets a password at creation, stored only as a BCrypt hash (`spring-security-crypto` only — Spring Security itself is NOT on the classpath, so there is no auth filter). Deletion is `DELETE /api/reviews/{id}` with the plaintext password in the `X-Review-Password` header; the service verifies it and throws `ForbiddenException` (403) on mismatch. DTOs are Java records and never expose the hash.

**FAQ board:** `/faq` page (한우소달구지 전용 단일 게시판) → `FaqController` (`/api/faqs`) → `FaqService`. Anyone can post a question (password-hashed like reviews, delete via `X-Review-Password`); answers are written with `PUT /api/faqs/{id}/answer` and the `X-Admin-Password` header matched against `ADMIN_PASSWORD`.

**Aggregates:** average rating and review count are computed on the fly via `ReviewRepository` queries (`findAverageRating`, `countByRestaurantId`) per restaurant in `RestaurantService.list` — an N+1 pattern that is fine at this scale but worth knowing before adding pagination.

**Errors:** `GlobalExceptionHandler` maps `NotFoundException` → 404, `ForbiddenException` → 403, bean-validation failures → 400, all as `{"message": "..."}`; `api.js` surfaces `message` to the UI.

**Frontend routing:** three pages in `src/pages/` (list, form, detail) wired in `App.jsx`. `frontend/vercel.json` rewrites all paths to `index.html` so direct visits to `/restaurants/1` work on Vercel.

## Configuration and deployment gotchas

- All environment-specific values come from env vars with local defaults in `backend/src/main/resources/application.yml`: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `CORS_ORIGINS` (comma-separated, no trailing slash, must exactly match the frontend origin), `PORT`, `DB_POOL_SIZE`, `ADMIN_PASSWORD` (FAQ 답변 작성용 관리자 비밀번호; 비어 있으면 답변/관리자 삭제 기능 비활성화), `DDL_AUTO` (default `update` — schema is auto-created, there are no migrations). If `DB_URL` is missing in production the app silently falls back to `localhost:3306` and crashes with `Connection refused`.
- `backend/src/test/resources/application.yml` fully replaces the main one during tests (H2 in MySQL mode, `create-drop`). H2 is test-scope only, so production can only run against MySQL.
- `VITE_API_URL` is baked in at build time; changing it on Vercel requires a redeploy.
- Production topology: Vercel (frontend, Root Directory `frontend`, Vite preset) → Render free Docker web service (`backend/Dockerfile`, health check `/actuator/health`; sleeps after 15 min idle) → Aiven free MySQL (needs `sslMode=REQUIRED` in the JDBC URL). Render has no managed MySQL, which is why Aiven is used. Vercel builds the `main` branch; pushing to `main` redeploys both hosts.
- The default branch is `main`; the assistant works on `claude/fullstack-deployment-setup-sbqrzl` and fast-forwards `main` only when the user asks.
