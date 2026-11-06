# Malabar Trails — Deployment Guide

**Repository:** https://github.com/guptamansi27/tour-project  
**Live app:** No deployed URL is configured in the repository.

## 1. Prerequisites

- Git
- Java 21 and Maven 3.9+
- Node.js 20+ and npm
- PostgreSQL 16 or compatible PostgreSQL service

The application has a Vite/React static frontend, a Spring Boot API (default port 8080, context path `/api`), and PostgreSQL. No provider-specific deployment, container, CI/CD configuration, or live URL is included. The hosted deployment below is therefore a provider-neutral target architecture, not a claim that the app is already deployed.

## 2. Environment variable names

Set the following names in a local root `.env` for development, or in each hosted service's environment/secret configuration. The `.env` file is ignored by Git. **This guide intentionally lists names only; do not commit secret values or put them in the ZIP.**

Backend:

- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `JWT_SECRET`
- `ADMIN_EMAIL`
- `ADMIN_PASSWORD`
- `FRONTEND_ORIGIN`
- `FRONTEND_ORIGINS`
- `GEMINI_API_KEY`
- `GEMINI_MODEL` 

Frontend build:

- `VITE_API_URL`

`VITE_API_URL` is embedded in browser assets at build time and must contain no secret. Never put database, JWT, admin, or Gemini credentials in a `VITE_` variable. `FRONTEND_ORIGINS`, when configured, takes precedence over `FRONTEND_ORIGIN`.

## 3. Local setup, step by step

1. Clone the repository and change to its root folder.
2. Create a private root `.env` containing the variables listed above. Configure the database connection and local frontend origin. Use a strong JWT secret of at least 32 bytes. Set `ADMIN_EMAIL` and `ADMIN_PASSWORD` for the reviewer account. Never share or commit this file.
3. Start PostgreSQL. Create the database named by `DATABASE_URL` if it does not exist and ensure the configured user can connect and create the schema.
4. Start the backend in a terminal:

   ```sh
   cd backend
   mvn spring-boot:run
   ```

   On startup Flyway applies the V1–V5 migrations, then Hibernate validates the schema. The initial ADMIN account is created only when both bootstrap variables are set and that email is not already registered.
5. Start the frontend in another terminal:

   ```sh
   cd frontend
   npm install
   npm run dev
   ```

   Open the local URL printed by Vite (the current project configuration uses port 3000). Confirm the API's allowed frontend origin matches the browser origin.
6. Browse destinations and journeys, submit a sample enquiry, and sign in to the staff studio at `/admin/login`.
7. Health endpoint: `/api/actuator/health`. API documentation: `/api/swagger-ui/index.html`.

## 4. Reviewer demo login

There is **no fixed demo username/password or live reviewer account** committed to the public repository. This avoids publishing a reusable password and matches the implementation: the first ADMIN is bootstrapped from the local environment only.

For a reviewer running the project locally:

- **Login page:** `http://localhost:3000/admin/login` (use the actual local Vite origin if different).
- **Username:** the email configured in the reviewer's private `ADMIN_EMAIL` setting.
- **Password:** the value configured in the reviewer's private `ADMIN_PASSWORD` setting before the first successful bootstrap.
- **Role:** `ADMIN`.

After the first account is created, changing bootstrap variables does not reset an existing account. No public registration or password reset flow is implemented. There are no valid shared credentials for a remote demo because no deployed app is configured.

## 5. Provider-neutral deployment outline

1. Provision PostgreSQL and restrict database network access to the API service. Enable backups and verify a restore process.
2. Configure backend environment variables in the host's secret manager. Provide database credentials, a unique JWT signing secret, the frontend origin, and initial admin bootstrap credentials. Set Gemini variables only if the optional summary feature is approved.
3. Deploy the Spring Boot service from `backend/pom.xml` on Java 21. Permit API access through HTTPS and allow the service to connect to PostgreSQL.
4. On startup, Flyway applies pending migrations. Confirm the API is healthy at `/api/actuator/health`; do not edit migrations already applied to a shared environment.
5. Confirm the initial ADMIN account exists, then remove `ADMIN_PASSWORD` from ongoing runtime configuration. The current application does not include a complete staff-user management screen.
6. Build frontend static assets from `frontend/` with `VITE_API_URL` set to the HTTPS API URL including `/api`:

   ```sh
   npm ci
   npm run build
   ```

7. Host `frontend/dist/` on a static hosting service. Configure SPA fallback so direct visits and page refreshes under `/packages/`, `/destinations/`, and `/admin/` serve `index.html`.
8. Configure HTTPS and exact trusted CORS origins. Verify credentialed browser requests reach the API and cookies work with the chosen domain topology.
9. Smoke-check public routes, package data, enquiry submission, staff sign-in/dashboard, health endpoint, and API docs. Remove any test personal data after review.

### Production hardening required

The current source disables CSRF protection and sets the JWT session cookie `Secure=false`. Before public production use, change the implementation to use HTTPS-only secure cookies and an appropriate CSRF protection strategy for cookie-authenticated state-changing requests. Configure rate limiting, secrets, backups, monitoring, and the operator's privacy/retention process. These are known deployment gaps, not completed setup steps.

## 6. Build commands

Frontend:

```sh
cd frontend
npm ci
npm run build
```

Backend (from `backend/`):

```sh
mvn test
mvn package
```

No claim is made that the full application was deployed or that these full build/test commands were run as part of preparing this document. `frontend/dist/` and `backend/target/` are generated build output, not submission source.

## 7. Important behavior and limitations

- An enquiry acknowledgement confirms database recording; no email or WhatsApp message is delivered.
- Gemini is optional. Without `GEMINI_API_KEY`, enquiry recording still works but summaries are unavailable.
- Enquiry data is personal data. Use synthetic data for review and establish retention/deletion controls before production.
- Packages and destinations use illustrative seed content. A published package is not proof of supplier availability or a confirmed booking.
- No production URL, fixed reviewer account, container, cloud provider, or automated deployment pipeline is configured in the repository.
