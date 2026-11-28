INSERT IGNORE INTO treatment (id, name, description, price, duration_minutes) VALUES
(1, 'Hijama Cupping', 'Traditionel vakuumkop-behandling for cirkulation og afslapning.', 399.00, 30),
(2, 'Cupping Massage', 'Blide vakuumkopper masseres rundt for dyb afslapning.', 299.00, 30),
(3, 'Vibrations Terapi', 'Massagepistol og varmehoved til muskler og restitution.', 299.00, 30),
(4, 'Sportsbehandling', 'Udvidet cupping til sportsudøvere og atleter.', 999.00, 30),
(5, 'NADA Behandling', 'Afslappende øreakupunktur for ro og mental balance.', 399.00, 45);


INSERT IGNORE INTO booking (id, start_time, end_time, status) VALUES
(1, '2026-12-01 10:00:00', '2026-12-01 10:30:00', 'CONFIRMED'),
(2, '2026-12-01 13:00:00', '2026-12-01 13:45:00', 'PENDING'),
(3, '2026-12-01 14:00:00', '2026-12-01 14:30:00', 'CANCELLED');

INSERT IGNORE INTO working_day (id, day_of_week, start_time, end_time, active) VALUES
(1, 'MONDAY',    '10:00:00', '18:00:00', true),
(2, 'TUESDAY',   '10:00:00', '18:00:00', true),
(3, 'WEDNESDAY', '10:00:00', '18:00:00', true),
(4, 'THURSDAY',  '10:00:00', '18:00:00', true),
(5, 'FRIDAY',    '10:00:00', '18:00:00', true),
(6, 'SATURDAY',  '10:00:00', '16:00:00', false),
(7, 'SUNDAY',    '10:00:00', '16:00:00', false);

INSERT IGNORE INTO booking (id, start_time, end_time, status) VALUES
(1, '2026-12-01 10:00:00', '2026-12-01 10:30:00', 'CONFIRMED'),
(2, '2026-12-01 13:00:00', '2026-12-01 13:45:00', 'PENDING'),
(3, '2026-12-01 14:00:00', '2026-12-01 14:30:00', 'CANCELLED');