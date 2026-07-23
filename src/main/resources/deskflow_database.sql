-- DeskFlow database setup (MySQL 8+)
-- Derived from API.md, ARCHITECTURE.md, DEVELOPMENT.md, and mini-project.md.

CREATE DATABASE IF NOT EXISTS deskflow
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE deskflow;

-- Drop in dependency order to allow reruns.
DROP TABLE IF EXISTS booking;
DROP TABLE IF EXISTS desk;

CREATE TABLE desk (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  code VARCHAR(32) NOT NULL,
  floor INT NOT NULL,
  has_monitor BOOLEAN NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (id),
  CONSTRAINT uq_desk_code UNIQUE (code),
  CONSTRAINT chk_desk_code_not_blank CHECK (CHAR_LENGTH(TRIM(code)) > 0),
  CONSTRAINT chk_floor_positive CHECK (floor > 0)
);

CREATE TABLE booking (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  desk_id BIGINT UNSIGNED NOT NULL,
  employee_name VARCHAR(120) NOT NULL,
  date DATE NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT fk_booking_desk
    FOREIGN KEY (desk_id) REFERENCES desk(id)
    ON UPDATE RESTRICT
    ON DELETE RESTRICT,
  -- Core business rule: one booking per desk per date.
  CONSTRAINT uq_booking_desk_date UNIQUE (desk_id, date),
  CONSTRAINT chk_booking_employee_name_not_blank CHECK (CHAR_LENGTH(TRIM(employee_name)) > 0)
);

-- Helpful indexes for API filters and date-based lookups.
CREATE INDEX idx_desk_floor_monitor_active ON desk (floor, has_monitor, is_active);
CREATE INDEX idx_booking_date ON booking (date);

-- Seed data: at least 8 desks across at least 2 floors.
INSERT INTO desk (code, floor, has_monitor, is_active) VALUES
  ('KRK-2F-01', 2, TRUE,  TRUE),
  ('KRK-2F-02', 2, FALSE, TRUE),
  ('KRK-2F-03', 2, TRUE,  FALSE),
  ('KRK-3F-10', 3, TRUE,  TRUE),
  ('KRK-3F-11', 3, FALSE, TRUE),
  ('KRK-3F-12', 3, TRUE,  TRUE),
  ('KRK-4F-20', 4, TRUE,  TRUE),
  ('KRK-4F-21', 4, FALSE, TRUE);

-- Sample bookings (respecting unique desk/date rule).
INSERT INTO booking (desk_id, employee_name, date, created_at) VALUES
  (1, 'Anna Kowalska', '2026-07-24', CURRENT_TIMESTAMP),
  (4, 'Mikko Laine',   '2026-07-24', CURRENT_TIMESTAMP),
  (6, 'Sara Niemi',    '2026-07-25', CURRENT_TIMESTAMP);

-- Example availability query used by GET /api/desks/available?date=YYYY-MM-DD
-- SELECT d.id, d.code, d.floor, d.has_monitor, d.is_active
-- FROM desk d
-- WHERE d.is_active = TRUE
--   AND d.id NOT IN (
--     SELECT b.desk_id
--     FROM booking b
--     WHERE b.date = '2026-07-24'
--   );
