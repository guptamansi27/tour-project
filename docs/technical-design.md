# https://github.com/guptamansi27/tour-project

# Malabar Trails — Technical Design Document

**Repository:** https://github.com/guptamansi27/tour-project  
**Live application:** Not deployed yet.  
**Prepared:** 6 November 2026  
**Document status:** Submission document; describes the checked-in implementation.

## 1. Purpose and summary

Malabar Trails is a tour discovery and enquiry management application for a Kerala-focused tour operator. Travellers browse destinations and published journeys, then submit a request for a tailored trip. Staff use a sign-in protected workspace to view enquiries and manage journey content.

The implementation consists of a React/Vite single-page frontend, a Java/Spring Boot REST API, and PostgreSQL. Flyway migrations manage the database schema. Enquiries are requests for follow-up, not confirmed reservations. The system records the enquiry and displays an acknowledgement; it does not send email or WhatsApp messages, take payment, or reserve travel inventory.

## 2. Assumptions (explicit)

The following are assumptions or deployment decisions because they are not established by a live production environment in the repository:

- **Assumption A1:** The intended operator is a single Kerala tour business, and the seeded packages/destinations are illustrative content for review. No multi-tenant behavior is implemented.
- **Assumption A2:** Prices are indicative INR amounts per person on twin sharing, as the public UI states. Tax treatment and final trip price are determined outside the application.
- **Assumption A3:** The intended deployment separates the static frontend, API service, and managed PostgreSQL. No cloud provider, hosting topology, production domain, or live URL is specified.
- **Assumption A4:** Staff credentials for a reviewer are provisioned locally from environment variables. There is no shared or preconfigured reviewer account in the repository and no public staff registration.
- **Assumption A5:** Production data retention, consent wording, and privacy obligations must be decided by the operator; the repository does not define a retention schedule.
- **Assumption A6:** A future SUPERADMIN would be a platform-level role distinct from an operator ADMIN. This role and its controls do not currently exist.
- **Assumption A7:** Gemini is an optional external provider. The operator must approve sending enquiry trip details (excluding name and contact fields in the prompt) to that service and review applicable provider/privacy terms.

All other statements below describe the checked-in source and configuration as inspected for this document.

## 3. Goals and scope

### Implemented scope

- Public destination index/guides and published package catalogue/details.
- Package search by text and region, itinerary, indicative price, inclusions/exclusions, stay information, media URLs, map details, and publication timing.
- Validated enquiry capture with optional package association, budget/message, WhatsApp consent, and idempotency-key handling.
- Staff login, dashboard, enquiry list/detail and status changes, package list/editor, role checks, and package version snapshots.
- Backend API workflows for package duplication/history restore, itinerary, seasonal prices, media URLs, enquiry assignment, notes, activity, and optional summary regeneration.

### Not implemented

Confirmed bookings, payment/refund processing, live supplier inventory, customer accounts, automatic message delivery, file upload/media storage, and a production superadmin console.

## 4. Architecture and rationale

```text
Traveller or staff browser
  └── React 19 + Vite SPA (React Router, Axios)
       └── JSON HTTP(S), credentialed API calls
            └── Java 21 / Spring Boot 3.4 REST API (/api)
                 ├── Spring Security + signed JWT cookie
                 ├── Spring Data JPA / Hibernate schema validation
                 ├── Flyway migrations V1–V5
                 └── PostgreSQL

Optional API integration: Google Gemini text generation for enquiry summaries
```

**Frontend choice:** React and Vite support a responsive, component-based browser experience and static asset deployment. React Router manages public and staff routes; lazy route loading keeps staff chart/table code out of the initial route chunk. Axios centralizes the API base URL and sends the session cookie with requests.

**Backend choice:** Spring Boot provides REST routing, validation, security integration, and relational persistence within one API service. A modular monolith is appropriate for the implemented workflows and keeps deployment simpler than multiple services.

**Database choice:** PostgreSQL suits related entities and workflow history. Flyway makes schema changes explicit and repeatable; Hibernate validates the schema rather than silently creating it.

**Authentication choice:** BCrypt stores password hashes. The API signs a one-hour JWT and returns it in an HttpOnly cookie. Role-based endpoint rules separate package/staff administration from enquiry work.

## 5. Components and main screens

### Public site

- **Home:** editorial hero with Kerala imagery, prominent journey-planning action, destination highlights, trust cues, and curated package cards. The visual hierarchy leads from inspiration to browsing.
- **Package catalogue:** image-led cards with region/search controls, duration and starting price. This supports quick comparison before a traveller opens details.
- **Package detail:** large destination image, itinerary, included/not-included information, stay and map details, and an enquiry form beside the trip content. Keeping the enquiry action in context reduces friction between evaluating and asking.
- **Destination index/detail:** large photography and concise guide content establish a sense of place, then link to related packages.
- **Enquiry form:** labelled fields collect contact details and trip constraints, with optional message, budget, and WhatsApp consent. Success copy clearly says the enquiry is recorded and no message was sent.

### Staff workspace

- **Login:** focused staff-only form separated from public browsing.
- **Overview dashboard:** status counts, enquiry pipeline chart, latest enquiries, and a shortcut to create a journey. The summary is intended for quick operational triage.
- **Enquiries:** tabular list and detail dialog expose traveller/contact/trip data, current status, and an action to change status. Backend APIs also support assignment, notes/activity, and AI summary; not all of those workflows are surfaced in the current UI.
- **Journeys and editor:** package list communicates publication state and price; the editor supports content maintenance. API capabilities for duplicate/history/restore, itinerary, pricing and media are broader than the visible console workflow.

### Design system decisions observed in source

The CSS uses a warm paper/off-white base, deep forest greens, muted earth/sand accents, restrained borders, and serif display headings with sans-serif body text. Large destination imagery and spacious layouts reinforce a considered travel-planning tone. Responsive breakpoints adapt grids and staff navigation for smaller screens. Loading skeletons and retry/empty states provide feedback while API data loads or fails. These are visual/interface choices, not evidence of formal user research.

## 6. Data model

| Record | Purpose |
|---|---|
| `staff_users` | Staff email, BCrypt password hash, and role (`ADMIN`, `EDITOR`, `CONSULTANT`). |
| `travel_packages` | Journey slug and copy, destination/region, price, duration, publication status/timing, and related trip details. |
| `package_versions` | JSON package snapshots indexed by version and editor identity for history/restore. |
| `itinerary_days` | Ordered day-by-day package plan. |
| `seasonal_prices` | Package pricing windows with date bounds. |
| `package_media` | Package image URL, alt text, and display order. |
| `destinations` | Public destination guide content, publication state, and presentation metadata. |
| `enquiries` | Traveller contact/trip preferences, optional package, status, assignee, consent, AI state, and idempotency key. |
| `enquiry_notes` | Staff-authored notes linked to an enquiry. |
| `enquiry_activity` | Status, assignment, and note activity entries linked to an enquiry. |

Flyway SQL in `backend/src/main/resources/db/migration/` defines the exact columns, constraints, and seed data. Current migration files are V1 through V5.

## 7. Main flows

### Browse and enquire

1. The SPA requests published destinations/packages from public API endpoints.
2. API results include published records; packages with a future `publishAt` are withheld until live.
3. The traveller opens a package and submits required contact details, preferred date, party size, plus optional budget/message and consent.
4. The API validates the payload and saves an enquiry. If an idempotency key already exists, it returns the prior enquiry rather than creating another.
5. The UI reports that the request was recorded. No outbound message is sent.

### Staff sign-in and enquiry handling

1. The operator supplies an initial admin email/password through environment configuration; on startup the API creates an ADMIN account if both are present and the email is absent.
2. Staff submit credentials to the login endpoint. The API checks the BCrypt hash and issues an expiring signed JWT cookie.
3. Authenticated requests use cookie credentials; backend authorization checks role for protected endpoints.
4. Staff review an enquiry and change its pipeline status. Related API endpoints provide assignment, notes/activity, and summary regeneration.

### Package editing and publishing

Staff with package permissions create or update package records. The API increments the version and stores a JSON snapshot. Related endpoints maintain itinerary, seasonal prices, and media URL records; duplication creates a draft copy and history can restore an earlier snapshot. Public package queries show published packages whose scheduled publication time has arrived.

## 8. AI feature

`GeminiEnquirySummaryService` is an optional server-side integration. When `GEMINI_API_KEY` is absent, it returns `UNAVAILABLE` and enquiry creation still proceeds. When configured, it sends destination, travel date, party size, budget, and traveller message to the Gemini `generateContent` API with instructions to create a concise factual internal brief, not invent details, and omit name/contact information. The model and API key are configurable on the backend. A successful response is stored as a summary with `READY`; provider or parsing failures result in `FAILED`. Staff can request regeneration through the admin summary endpoint.

The summary is an assistive note, not a booking decision or a message to the traveller. Human review is appropriate. This integration does not implement structured trip extraction, recommendations, reply drafting/sending, or other autonomous actions.

## 9. Security and personal data

Staff accounts are not self-registered. Passwords are BCrypt hashes. JWTs are signed and expire after one hour; the browser cookie is HttpOnly. CORS allows configured origin(s). API request records use validation bounds. Enquiries contain personal data and the code includes a WhatsApp-consent field.

**Observed production gaps:** `SecurityConfig` disables CSRF; `AuthService` currently sets the session cookie `Secure=false`. Before a public production launch, enable HTTPS-only cookies and implement a deliberate CSRF defense for cookie-authenticated writes. Also configure secret management, rate limiting/abuse controls, monitoring, backups, privacy notice, consent review, and data retention/deletion procedures. No claim is made that these operational controls are already in place.

## 10. What was cut and what comes next

### Cut from this implementation

The current project concentrates on discovery, enquiry capture, and first-stage staff operations. It does not implement payments, booking confirmation, flight/hotel inventory, external communication delivery, a full staff administration UI, image uploads/CDN, or broad automated testing/production operations. These exclusions keep the current scope focused; they are not hidden capabilities.

### Next steps

1. **Superadmin and user governance:** add a platform SUPERADMIN distinct from operator ADMIN; staff/user search, invitation, deactivation, password reset, role assignment, session revocation, and immutable admin audit events. If multiple operators are supported, enforce tenant ownership in service/data access and database queries.
2. Complete enquiry operations UI for assignment, internal notes, activity timeline, filters and pagination.
3. Add consent-aware email/WhatsApp integrations with delivery tracking, retries, opt-outs, and message audit records.
4. Build quote/booking lifecycle, supplier availability checks, payment gateway, invoices/refunds, and reconciliation only after inventory and commercial requirements are defined.
5. Add secure media uploads/object storage/CDN, destination CMS, previews, approvals, and scheduled publishing UI.
6. Add automated unit/integration tests and CI build, security/dependency checks, observability, backups/restore drills, and rollback procedures.
7. Expand AI assistance only with privacy controls, quotas, evaluation, and human approval at consequential steps.

## 11. Glossary

- **Enquiry:** a traveller request for information or a tailored proposal; not a reservation.
- **Idempotency key:** request identifier used to make retries avoid duplicate enquiry records.
- **SPA:** single-page application with browser-side route handling.
- **Superadmin:** proposed future platform-level administrator; not present in current roles.
