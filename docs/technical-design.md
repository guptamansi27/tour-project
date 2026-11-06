# Malabar Trails — Technical Design

Repository: not yet connected to a GitHub URL. Live application: not deployed.

## Assumptions and scope

The operator is based in Kochi and sells Kerala/South India trips. Seed package content and prices are illustrative demo content. Prices are stated per person on twin sharing; final tax treatment must be confirmed in a tailored quote. Core scope covers public package discovery, enquiry capture, staff access, and package/enquiry administration. Payments, inventory integrations and actual message delivery are excluded.

## Architecture

```text
Traveller browser — Vite + React SPA (public pages and staff studio)
                    │ Axios / JSON over HTTP(S)
                    ▼
             Spring Boot REST API — Spring Security / JWT cookie
                    │ Spring Data JPA / Flyway
                    ▼
                 PostgreSQL
```

The Vite + React app lives in `frontend/` and builds a static `frontend/dist/` bundle. React Router handles public and admin routes; public package data and authenticated staff records load from the API in the browser. Admin access uses an HttpOnly cookie, which JavaScript cannot read. Static hosting must direct unknown paths to `index.html` for client-side routes. Vite reads its public API URL from the single root `.env` file.

## Technology choices

React with Vite provides a fast local development server and static production build. Route modules load on demand so admin table and chart libraries are excluded from the initial public page bundle. Java 21/Spring Boot provides validation, REST endpoints, and relational persistence. PostgreSQL with Flyway keeps schema changes explicit. BCrypt is used for passwords and JWT access expires after one hour. CSS custom properties provide reusable visual tokens.

## Data model

`staff_users` stores unique staff emails, BCrypt hashes, and `ADMIN`, `EDITOR`, or `CONSULTANT` roles. `travel_packages` stores public content, price basis, status, scheduling, location, stay and availability fields. `package_versions` stores JSON snapshots by package/version. `seasonal_prices`, `itinerary_days`, and `package_media` are child records. `destinations` stores public destination-guide content and imagery. `enquiries` references an optional package and stores traveller contact, party, budget, consent, source, assignee, status, AI state, and idempotency key; notes and activity are separate related records.

## API and journeys

Public: `GET /api/packages`, `GET /api/packages/{slug}`, `GET /api/destinations`, `GET /api/destinations/{slug}`, `POST /api/enquiries`. Staff: login/logout, dashboard, package CRUD plus duplicate/history/restore, itinerary/seasonal-price/media management, and enquiry status/assignment/note/activity workflows. `ADMIN` and `EDITOR` can manage packages; `CONSULTANT` can work the enquiry inbox but cannot change package content. Visitor journey: browse a destination or published detail page, submit a validated enquiry, receive a system-record acknowledgement.

## Security and data handling

Admin accounts are bootstrapped from environment variables and are not self-registered. Passwords are BCrypt hashes. JWT roles become Spring Security role authorities; endpoint rules enforce package and enquiry permissions. The cookie is HttpOnly, path-scoped, and expires after one hour. CORS is restricted to configured frontend origins. Enquiry and package DTO input is validated and size-limited. Production requires HTTPS cookie settings, production-grade secret storage, CSRF review, privacy retention policy, and rate limiting.

## AI, deployment and limitations

Gemini may generate an optional enquiry summary when configured; structured extraction, recommendations, reply drafting and sending are not implemented. Local setup uses an installed PostgreSQL service, the Spring Boot API, and the Vite development server from `frontend/`; production frontend deployment serves `frontend/dist/`. Flyway migrations V1–V5 are forward-only. Current gaps include full staff UI for all workflow APIs, real image upload/CDN, server rendering, production-ready cookie/CSRF tuning, and automated test coverage.
