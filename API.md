# DeskFlow — API Reference (per-parameter)

Detailed interface specification for the DeskFlow Hot Desk Booking API.
This complements [ARCHITECTURE.md](ARCHITECTURE.md) (high-level design) with
request/response parameter detail for each endpoint.

## Conventions

- **Base path**: `/api`
- **Content type**: `application/json; charset=utf-8`
- **Date format**: `YYYY-MM-DD` (`LocalDate`); timestamp: ISO-8601 `YYYY-MM-DDThh:mm:ssZ`
- **Unified error body** (all non-2xx responses, produced by `GlobalExceptionHandler`):
  ```json
  { "error": "CONFLICT", "message": "Desk already booked for this date", "timestamp": "2026-07-24T10:00:00Z" }
  ```

  | HTTP status | `error` value | Triggered by |
  |---|---|---|
  | 400 | `BAD_REQUEST` | validation failure, invalid date, inactive desk |
  | 404 | `NOT_FOUND` | unknown desk or booking id |
  | 409 | `CONFLICT` | desk already booked for that date |
  | 500 | `INTERNAL_ERROR` | unexpected error (fallback only) |

## Endpoint summary

| # | Method & Path | Purpose | Status codes |
|---|---|---|---|
| 1 | `GET /api/health` | Health check | 200 |
| 2 | `GET /api/desks` | List desks (filter by floor / hasMonitor) | 200 |
| 3 | `POST /api/bookings` | Create a booking | 201 / 404 / 400 / 409 |
| 4 | `GET /api/bookings?date=` | List bookings for a date | 200 / 400 |
| 5 | `DELETE /api/bookings/{id}` | Cancel a booking | 204 / 404 |
| C | `GET /api/desks/available?date=` | Creative: free desks for a date | 200 / 400 |

---

## 1 — Health check
`GET /api/health`

- **Input**: none
- **Output** (200):

  | Field | Type | Notes |
  |---|---|---|
  | status | string | always `"ok"` |

  ```json
  { "status": "ok" }
  ```

---

## 2 — List desks
`GET /api/desks`

- **Input** (query params, both optional, both implemented):

  | Param | Type | Required | Notes | Example |
  |---|---|---|---|---|
  | floor | int | no | filter by floor | `?floor=3` |
  | hasMonitor | boolean | no | filter by monitor | `?hasMonitor=true` |

  Combinable: `?floor=3&hasMonitor=true`. No params → all desks.
- **Output** (200): array of `DeskResponse`:

  | Field | Type | Notes |
  |---|---|---|
  | id | number | desk id |
  | code | string | unique short code, e.g. `KRK-3F-12` |
  | floor | number | floor number |
  | hasMonitor | boolean | has a monitor |
  | active | boolean | bookable |

  ```json
  [
    { "id": 1, "code": "KRK-3F-12", "floor": 3, "hasMonitor": true,  "active": true },
    { "id": 2, "code": "KRK-2F-05", "floor": 2, "hasMonitor": false, "active": true }
  ]
  ```
- **Expected**: always 200; no match → empty array `[]` (not 404).

---

## 3 — Create a booking
`POST /api/bookings`

- **Input** (request body `BookingRequest`):

  | Field | Type | Required | Validation |
  |---|---|---|---|
  | deskId | number | yes | `@NotNull` |
  | employeeName | string | yes | `@NotBlank` (non-empty, non-whitespace) |
  | date | string(date) | yes | `@NotNull`, format `YYYY-MM-DD` |

  ```json
  { "deskId": 1, "employeeName": "Anna Kowalska", "date": "2026-07-24" }
  ```
- **Output** (201, `BookingResponse`):

  | Field | Type | Notes |
  |---|---|---|
  | id | number | booking id |
  | deskId | number | desk id |
  | deskCode | string | desk short code (readable demo) |
  | employeeName | string | employee name |
  | date | string(date) | booking date |
  | createdAt | string(datetime) | creation timestamp |

  ```json
  {
    "id": 10, "deskId": 1, "deskCode": "KRK-3F-12",
    "employeeName": "Anna Kowalska", "date": "2026-07-24",
    "createdAt": "2026-07-23T10:15:00Z"
  }
  ```
- **Status-code matrix**:

  | Scenario | Status | Body example |
  |---|---|---|
  | Created | **201** | BookingResponse above |
  | Desk id does not exist | **404** | `{ "error": "NOT_FOUND", "message": "Desk 99 not found", "timestamp": "..." }` |
  | Desk `is_active=false` | **400** | `{ "error": "BAD_REQUEST", "message": "Desk 3 is inactive", "timestamp": "..." }` |
  | date missing / invalid | **400** | `{ "error": "BAD_REQUEST", "message": "date is required", "timestamp": "..." }` |
  | employeeName blank | **400** | `{ "error": "BAD_REQUEST", "message": "employeeName must not be blank", "timestamp": "..." }` |
  | Desk already booked that date | **409** | `{ "error": "CONFLICT", "message": "Desk already booked for this date", "timestamp": "..." }` |
- **Validation order**: input format (400) → desk exists (404) → desk active (400) → conflict check (409) → save → 201.

---

## 4 — List bookings for a date
`GET /api/bookings`

- **Input** (query params):

  | Param | Type | Required | Notes |
  |---|---|---|---|
  | date | string(date) | yes | query date `YYYY-MM-DD` |

  Missing / invalid `date` → **400**.
- **Output** (200): array of `BookingResponse` (same shape as endpoint 3):

  ```json
  [
    { "id": 10, "deskId": 1, "deskCode": "KRK-3F-12",
      "employeeName": "Anna Kowalska", "date": "2026-07-24",
      "createdAt": "2026-07-23T10:15:00Z" }
  ]
  ```
- **Expected**: no bookings that date → empty array `[]`.

---

## 5 — Cancel a booking
`DELETE /api/bookings/{id}`

- **Input** (path param):

  | Param | Type | Required | Notes |
  |---|---|---|---|
  | id | number | yes | booking id |
- **Output / expected**:

  | Scenario | Status | Body |
  |---|---|---|
  | Deleted | **204** | no body (or 200 + `{ "message": "Booking 10 cancelled" }`) |
  | Booking id not found | **404** | `{ "error": "NOT_FOUND", "message": "Booking 99 not found", "timestamp": "..." }` |

---

## C — Creative feature (recommended default)
`GET /api/desks/available`

> Final choice is up to the team; this is the recommended implementation.

- **Input** (query params):

  | Param | Type | Required | Notes |
  |---|---|---|---|
  | date | string(date) | yes | date to check availability |
  | floor | int | no | optional floor filter |
- **Output** (200): array of `DeskResponse` (same as endpoint 2), containing only
  desks that are **`active=true` and not booked on that date**.
- **Logic**: find the set of desk ids booked on `date` → return active desks not in
  that set. Reuses `DeskRepository` + `BookingRepository`.
- **Expected**: missing `date` → 400; no free desks → empty array `[]`.

**Alternative creative ideas** (pick one; shapes for reference):
- My bookings: `GET /api/bookings?employeeName=X` → array of BookingResponse
- Per-floor stats: `GET /api/stats?date=X` → `[{ "floor": 3, "count": 5 }, ...]`
- Advance-limit rule: in endpoint 3, reject `date` beyond today + N days → 400

---

## Verification (end-to-end)

1. **Start**: `mvn spring-boot:run` → `GET /api/health` returns `{"status":"ok"}`.
2. **Manual matrix** (curl / Postman, cover every status code):
   - `GET /api/desks?floor=3&hasMonitor=true` → 200 filtered
   - `POST /api/bookings` valid → 201
   - same desk+date again → **409** (core acceptance check)
   - unknown deskId → 404; inactive desk / blank name / missing date → 400
   - `GET /api/bookings?date=X` → shows the created booking
   - `DELETE /api/bookings/{id}` → 204; delete same id again → 404
   - `GET /api/desks/available?date=X` → returns free desks
3. **Integration tests** (H2 profile, `mvn test`): at least happy-path create + 409 conflict.
4. Tick off the brief's "Done means" checklist.
