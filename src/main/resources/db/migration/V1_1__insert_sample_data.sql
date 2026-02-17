INSERT IGNORE INTO users (id, username, password_hash, role, email) VALUES
(1, 'admin', '$2a$12$PHxCrCaCIaKBv8uY8.H4M.OvA0wNY3jjMenKYmZIFrFBTNRZ.p4kO', 'ADMIN', 'admin@example.com'),
(2, 'judge1', '$2a$12$QwMbgi/l1GL0MCRVwXL/U.0/MfV41FUQesyslrzTXbg3shSBV.y7q', 'JUDGE', 'judge1@example.com'),
(3, 'judge2', '$2a$12$ZJPS84EyrxU6CNhWWNCok.zFhqU.CR62voW9G08uGkDmwWKDN/reC', 'JUDGE', 'judge2@example.com');

INSERT IGNORE INTO countries (id, code, name) VALUES
(1, 'USA', 'United States'),
(2, 'GER', 'Germany'),
(3, 'FRA', 'France');

INSERT IGNORE INTO sports (id, name) VALUES
(1, 'Swimming'),
(2, 'Athletics'),
(3, 'Gymnastics');

INSERT IGNORE INTO athletes (id, first_name, last_name, country_id, gender) VALUES
(1, 'Katie', 'Ledecky', 1, 'F'),
(2, 'Caeleb', 'Dressel', 1, 'M'),
(3, 'Max', 'Mustermann', 2, 'M'),
(4, 'Claire', 'Dupont', 3, 'F');

INSERT IGNORE INTO results (id, event_id, athlete_id, rank, time_or_points, medal, created_by) VALUES
(1, NULL, 1, 1, '3:59.34', 'GOLD', 2),
(2, NULL, 2, 2, '4:01.12', 'SILVER', 2),
(3, NULL, 3, 1, '9.85', 'GOLD', 3),
(4, NULL, 4, NULL, '12.34', NULL, 3);

INSERT IGNORE INTO imports (id, filename, imported_by) VALUES
(1, 'athletes_import_2026.csv', 2),
(2, 'results_import_2026.csv', 3);

INSERT IGNORE INTO login_logs (id, user_id, ip_address) VALUES
(1, 1, '192.0.2.1'),
(2, 2, '198.51.100.5'),
(3, 3, '203.0.113.10');
