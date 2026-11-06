# Malabar Trails

A Kochi-based tour operator website and staff studio for publishing South India journeys and managing travel enquiries. The repository contains a Vite + React frontend and a Spring Boot API backed by PostgreSQL. The frontend is a client-rendered SPA that loads public package data and staff records from the API.

## Features

- Public landing, destination guide/index, package catalogue, journey details and enquiry form.
- Staff sign-in with BCrypt password hashes and short-lived JWT in an HttpOnly cookie.
- Role-scoped staff dashboard, enquiry list, package editor, seasonal pricing, itineraries, media, history, duplicate and restore API workflows.
- Flyway-managed PostgreSQL schema, seeded Kerala journeys, destination records, package media, version snapshots, and enquiry activity/notes.
- Idempotent enquiry submission and honest acknowledgement simulation.
- Health endpoint at `/api/actuator/health`; Swagger UI at `/api/swagger-ui/index.html`.

## Architecture

`frontend/` contains the Vite + React application, including local destination artwork in `frontend/public/images/`. `backend/` is the Java 21 Spring Boot API. The root `.env` is shared by the frontend build and backend configuration. Axios requests use the browser's HttpOnly staff session cookie. Flyway migrations are the schema source of truth.

## Prerequisites and local run

- Node.js 20 or newer and npm.
- Java 21 and Maven 3.9+.
- PostgreSQL 16 installed and running locally.

1. Keep local settings in the single root `.env`. Spring Boot imports it from either the repository root or `backend/`; Vite is configured to read `VITE_API_URL` from the repository root while running in `frontend/`. Set a strong `JWT_SECRET`, `ADMIN_EMAIL`, and `ADMIN_PASSWORD` there.
2. Start PostgreSQL using your local PostgreSQL service. Create the database named in `DATABASE_URL` if it does not exist.
3. Start the API: `cd backend` then `mvn spring-boot:run`. Flyway creates and seeds the schema. The bootstrap account is created only when both admin variables are set and the address does not exist.
4. In a new terminal, run `cd frontend`, `npm install`, then `npm run dev`. Visit `http://localhost:3000`.

The API reads database settings from `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD`. The frontend reads `VITE_API_URL` at build time. Vite exposes variables prefixed with `VITE_` to browser code, so keep secrets under backend-only names such as `GEMINI_API_KEY` and `JWT_SECRET`.

## API overview

- `GET /api/packages?q=&region=`, `GET /api/packages/{slug}`, `GET /api/destinations`, and `GET /api/destinations/{slug}`: public journey and destination content.
- `POST /api/enquiries`: validated enquiry creation; optional `Idempotency-Key` header.
- `POST /api/auth/login` and `POST /api/auth/logout`: staff session.
- Admin/editor: package create/update, duplicate, history/restore, itinerary, seasonal-price and media endpoints. Admin/editor/consultant: enquiry status, assignment, notes and activity endpoints.

## Demo flow

Browse a package, send an enquiry, then sign in at `/admin/login`. Use the bootstrapped staff credentials. All requests and dashboard totals are backed by PostgreSQL. The enquiry acknowledgement is a record only; no email or WhatsApp provider is configured.

## Verification and limits

Run `cd frontend; npm run build` and `cd backend; mvn test`. The frontend build is written to `frontend/dist/`. Flyway migrations V1–V5 must remain forward-only; restart the API after an update so new migrations apply. Destination cards have resilient Munnar, Wayanad and Alappuzha imagery, with database records updated by V4/V5. Gemini currently produces only an optional enquiry summary; it does not provide extraction, recommendations or automated replies. The app requires locally running PostgreSQL and does not include payment, hotel/flight inventory, or actual message delivery integrations. No live deployment URL is configured.
