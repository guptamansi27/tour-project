# Deployment and reviewer setup

## Prerequisites

Use Java 21, Maven 3.9+, Node.js 20+, and PostgreSQL 16. The frontend is a static Vite build; the API runs as a Spring Boot service.

## Local setup

1. Put local settings in the single root `.env`. Spring Boot explicitly imports it from either supported working directory, and Vite reads it automatically. Never commit secrets.
2. Start your local PostgreSQL service and create the database named in `DATABASE_URL` if needed.
3. Run the API from `backend/` with `mvn spring-boot:run`. Flyway applies schema and sample package records.
4. Set `ADMIN_EMAIL` and `ADMIN_PASSWORD` in the root `.env` before first API startup. The app stores a BCrypt hash and creates no public registration route.
5. Run `cd frontend`, `npm install`, and `npm run dev`. Vite reads the public `VITE_API_URL` from the single root `.env`.

## Environment variable names

`VITE_API_URL`, `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD`, `JWT_SECRET`, `ADMIN_EMAIL`, `ADMIN_PASSWORD`, `FRONTEND_ORIGIN`, `FRONTEND_ORIGINS`, `GEMINI_API_KEY`, `GEMINI_MODEL`.

Gemini variables are backend-only. When configured, Gemini creates an optional enquiry summary only; no message is sent automatically. Vite embeds `VITE_` variables in the public frontend bundle; never prefix a secret with `VITE_`.

## Deployment sequence

1. Provision managed PostgreSQL and restrict network access to the API service.
2. Deploy the Spring Boot service with database credentials, a long random JWT secret, bootstrap admin credentials, and the exact public frontend origin. Run once with admin bootstrap credentials, then rotate/remove `ADMIN_PASSWORD` after confirming account creation.
3. Confirm `/api/actuator/health` reports `UP` and Flyway migrations V1 through V5 have completed. Never amend an applied migration; create the next versioned migration instead.
4. Set `VITE_API_URL` to the HTTPS API URL in the root `.env` at frontend build time. From `frontend/`, run `npm run build` and deploy `frontend/dist/` to any static host configured to serve `index.html` for client-side routes.
5. Configure HTTPS and secure cookies at the edge. For cross-site frontend/API hosts, adjust cookie SameSite/Secure settings and CSRF strategy before production rollout.

## Reviewer demo login

Create a fresh reviewer account through `ADMIN_EMAIL` and `ADMIN_PASSWORD` at backend startup. Use the same values at `/admin/login`. No fixed credential is committed to this repository. Health check: `GET /api/actuator/health`. API docs: `/api/swagger-ui/index.html`.

## Limitations

No live deployment URL is provided. The current implementation supports local/staging review and includes operational itinerary, seasonal pricing, media, package-history, destination, and enquiry-note APIs. It still needs production cookie configuration, HTTPS, operational secrets management, full CMS controls for every API workflow, real image hosting/CDN, and full AI processing before production launch.
