-- H2 seed data for DeskFlow (loaded every app start)
INSERT INTO desk (code, floor, has_monitor, is_active) VALUES
  ('KRK-2F-01', 2, TRUE,  TRUE),
  ('KRK-2F-02', 2, FALSE, TRUE),
  ('KRK-2F-03', 2, TRUE,  FALSE),
  ('KRK-3F-10', 3, TRUE,  TRUE),
  ('KRK-3F-11', 3, FALSE, TRUE),
  ('KRK-3F-12', 3, TRUE,  TRUE),
  ('KRK-4F-20', 4, TRUE,  TRUE),
  ('KRK-4F-21', 4, FALSE, TRUE);

INSERT INTO booking (desk_id, employee_name, date, created_at) VALUES
  (1, 'Anna Kowalska', '2026-07-24', CURRENT_TIMESTAMP),
  (4, 'Mikko Laine',   '2026-07-24', CURRENT_TIMESTAMP),
  (6, 'Sara Niemi',    '2026-07-25', CURRENT_TIMESTAMP);
