-- =============================================================
-- V7: Adjust test data for realistic athlete leaderboard scenarios.
--
-- Goals:
--   1. Multi-sport athletes to produce a meaningful Platz 1/2/3:
--        Platz 1 – Johannes Boe      (no): 3× Gold  (Biathlon + Cross-Country + Nordic Combined)
--        Platz 2 – Mikaela Shiffrin  (us): 2× Gold, 1× Silver (Alpine + Snowboarding gold, Freestyle silver)
--        Platz 3 – Eileen Gu         (cn): 1× Gold, 1× Silver, 1× Bronze (Freestyle + Fig.Skating + Speed Skating)
--   2. Edge-case: Skeleton has GOLD + BRONZE but NO SILVER.
--   3. All other single-sport athletes remain unchanged.
--
-- UNIQUE constraint (event_id, athlete_id) stays intact:
--   existing athletes appear in NEW sports → new rows, no duplicates.
-- =============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------
-- 1. Clear only results & athletes, keep countries/sports intact
-- ---------------------------------------------------------------
DELETE FROM results;
DELETE FROM athletes;

-- ---------------------------------------------------------------
-- 2. Athletes
--    Key multi-sport athletes reuse a single ID across all their sports.
--    Additional single-sport filler athletes keep the leaderboard realistic.
-- ---------------------------------------------------------------
INSERT INTO athletes (id, first_name, last_name, country_id) VALUES

-- ── Platz 1: Johannes Boe (no) ───────────────────────────────
-- Biathlon GOLD + Cross-Country GOLD + Nordic Combined GOLD = 3× 🥇
(5,  'Johannes',   'Boe',              4),

-- ── Platz 2: Mikaela Shiffrin (us) ───────────────────────────
-- Alpine Skiing GOLD + Snowboarding GOLD + Freestyle Skiing SILVER = 2× 🥇 1× 🥈
(1,  'Mikaela',    'Shiffrin',         1),

-- ── Platz 3: Eileen Gu (cn) ───────────────────────────────────
-- Freestyle Skiing GOLD + Figure Skating SILVER + Speed Skating BRONZE = 1× 🥇 1× 🥈 1× 🥉
(20, 'Eileen',     'Gu',               11),

-- ── Alpine Skiing filler (sport 1) ───────────────────────────
(2,  'Petra',      'Vlhova',           19),  -- 🥈 Silver
(3,  'Sofia',      'Goggia',           9),   -- 🥉 Bronze
(4,  'Marco',      'Odermatt',         8),   -- 4th

-- ── Biathlon filler (sport 2) ────────────────────────────────
(6,  'Marte',      'Roeiseland',       4),   -- 🥈 Silver
(7,  'Dorothea',   'Wierer',           9),   -- 🥉 Bronze
(8,  'Martin',     'Fourcade',         3),   -- 4th

-- ── Cross-Country Skiing filler (sport 3) ────────────────────
(10, 'Therese',    'Johaug',           4),   -- 🥈 Silver
(11, 'Federico',   'Pellegrino',       9),   -- 🥉 Bronze
(12, 'Ebba',       'Andersson',        5),   -- 4th

-- ── Ski Jumping (sport 4) ────────────────────────────────────
(13, 'Ryoyu',      'Kobayashi',        10),  -- 🥇 Gold
(14, 'Karl',       'Geiger',           2),   -- 🥈 Silver
(15, 'Piotr',      'Zyla',             16),  -- 🥉 Bronze
(16, 'Halvor',     'Granerud',         4),   -- 4th

-- ── Nordic Combined filler (sport 5) ─────────────────────────
(18, 'Joergen',    'Graabak',          4),   -- 🥈 Silver
(19, 'Jens',       'Oftebro',          4),   -- 🥉 Bronze

-- ── Freestyle Skiing filler (sport 6) ────────────────────────
-- Shiffrin takes Silver → Gremaud drops to 3rd, Ledeux out
(21, 'Mathilde',   'Gremaud',          8),   -- 🥉 Bronze
(22, 'Tess',       'Ledeux',           3),   -- 4th

-- ── Snowboarding filler (sport 7) ────────────────────────────
-- Shiffrin takes Gold → Kim drops to Silver, Gasser to Bronze
(24, 'Chloe',      'Kim',              1),   -- 🥈 Silver
(25, 'Anna',       'Gasser',           7),   -- 🥉 Bronze
(26, 'Zoi',        'Sadowski-Synnott', 18),  -- 4th

-- ── Figure Skating filler (sport 8) ──────────────────────────
-- Gu takes Silver → Chen still Gold, Zhou Bronze, Hanyu 4th
(28, 'Nathan',     'Chen',             1),   -- 🥇 Gold
(30, 'Vincent',    'Zhou',             1),   -- 🥉 Bronze
(29, 'Yuzuru',     'Hanyu',            10),  -- 4th
(31, 'Shoma',      'Uno',              10),  -- 5th

-- ── Speed Skating filler (sport 9) ───────────────────────────
-- Gu takes Bronze → van der Poel Gold, Wust Silver
(32, 'Nils',       'van der Poel',     5),   -- 🥇 Gold
(33, 'Ireen',      'Wust',             13),  -- 🥈 Silver
(34, 'Thomas',     'Krol',             13),  -- 4th
(35, 'Miho',       'Takagi',           10),  -- 5th

-- ── Short Track Speed Skating (sport 10) ─────────────────────
(36, 'Hwang',      'Daeheon',          12),  -- 🥇 Gold
(37, 'Suzanne',    'Schulting',        13),  -- 🥈 Silver
(38, 'Steven',     'Dubois',           6),   -- 🥉 Bronze
(39, 'Arianna',    'Fontana',          9),   -- 4th

-- ── Ice Hockey (sport 11) ────────────────────────────────────
(40, 'Mikael',     'Granlund',         14),  -- 🥇 Gold (FIN)
(41, 'Aleksander', 'Barkov',           14),  -- 🥇 Gold (FIN)
(42, 'Connor',     'McDavid',          6),   -- 🥈 Silver (CAN)
(43, 'Leon',       'Draisaitl',        2),   -- 🥉 Bronze (GER)

-- ── Curling (sport 12) ───────────────────────────────────────
(44, 'Niklas',     'Edin',             5),   -- 🥇 Gold (SWE)
(45, 'Brad',       'Gushue',           6),   -- 🥈 Silver (CAN)
(46, 'Bruce',      'Mouat',            20),  -- 🥉 Bronze

-- ── Bobsleigh (sport 13) ─────────────────────────────────────
(47, 'Francesco',  'Friedrich',        2),   -- 🥇 Gold
(48, 'Johannes',   'Lochner',          2),   -- 🥈 Silver
(49, 'Christoph',  'Hafer',            2),   -- 🥉 Bronze

-- ── Skeleton (sport 14) — EDGE CASE: no Silver ───────────────
(50, 'Christopher','Grotheer',         2),   -- 🥇 Gold
(51, 'Axel',       'Jungk',            2),   -- 4th (no medal → Silver slot intentionally empty)
(52, 'Tomass',     'Dukurs',           17),  -- 🥉 Bronze

-- ── Luge (sport 15) ──────────────────────────────────────────
(53, 'Johannes',   'Ludwig',           2),   -- 🥇 Gold
(54, 'Wolfgang',   'Kindl',            7),   -- 🥈 Silver
(55, 'Reinhard',   'Egger',            7);   -- 🥉 Bronze

-- ---------------------------------------------------------------
-- 3. Results — one row per (event_id, athlete_id)
-- ---------------------------------------------------------------
INSERT INTO results (event_id, athlete_id, rank, time_or_points, medal, score_type, created_by) VALUES

-- ── Sport 1: Alpine Skiing (TIME) ────────────────────────────
-- Shiffrin (1) wins Gold here
(1,  1,  1, '1:31.45', 'GOLD',   'TIME', 1),  -- Mikaela Shiffrin     🥇
(1,  2,  2, '1:32.12', 'SILVER', 'TIME', 1),  -- Petra Vlhova         🥈
(1,  3,  3, '1:32.67', 'BRONZE', 'TIME', 1),  -- Sofia Goggia         🥉
(1,  4,  4, '1:33.28', NULL,     'TIME', 1),  -- Marco Odermatt

-- ── Sport 2: Biathlon (TIME) ─────────────────────────────────
-- Boe (5) wins Gold here
(2,  5,  1, '20:45.2', 'GOLD',   'TIME', 1),  -- Johannes Boe         🥇
(2,  6,  2, '20:23.1', 'SILVER', 'TIME', 1),  -- Marte Roeiseland     🥈
(2,  7,  3, '20:48.3', 'BRONZE', 'TIME', 1),  -- Dorothea Wierer      🥉
(2,  8,  4, '24:01.8', NULL,     'TIME', 1),  -- Martin Fourcade

-- ── Sport 3: Cross-Country Skiing (TIME) ─────────────────────
-- Boe (5) wins Gold here too
(3,  5,  1, '33:15.0', 'GOLD',   'TIME', 1),  -- Johannes Boe         🥇 (multi-sport)
(3,  10, 2, '24:54.2', 'SILVER', 'TIME', 1),  -- Therese Johaug       🥈
(3,  11, 3, '34:01.2', 'BRONZE', 'TIME', 1),  -- Federico Pellegrino  🥉
(3,  12, 4, '25:12.8', NULL,     'TIME', 1),  -- Ebba Andersson

-- ── Sport 4: Ski Jumping (PTS) ───────────────────────────────
(4,  13, 1, '297.0',   'GOLD',   'PTS',  1),  -- Ryoyu Kobayashi      🥇
(4,  14, 2, '293.7',   'SILVER', 'PTS',  1),  -- Karl Geiger          🥈
(4,  15, 3, '292.0',   'BRONZE', 'PTS',  1),  -- Piotr Zyla           🥉
(4,  16, 4, '289.4',   NULL,     'PTS',  1),  -- Halvor Granerud

-- ── Sport 5: Nordic Combined (TIME) ──────────────────────────
-- Boe (5) wins Gold here too → 3× Gold total = Platz 1
(5,  5,  1, '25:41.0', 'GOLD',   'TIME', 1),  -- Johannes Boe         🥇 (multi-sport)
(5,  18, 2, '25:48.3', 'SILVER', 'TIME', 1),  -- Joergen Graabak      🥈
(5,  19, 3, '25:52.7', 'BRONZE', 'TIME', 1),  -- Jens Oftebro         🥉

-- ── Sport 6: Freestyle Skiing (PTS) ──────────────────────────
-- Gu (20) wins Gold; Shiffrin (1) takes Silver → 2nd Gold-sport for Shiffrin later
(6,  20, 1, '95.25',   'GOLD',   'PTS',  1),  -- Eileen Gu            🥇 (multi-sport)
(6,  1,  2, '91.80',   'SILVER', 'PTS',  1),  -- Mikaela Shiffrin     🥈 (multi-sport)
(6,  21, 3, '90.40',   'BRONZE', 'PTS',  1),  -- Mathilde Gremaud     🥉
(6,  22, 4, '88.45',   NULL,     'PTS',  1),  -- Tess Ledeux

-- ── Sport 7: Snowboarding (PTS) ──────────────────────────────
-- Shiffrin (1) wins Gold → 2× Gold total = Platz 2
(7,  1,  1, '94.00',   'GOLD',   'PTS',  1),  -- Mikaela Shiffrin     🥇 (multi-sport)
(7,  24, 2, '87.25',   'SILVER', 'PTS',  1),  -- Chloe Kim            🥈
(7,  25, 3, '84.51',   'BRONZE', 'PTS',  1),  -- Anna Gasser          🥉
(7,  26, 4, '80.10',   NULL,     'PTS',  1),  -- Zoi Sadowski-Synnott

-- ── Sport 8: Figure Skating (PTS) ────────────────────────────
-- Gu (20) takes Silver; Chen still Gold
(8,  28, 1, '332.60',  'GOLD',   'PTS',  1),  -- Nathan Chen          🥇
(8,  20, 2, '318.45',  'SILVER', 'PTS',  1),  -- Eileen Gu            🥈 (multi-sport)
(8,  30, 3, '293.00',  'BRONZE', 'PTS',  1),  -- Vincent Zhou         🥉
(8,  29, 4, '288.12',  NULL,     'PTS',  1),  -- Yuzuru Hanyu
(8,  31, 5, '275.00',  NULL,     'PTS',  1),  -- Shoma Uno

-- ── Sport 9: Speed Skating (TIME) ────────────────────────────
-- Gu (20) takes Bronze → 1× Gold, 1× Silver, 1× Bronze = Platz 3
(9,  32, 1, '6:08.84', 'GOLD',   'TIME', 1),  -- Nils van der Poel    🥇
(9,  33, 2, '3:56.93', 'SILVER', 'TIME', 1),  -- Ireen Wust           🥈
(9,  20, 3, '6:11.20', 'BRONZE', 'TIME', 1),  -- Eileen Gu            🥉 (multi-sport)
(9,  34, 4, '6:12.45', NULL,     'TIME', 1),  -- Thomas Krol
(9,  35, 5, '3:58.09', NULL,     'TIME', 1),  -- Miho Takagi

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

-- ── Sport 12: Curling (WINS) ─────────────────────────────────
(12, 44, 1, '9',       'GOLD',   'WINS', 1),  -- Niklas Edin          🥇 (SWE)
(12, 45, 2, '7',       'SILVER', 'WINS', 1),  -- Brad Gushue          🥈 (CAN)
(12, 46, 3, '6',       'BRONZE', 'WINS', 1),  -- Bruce Mouat          🥉

-- ── Sport 13: Bobsleigh (TIME) ───────────────────────────────
(13, 47, 1, '3:47.12', 'GOLD',   'TIME', 1),  -- Francesco Friedrich  🥇 (GER)
(13, 48, 2, '3:47.54', 'SILVER', 'TIME', 1),  -- Johannes Lochner     🥈 (GER)
(13, 49, 3, '3:48.02', 'BRONZE', 'TIME', 1),  -- Christoph Hafer      🥉 (GER)

-- ── Sport 14: Skeleton (TIME) — EDGE CASE: Silver missing ──────
(14, 50, 1, '3:59.02', 'GOLD',   'TIME', 1),  -- Christopher Grotheer 🥇 (GER)
(14, 51, 2, '3:59.45', NULL,     'TIME', 1),  -- Axel Jungk           (4th, kein Silber)
(14, 52, 3, '4:01.60', 'BRONZE', 'TIME', 1),  -- Tomass Dukurs        🥉 (LAT)

-- ── Sport 15: Luge (TIME) ────────────────────────────────────
(15, 53, 1, '3:34.56', 'GOLD',   'TIME', 1),  -- Johannes Ludwig      🥇 (GER)
(15, 54, 2, '3:34.89', 'SILVER', 'TIME', 1),  -- Wolfgang Kindl       🥈 (AUT)
(15, 55, 3, '3:35.12', 'BRONZE', 'TIME', 1);  -- Reinhard Egger       🥉 (AUT)

SET FOREIGN_KEY_CHECKS = 1;

