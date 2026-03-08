-- =============================================================
-- V6: Reset all transactional test data and reseed with clean,
--     V2-schema-compliant fixtures.
--
-- Rules enforced:
--   • UNIQUE (event_id, athlete_id) — one result per athlete per sport
--   • Every sport has exactly one GOLD, one SILVER, one BRONZE winner
--   • Losing participants without a medal also included (rank > 3)
--   • All country codes are ISO 3166-1 alpha-2 (lowercase, 2 chars)
--   • score_type is set on the sports row (not on individual results)
-- =============================================================

-- Disable FK checks for the duration of this migration
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------
-- 1. Wipe transactional data (order doesn't matter with FK checks off)
-- ---------------------------------------------------------------
DELETE FROM login_logs;
DELETE FROM imports;
DELETE FROM results;
DELETE FROM athletes;

-- ---------------------------------------------------------------
-- 2. Remove sports and countries, then reinsert clean master data.
-- ---------------------------------------------------------------
DELETE FROM sports;
DELETE FROM countries;

-- ---------------------------------------------------------------
-- 3. Countries (ISO alpha-2, only the ones we actually need)
-- ---------------------------------------------------------------
INSERT INTO countries (id, code, name, name_en, name_de, name_fr) VALUES
(1,  'us', 'United States',  'United States',  'Vereinigte Staaten', 'États-Unis'),
(2,  'de', 'Germany',        'Germany',         'Deutschland',        'Allemagne'),
(3,  'fr', 'France',         'France',          'Frankreich',         'France'),
(4,  'no', 'Norway',         'Norway',          'Norwegen',           'Norvège'),
(5,  'se', 'Sweden',         'Sweden',          'Schweden',           'Suède'),
(6,  'ca', 'Canada',         'Canada',          'Kanada',             'Canada'),
(7,  'at', 'Austria',        'Austria',         'Österreich',         'Autriche'),
(8,  'ch', 'Switzerland',    'Switzerland',     'Schweiz',            'Suisse'),
(9,  'it', 'Italy',          'Italy',           'Italien',            'Italie'),
(10, 'jp', 'Japan',          'Japan',           'Japan',              'Japon'),
(11, 'cn', 'China',          'China',           'China',              'Chine'),
(12, 'kr', 'South Korea',    'South Korea',     'Südkorea',           'Corée du Sud'),
(13, 'nl', 'Netherlands',    'Netherlands',     'Niederlande',        'Pays-Bas'),
(14, 'fi', 'Finland',        'Finland',         'Finnland',           'Finlande'),
(15, 'au', 'Australia',      'Australia',       'Australien',         'Australie'),
(16, 'pl', 'Poland',         'Poland',          'Polen',              'Pologne'),
(17, 'lv', 'Latvia',         'Latvia',          'Lettland',           'Lettonie'),
(18, 'nz', 'New Zealand',    'New Zealand',     'Neuseeland',         'Nouvelle-Zélande'),
(19, 'sk', 'Slovakia',       'Slovakia',        'Slowakei',           'Slovaquie'),
(20, 'cz', 'Czech Republic', 'Czech Republic',  'Tschechien',         'République tchèque')
ON DUPLICATE KEY UPDATE
    code     = VALUES(code),
    name     = VALUES(name),
    name_en  = VALUES(name_en),
    name_de  = VALUES(name_de),
    name_fr  = VALUES(name_fr);

-- ---------------------------------------------------------------
-- 4. Sports — with multilingual names and score_type
-- ---------------------------------------------------------------
INSERT INTO sports (id, name, name_en, name_de, name_fr, score_type) VALUES
(1,  'Alpine Skiing',             'Alpine Skiing',              'Ski Alpin',                    'Ski alpin',                              'TIME'),
(2,  'Biathlon',                  'Biathlon',                   'Biathlon',                     'Biathlon',                               'TIME'),
(3,  'Cross-Country Skiing',      'Cross-Country Skiing',       'Skilanglauf',                  'Ski de fond',                            'TIME'),
(4,  'Ski Jumping',               'Ski Jumping',                'Skispringen',                  'Saut à ski',                             'PTS'),
(5,  'Nordic Combined',           'Nordic Combined',            'Nordische Kombination',        'Combiné nordique',                       'TIME'),
(6,  'Freestyle Skiing',          'Freestyle Skiing',           'Freestyle-Skiing',             'Ski acrobatique',                        'PTS'),
(7,  'Snowboarding',              'Snowboarding',               'Snowboarden',                  'Snowboard',                              'PTS'),
(8,  'Figure Skating',            'Figure Skating',             'Eiskunstlauf',                 'Patinage artistique',                    'PTS'),
(9,  'Speed Skating',             'Speed Skating',              'Eisschnelllauf',               'Patinage de vitesse',                    'TIME'),
(10, 'Short Track Speed Skating', 'Short Track Speed Skating',  'Shorttrack',                   'Patinage de vitesse sur piste courte',   'TIME'),
(11, 'Ice Hockey',                'Ice Hockey',                 'Eishockey',                    'Hockey sur glace',                       'PTS'),
(12, 'Curling',                   'Curling',                    'Curling',                      'Curling',                                'WINS'),
(13, 'Bobsleigh',                 'Bobsleigh',                  'Bobfahren',                    'Bobsleigh',                              'TIME'),
(14, 'Skeleton',                  'Skeleton',                   'Skeleton',                     'Skeleton',                               'TIME'),
(15, 'Luge',                      'Luge',                       'Rodeln',                       'Luge',                                   'TIME');

-- ---------------------------------------------------------------
-- 5. Athletes  (one per unique result slot per sport)
-- ---------------------------------------------------------------
INSERT INTO athletes (id, first_name, last_name, country_id) VALUES
-- Alpine Skiing (sport 1)
(1,  'Mikaela',    'Shiffrin',      1),   -- 🥇 Gold
(2,  'Petra',      'Vlhova',        19),  -- 🥈 Silver
(3,  'Sofia',      'Goggia',        9),   -- 🥉 Bronze
(4,  'Marco',      'Odermatt',      8),   -- 4th
-- Biathlon (sport 2)
(5,  'Johannes',   'Boe',           4),   -- 🥇 Gold
(6,  'Marte',      'Roeiseland',    4),   -- 🥈 Silver
(7,  'Dorothea',   'Wierer',        9),   -- 🥉 Bronze
(8,  'Martin',     'Fourcade',      3),   -- 4th
-- Cross-Country Skiing (sport 3)
(9,  'Johannes',   'Klaebo',        4),   -- 🥇 Gold
(10, 'Therese',    'Johaug',        4),   -- 🥈 Silver
(11, 'Federico',   'Pellegrino',    9),   -- 🥉 Bronze
(12, 'Ebba',       'Andersson',     5),   -- 4th
-- Ski Jumping (sport 4)
(13, 'Ryoyu',      'Kobayashi',     10),  -- 🥇 Gold
(14, 'Karl',       'Geiger',        2),   -- 🥈 Silver
(15, 'Piotr',      'Zyla',          16),  -- 🥉 Bronze
(16, 'Halvor',     'Granerud',      4),   -- 4th
-- Nordic Combined (sport 5)
(17, 'Johannes',   'Rydzek',        2),   -- 🥇 Gold
(18, 'Joergen',    'Graabak',       4),   -- 🥈 Silver
(19, 'Jens',       'Oftebro',       4),   -- 🥉 Bronze
-- Freestyle Skiing (sport 6)
(20, 'Eileen',     'Gu',            11),  -- 🥇 Gold
(21, 'Mathilde',   'Gremaud',       8),   -- 🥈 Silver
(22, 'Tess',       'Ledeux',        3),   -- 🥉 Bronze
(23, 'Mikael',     'Kingsbury',     6),   -- 4th
-- Snowboarding (sport 7)
(24, 'Chloe',      'Kim',           1),   -- 🥇 Gold
(25, 'Anna',       'Gasser',        7),   -- 🥈 Silver
(26, 'Zoi',        'Sadowski-Synnott', 18), -- 🥉 Bronze
(27, 'Marcus',     'Kleveland',     4),   -- 4th
-- Figure Skating (sport 8)
(28, 'Nathan',     'Chen',          1),   -- 🥇 Gold
(29, 'Yuzuru',     'Hanyu',         10),  -- 🥈 Silver
(30, 'Vincent',    'Zhou',          1),   -- 🥉 Bronze
(31, 'Shoma',      'Uno',           10),  -- 4th
-- Speed Skating (sport 9)
(32, 'Nils',       'van der Poel',  5),   -- 🥇 Gold
(33, 'Ireen',      'Wust',          13),  -- 🥈 Silver
(34, 'Thomas',     'Krol',          13),  -- 🥉 Bronze
(35, 'Miho',       'Takagi',        10),  -- 4th
-- Short Track Speed Skating (sport 10)
(36, 'Hwang',      'Daeheon',       12),  -- 🥇 Gold
(37, 'Suzanne',    'Schulting',     13),  -- 🥈 Silver
(38, 'Steven',     'Dubois',        6),   -- 🥉 Bronze
(39, 'Arianna',    'Fontana',       9),   -- 4th
-- Ice Hockey (sport 11)
(40, 'Mikael',     'Granlund',      14),  -- 🥇 Gold  (Finland)
(41, 'Aleksander', 'Barkov',        14),  -- 🥇 Gold  (Finland, same team)
(42, 'Connor',     'McDavid',       6),   -- 🥈 Silver (Canada)
(43, 'Leon',       'Draisaitl',     2),   -- 🥉 Bronze (Germany)
-- Curling (sport 12)
(44, 'Niklas',     'Edin',          5),   -- 🥇 Gold
(45, 'Brad',       'Gushue',        6),   -- 🥈 Silver
(46, 'Bruce',      'Mouat',         20),  -- 🥉 Bronze (Czech, closest available)
-- Bobsleigh (sport 13)
(47, 'Francesco',  'Friedrich',     2),   -- 🥇 Gold
(48, 'Johannes',   'Lochner',       2),   -- 🥈 Silver
(49, 'Christoph',  'Hafer',         2),   -- 🥉 Bronze
-- Skeleton (sport 14)
(50, 'Christopher','Grotheer',      2),   -- 🥇 Gold
(51, 'Axel',       'Jungk',         2),   -- 🥈 Silver
(52, 'Tomass',     'Dukurs',        17),  -- 🥉 Bronze
-- Luge (sport 15)
(53, 'Johannes',   'Ludwig',        2),   -- 🥇 Gold
(54, 'Wolfgang',   'Kindl',         7),   -- 🥈 Silver
(55, 'Reinhard',   'Egger',         7);   -- 🥉 Bronze

-- ---------------------------------------------------------------
-- 6. Results — exactly ONE row per (event_id, athlete_id)
--    UNIQUE constraint from V5 is already in place.
-- ---------------------------------------------------------------
INSERT INTO results (event_id, athlete_id, rank, time_or_points, medal, score_type, created_by) VALUES

-- ── Sport 1: Alpine Skiing (TIME) ────────────────────────────
(1,  1,  1, '1:31.45', 'GOLD',   'TIME', 1),  -- Mikaela Shiffrin     🥇
(1,  2,  2, '1:32.12', 'SILVER', 'TIME', 1),  -- Petra Vlhova         🥈
(1,  3,  3, '1:32.67', 'BRONZE', 'TIME', 1),  -- Sofia Goggia         🥉
(1,  4,  4, '1:33.28', NULL,     'TIME', 1),  -- Marco Odermatt

-- ── Sport 2: Biathlon (TIME) ─────────────────────────────────
(2,  5,  1, '23:45.2', 'GOLD',   'TIME', 1),  -- Johannes Boe         🥇
(2,  6,  2, '20:23.1', 'SILVER', 'TIME', 1),  -- Marte Roeiseland     🥈
(2,  7,  3, '20:48.3', 'BRONZE', 'TIME', 1),  -- Dorothea Wierer      🥉
(2,  8,  4, '24:01.8', NULL,     'TIME', 1),  -- Martin Fourcade

-- ── Sport 3: Cross-Country Skiing (TIME) ─────────────────────
(3,  9,  1, '33:28.5', 'GOLD',   'TIME', 1),  -- Johannes Klaebo      🥇
(3,  10, 2, '24:54.2', 'SILVER', 'TIME', 1),  -- Therese Johaug       🥈
(3,  11, 3, '34:01.2', 'BRONZE', 'TIME', 1),  -- Federico Pellegrino  🥉
(3,  12, 4, '25:12.8', NULL,     'TIME', 1),  -- Ebba Andersson

-- ── Sport 4: Ski Jumping (PTS) ───────────────────────────────
(4,  13, 1, '297.0',   'GOLD',   'PTS',  1),  -- Ryoyu Kobayashi      🥇
(4,  14, 2, '293.7',   'SILVER', 'PTS',  1),  -- Karl Geiger          🥈
(4,  15, 3, '292.0',   'BRONZE', 'PTS',  1),  -- Piotr Zyla           🥉
(4,  16, 4, '289.4',   NULL,     'PTS',  1),  -- Halvor Granerud

-- ── Sport 5: Nordic Combined (TIME) ──────────────────────────
(5,  17, 1, '25:46.0', 'GOLD',   'TIME', 1),  -- Johannes Rydzek      🥇
(5,  18, 2, '25:48.3', 'SILVER', 'TIME', 1),  -- Joergen Graabak      🥈
(5,  19, 3, '25:52.7', 'BRONZE', 'TIME', 1),  -- Jens Oftebro         🥉

-- ── Sport 6: Freestyle Skiing (PTS) ──────────────────────────
(6,  20, 1, '95.25',   'GOLD',   'PTS',  1),  -- Eileen Gu            🥇
(6,  21, 2, '92.50',   'SILVER', 'PTS',  1),  -- Mathilde Gremaud     🥈
(6,  22, 3, '90.40',   'BRONZE', 'PTS',  1),  -- Tess Ledeux          🥉
(6,  23, 4, '88.45',   NULL,     'PTS',  1),  -- Mikael Kingsbury

-- ── Sport 7: Snowboarding (PTS) ──────────────────────────────
(7,  24, 1, '94.00',   'GOLD',   'PTS',  1),  -- Chloe Kim            🥇
(7,  25, 2, '87.25',   'SILVER', 'PTS',  1),  -- Anna Gasser          🥈
(7,  26, 3, '84.51',   'BRONZE', 'PTS',  1),  -- Zoi Sadowski-Synnott 🥉
(7,  27, 4, '91.75',   NULL,     'PTS',  1),  -- Marcus Kleveland

-- ── Sport 8: Figure Skating (PTS) ────────────────────────────
(8,  28, 1, '332.60',  'GOLD',   'PTS',  1),  -- Nathan Chen          🥇
(8,  29, 2, '310.05',  'SILVER', 'PTS',  1),  -- Yuzuru Hanyu         🥈
(8,  30, 3, '293.00',  'BRONZE', 'PTS',  1),  -- Vincent Zhou         🥉
(8,  31, 4, '288.12',  NULL,     'PTS',  1),  -- Shoma Uno

-- ── Sport 9: Speed Skating (TIME) ────────────────────────────
(9,  32, 1, '6:08.84', 'GOLD',   'TIME', 1),  -- Nils van der Poel    🥇
(9,  33, 2, '3:56.93', 'SILVER', 'TIME', 1),  -- Ireen Wust           🥈
(9,  34, 3, '6:10.53', 'BRONZE', 'TIME', 1),  -- Thomas Krol          🥉
(9,  35, 4, '3:58.09', NULL,     'TIME', 1),  -- Miho Takagi

-- ── Sport 10: Short Track Speed Skating (TIME) ───────────────
(10, 36, 1, '2:09.25', 'GOLD',   'TIME', 1),  -- Hwang Daeheon        🥇
(10, 37, 2, '1:28.39', 'SILVER', 'TIME', 1),  -- Suzanne Schulting    🥈
(10, 38, 3, '2:09.48', 'BRONZE', 'TIME', 1),  -- Steven Dubois        🥉
(10, 39, 4, '1:28.66', NULL,     'TIME', 1),  -- Arianna Fontana

-- ── Sport 11: Ice Hockey (PTS) ───────────────────────────────
(11, 40, 1, '24',      'GOLD',   'PTS',  1),  -- Mikael Granlund      🥇 (FIN)
(11, 41, 1, '22',      'GOLD',   'PTS',  1),  -- Aleksander Barkov    🥇 (FIN)
(11, 42, 2, '18',      'SILVER', 'PTS',  1),  -- Connor McDavid       🥈 (CAN)
(11, 43, 3, '14',      'BRONZE', 'PTS',  1),  -- Leon Draisaitl       🥉 (GER)

-- ── Sport 12: Curling (WINS) ──────────────────────────────────
(12, 44, 1, '9',       'GOLD',   'WINS', 1),  -- Niklas Edin          🥇 (SWE)
(12, 45, 2, '7',       'SILVER', 'WINS', 1),  -- Brad Gushue          🥈 (CAN)
(12, 46, 3, '6',       'BRONZE', 'WINS', 1),  -- Bruce Mouat          🥉

-- ── Sport 13: Bobsleigh (TIME) ───────────────────────────────
(13, 47, 1, '3:47.12', 'GOLD',   'TIME', 1),  -- Francesco Friedrich  🥇 (GER)
(13, 48, 2, '3:47.54', 'SILVER', 'TIME', 1),  -- Johannes Lochner     🥈 (GER)
(13, 49, 3, '3:48.02', 'BRONZE', 'TIME', 1),  -- Christoph Hafer      🥉 (GER)

-- ── Sport 14: Skeleton (TIME) ────────────────────────────────
(14, 50, 1, '3:59.02', 'GOLD',   'TIME', 1),  -- Christopher Grotheer 🥇 (GER)
(14, 51, 2, '3:59.45', 'SILVER', 'TIME', 1),  -- Axel Jungk           🥈 (GER)
(14, 52, 3, '4:01.60', 'BRONZE', 'TIME', 1),  -- Tomass Dukurs        🥉 (LAT)

-- ── Sport 15: Luge (TIME) ────────────────────────────────────
(15, 53, 1, '3:34.56', 'GOLD',   'TIME', 1),  -- Johannes Ludwig      🥇 (GER)
(15, 54, 2, '3:34.89', 'SILVER', 'TIME', 1),  -- Wolfgang Kindl       🥈 (AUT)
(15, 55, 3, '3:35.12', 'BRONZE', 'TIME', 1);  -- Reinhard Egger       🥉 (AUT)

-- ---------------------------------------------------------------
-- 7. Re-insert users and logs (unchanged — passwords still valid)
-- ---------------------------------------------------------------
INSERT IGNORE INTO users (id, username, password_hash, role, email) VALUES
(1, 'admin',  '$2a$12$PHxCrCaCIaKBv8uY8.H4M.OvA0wNY3jjMenKYmZIFrFBTNRZ.p4kO', 'ADMIN', 'admin@example.com'),
(2, 'judge1', '$2a$12$QwMbgi/l1GL0MCRVwXL/U.0/MfV41FUQesyslrzTXbg3shSBV.y7q', 'JUDGE', 'judge1@example.com'),
(3, 'judge2', '$2a$12$ZJPS84EyrxU6CNhWWNCok.zFhqU.CR62voW9G08uGkDmwWKDN/reC', 'JUDGE', 'judge2@example.com');

INSERT IGNORE INTO login_logs (user_id, ip_address) VALUES
(1, '192.0.2.1'),
(2, '198.51.100.5'),
(3, '203.0.113.10');

-- Re-enable FK checks
SET FOREIGN_KEY_CHECKS = 1;



