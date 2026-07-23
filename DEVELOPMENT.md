# DeskFlow — Development Rules & Team Agreement

Working agreement for the ~5 hour build. Goal: minimize coordination overhead and
merge conflicts so the team ships all items in the brief's
[acceptance checklist](mini-project.md#done-means-acceptance-checklist).

## 1. Roles & Ownership

| Role | Owns |
|---|---|
| API lead | Controllers, request/response shapes, status codes |
| Data lead | Entities, repositories, DB constraints, seed data |
| Feature lead | Creative endpoint(s) + its validation rules |
| Demo/slides lead | curl/Postman demo script, slide deck |

Everyone still writes code. Roles are ownership, not silos — and per the brief,
**every member must be able to answer questions about their part.**

## 2. Contract-First Rule (do this before writing any controller code)

Before implementation starts, freeze in writing:

1. Exact JSON field names (camelCase) for every request/response in
   [ARCHITECTURE.md](ARCHITECTURE.md#6-api-contract).
2. The status-code table (already fixed by the brief — do not deviate).
3. The single error response shape.

**No renaming fields mid-build.** If a field name must change, announce it to the
whole team before editing shared DTOs.

## 3. Timeboxed Checkpoints (5 hours)

| Time | Checkpoint |
|---|---|
| T+15min | Contract frozen, roles assigned, toolchain confirmed |
| T+45min | Entities + repositories + seed data run and return data |
| T+1.5h | All 5 required endpoints return *something* (skeleton) |
| T+2.5h | All 5 required endpoints fully correct incl. 400/404/409 |
| T+3.5h | Creative endpoint working end-to-end |
| T+4h | Code frozen (bug fixes only); slides start |
| T+4.5h | Full demo rehearsed once, timed |

If a checkpoint slips by more than ~15 minutes, cut scope (drop to stretch goals
first, never drop a required endpoint).

## 4. Git Workflow

- **Trunk-based**, no long-lived branches. Short branches named
  `feature/desk`, `feature/booking`, `feature/creative` merged back within
  30–45 minutes of starting them.
- Whoever touches the DB schema (entities) commits and pushes **first**; everyone
  else pulls before starting their own work on top of it.
- Commit message convention: `feat: ...`, `fix: ...`, `chore: ...`,
  `docs: ...` — short, in English, describing the *behavior* change.
- Merge/push often in small diffs. Do not batch a whole module into one commit
  at the end.

## 5. Coding Conventions

- Packages by feature (`desk`, `booking`, `common`, `config`), not by layer type.
- Classes: `XxxController`, `XxxService`, `XxxRepository`, `XxxEntity` (or plain
  domain name), `XxxRequest`/`XxxResponse` for DTOs.
- REST paths: lower-case, plural nouns — `/api/desks`, `/api/bookings`.
- JSON fields: camelCase. Dates: ISO-8601 (`LocalDate`, no manual formatting).
- Controllers stay thin: no business logic, no direct repository calls.

## 6. Definition of Done (per endpoint, before merging to `main`)

- [ ] Matches the frozen contract (field names, status codes).
- [ ] Manually verified with curl/Postman for the success path **and** at least
      one failure path.
- [ ] Covered by seed data so the demo doesn't need manual setup.
- [ ] A one-line comment or note on any non-obvious business rule, so the owner
      can explain it when questioned.

## 7. Lightweight Review

No formal PR review given the timebox. Before merging, get one teammate to glance
at the diff for ~30 seconds (naming, obvious bugs, contract match). That's enough.

## 8. Escalation Rule

Stuck for more than 15 minutes → say so in the group chat immediately. Do not
debug silently — a single blocked person is the biggest risk to a 5-hour timebox.

## 9. Testing Expectations

Manual verification (curl/Postman) is the minimum bar for every endpoint. If time
remains after the required + creative work is solid, add automated tests for:
1. Happy path booking creation (201).
2. Double-booking conflict (409).

Do not write tests before the required endpoints are done — this is a stretch
item in the brief, not a core deliverable.

## 10. Presentation Readiness (must all be true before presenting)

- [ ] App starts and `/api/health` returns ok.
- [ ] Seed data loads automatically on startup.
- [ ] All 5 required endpoints verified live (not just "should work").
- [ ] Double-booking returns 409, demonstrated live.
- [ ] Creative feature demonstrated end-to-end.
- [ ] Slide deck ready (8–15 slides) and every member owns 1–2 slides.
