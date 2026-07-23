# Mini-Project: DeskFlow — Hot Desk Booking API

**Theme:** Help the Finnish office manage hot desks. Employees need a simple way to see which desks are free and book one for a given day to work.

**Objective:** As a group, build a small Java REST API backed by a database, then present your work in **8~10 minutes**.

**Timebox:** ~5 hours build + presentation  
**Team size:** 4–5 people  
**AI tools:** Allowed and expected — use them to move fast, but you must understand and be able to explain every endpoint you ship and prepare yourself to questions at the end.

---

## Outcome

By the end of the day your team should have:

1. A running Java API (Spring Boot recommended) connected to a database
2. Seed data so a demo works without manual setup
3. A slide deck (**8–15 slides**) for a **10-minute** presentation

---

## Suggested time split

| Block            | Time          | Focus                                             |
| ---------------- | ------------- | -------------------------------------------------- |
| Kickoff          | 15 min        | Read the brief, pick roles, sketch the data model |
| Core build       | 2.5–3 h       | Database + required endpoints                     |
| Creative feature | 45–60 min     | One team-designed feature within the theme        |
| Polish + demo    | 30–45 min     | Seed data, error cases, happy-path walkthrough    |
| Slides           | 30–45 min     | Prepare an 8–15 slide deck                        |
| Present          | 10 min / team | Present your work                                 |

---

## Suggested roles (flexible)

- **API lead** — controllers, request/response shapes, HTTP status codes
- **Data lead** — schema, entities/repos, seed data
- **Feature lead** — creative endpoint(s) + validation rules
- **Demo / slides** — walkthrough prep and presentation

Everyone still contributes to code. Roles are for ownership, not silos. Everyone should present!

---

## Tech expectations

Keep the stack simple:

- **Java** + **Spring Boot** (or equivalent REST framework you already know)
- **Database:** **MySQL recommended**; H2, PostgreSQL, or another DB is fine if you prefer
- **Persistence:** JPA / JDBC — your choice
- **API style:** JSON over HTTP

You do **not** need authentication, Docker, or cloud deploy for the core brief. A frontend is **not** required for the core — see creative bonus below.

---

## Domain model (prescribed)

Use at least these concepts. Column names can vary; meaning should not.

### `desk`

| Field         | Notes                                     |
| ------------- | ------------------------------------------ |
| `id`          | Primary key                               |
| `code`        | Unique short code, e.g. `KRK-3F-12`       |
| `floor`       | Integer floor number                      |
| `has_monitor` | Boolean                                   |
| `is_active`   | Boolean — inactive desks cannot be booked |

### `booking`

| Field           | Notes                                  |
| --------------- | --------------------------------------- |
| `id`            | Primary key                            |
| `desk_id`       | FK → desk                              |
| `employee_name` | String                                 |
| `date`          | Booking date (one day per booking)     |
| `created_at`    | Timestamp when the booking was created |

**Core rule:** A desk may have **at most one booking per date**.

Seed at least **8 desks** across **2+ floors**, and a few sample bookings.

---

## Required API (follow these contracts)

Implement these so you can demo clear, consistent behaviour.

### 1. Health

```http
GET /api/health
```

**200** response example:

```json
{ "status": "ok" }
```

### 2. List desks

```http
GET /api/desks
```

Optional query params (implement at least one):

- `floor` — filter by floor
- `hasMonitor` — `true` / `false`

**200** — array of desks (include `id`, `code`, `floor`, `hasMonitor`, `active`).

### 3. Create a booking

```http
POST /api/bookings
Content-Type: application/json
```

Request body:

```json
{
    "deskId": 1,
    "employeeName": "Anna Kowalska",
    "date": "2026-07-24"
}
```

Behaviour:

- **201** + created booking JSON on success
- **404** if desk does not exist
- **400** if desk is inactive, date is missing/invalid, or `employeeName` is blank
- **409** if that desk is already booked on that date

### 4. List bookings for a date

```http
GET /api/bookings?date=2026-07-24
```

**200** — array of bookings for that date (include desk `code` or `deskId` so the demo is readable).

### 5. Cancel a booking

```http
DELETE /api/bookings/{id}
```

- **204** (or **200** with a short message) if deleted
- **404** if booking id does not exist

---

## Creative part (required, bonus, but open-ended)

Ship **one+** additional API feature that fits the DeskFlow theme. Your team designs the endpoint(s), request/response shape, and rules.

Pick something useful, for example:

- Availability: “which desks are free on date X?”
- Employee view: “all of my bookings”
- Desk search: free text / code prefix search
- Soft rules: block booking more than N days ahead
- Stats: bookings per floor for a date
- Waitlist / “notify me if cancelled” (can be simplified / in-memory)

**Nice bonus:** a small **frontend** (even a single HTML page) that talks to your API — e.g. list free desks for a date and create a booking. Optional; only after the required API and creative endpoint are working.

---

## Done means (acceptance checklist)

- [ ] App starts and `/api/health` returns ok
- [ ] Seed data loads on startup
- [ ] All 5 required endpoints work
- [ ] Double-booking the same desk/date returns **409**
- [ ] At least one creative feature works end-to-end
- [ ] Slide deck ready (**8–15 slides**, **10-minute** presentation)

---

## Presentation

**10 minutes** per team.  
**Deck size:** about **8–15 slides**.  
Every team member should be able to answer a question about the part they owned.

---

## Optional stretch (time filler / ambitious teams)

Do these only after the core + creative feature are solid:

1. **Validation polish** — consistent error JSON body, e.g. `{ "error": "...", "details": "..." }`
2. **Integration tests** — happy path + 409 conflict
3. **DTO mapping** — don’t expose JPA entities directly
4. **OpenAPI / Swagger** UI for the demo
5. **Docker Compose** with MySQL
6. **Audit field** — who cancelled / last updated
7. **Idempotency** — reject duplicate identical `POST` within a short window

---

## Constraints (on purpose)

- Stay inside the **hot desk booking** theme — no unrelated apps
- Prefer a thin, working vertical slice over a large unfinished design
- If stuck, cut scope: required endpoints first, then creative, then stretch / frontend bonus
- Do not spend the day on auth, microservices, or a polished production UI

---

## Deliverables

**Presentation only** — the slide deck (and the live talk / demo during the 10 minutes).  
Code submission is **not** required.

---
