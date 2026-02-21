-- Add more diverse results for existing athletes to create varied medal profiles
-- This creates scenarios: many medals, few medals, single medal type, no medals, etc.

-- Mikaela Shiffrin (athlete_id=1) - CHAMPION with many different medals (multiple Gold, Silver, Bronze)
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(1, 1, 1, '1:31.45', 'GOLD', 2),
(1, 1, 2, '1:32.89', 'SILVER', 2),
(1, 1, 1, '2:15.34', 'GOLD', 2),
(1, 1, 3, '2:18.67', 'BRONZE', 2),
(1, 1, 1, '1:55.23', 'GOLD', 2),
(1, 1, 2, '1:56.12', 'SILVER', 2);

-- Marco Odermatt (athlete_id=2) - Many medals, mostly Gold
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(2, 1, 1, '2:26.78', 'GOLD', 2),
(2, 1, 1, '1:42.15', 'GOLD', 2),
(2, 1, 1, '2:25.45', 'GOLD', 2),
(2, 1, 2, '2:28.34', 'SILVER', 2),
(2, 1, 1, '1:41.89', 'GOLD', 2);

-- Petra Vlhova (athlete_id=3) - Mix of Silver and Bronze, no Gold
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(3, 1, 2, '2:16.45', 'SILVER', 2),
(3, 1, 3, '1:33.12', 'BRONZE', 2),
(3, 1, 2, '1:56.78', 'SILVER', 2),
(3, 1, 3, '2:19.23', 'BRONZE', 2);

-- Alexis Pinturault (athlete_id=4) - Few medals (1 Gold, 1 Bronze)
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(4, 1, 1, '2:27.12', 'GOLD', 2),
(4, 1, 3, '2:29.45', 'BRONZE', 2),
(4, 1, 4, '2:30.67', NULL, 2),
(4, 1, 6, '2:32.89', NULL, 2);

-- Sofia Goggia (athlete_id=5) - Only Gold medals
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(5, 1, 1, '1:31.78', 'GOLD', 2),
(5, 1, 1, '1:30.95', 'GOLD', 2),
(5, 1, 1, '1:32.34', 'GOLD', 2);

-- Johannes Boe (athlete_id=6) - Multiple Golds and one Silver
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(6, 2, 1, '23:35.8', 'GOLD', 3),
(6, 2, 1, '48:12.4', 'GOLD', 3),
(6, 2, 2, '24:08.3', 'SILVER', 3),
(6, 2, 1, '1:14:23.5', 'GOLD', 3);

-- Marte Roeiseland (athlete_id=7) - Complete set: Gold, Silver, Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(7, 2, 1, '20:18.9', 'GOLD', 3),
(7, 2, 2, '20:42.3', 'SILVER', 3),
(7, 2, 3, '21:05.7', 'BRONZE', 3),
(7, 2, 1, '54:23.1', 'GOLD', 3);

-- Dorothea Wierer (athlete_id=8) - Only Bronze medals
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(8, 2, 3, '20:55.6', 'BRONZE', 3),
(8, 2, 3, '21:12.8', 'BRONZE', 3),
(8, 2, 3, '55:34.2', 'BRONZE', 3);

-- Martin Fourcade (athlete_id=9) - One Silver medal only
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(9, 2, 2, '24:15.7', 'SILVER', 3),
(9, 2, 4, '24:45.3', NULL, 3),
(9, 2, 5, '49:23.8', NULL, 3);

-- Tiril Eckhoff (athlete_id=10) - No medals, multiple participations
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(10, 2, 4, '21:15.4', NULL, 3),
(10, 2, 5, '21:34.9', NULL, 3),
(10, 2, 6, '55:45.2', NULL, 3),
(10, 2, 7, '20:56.7', NULL, 3),
(10, 2, 8, '21:23.1', NULL, 3);

-- Johannes Klaebo (athlete_id=11) - Many Golds and Silvers
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(11, 3, 1, '33:15.8', 'GOLD', 2),
(11, 3, 2, '3:42.5', 'SILVER', 2),
(11, 3, 1, '1:22:45.3', 'GOLD', 2),
(11, 3, 1, '14:28.7', 'GOLD', 2),
(11, 3, 2, '34:12.4', 'SILVER', 2);

-- Therese Johaug (athlete_id=12) - All Gold
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(12, 3, 1, '24:38.4', 'GOLD', 2),
(12, 3, 1, '41:05.3', 'GOLD', 2),
(12, 3, 1, '1:08:34.8', 'GOLD', 2),
(12, 3, 1, '24:45.2', 'GOLD', 2);

-- Alexander Bolshunov (athlete_id=13) - Mix of all medals
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(13, 3, 2, '33:55.9', 'SILVER', 2),
(13, 3, 1, '1:23:12.5', 'GOLD', 2),
(13, 3, 3, '34:23.7', 'BRONZE', 2),
(13, 3, 1, '14:35.2', 'GOLD', 2),
(13, 3, 2, '41:34.6', 'SILVER', 2);

-- Ebba Andersson (athlete_id=14) - Only Silver
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(14, 3, 2, '25:23.5', 'SILVER', 2),
(14, 3, 2, '41:28.9', 'SILVER', 2),
(14, 3, 2, '1:09:12.3', 'SILVER', 2);

-- Iivo Niskanen (athlete_id=15) - Two Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(15, 3, 3, '34:15.8', 'BRONZE', 2),
(15, 3, 3, '1:23:45.7', 'BRONZE', 2),
(15, 3, 5, '14:52.3', NULL, 2);

-- Ryoyu Kobayashi (athlete_id=16) - Multiple Golds
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(16, 4, 1, '146.8 pts', 'GOLD', 3),
(16, 4, 1, '289.5 pts', 'GOLD', 3),
(16, 4, 1, '144.2 pts', 'GOLD', 3);

-- Karl Geiger (athlete_id=17) - Bronze and Silver
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(17, 4, 3, '141.7 pts', 'BRONZE', 3),
(17, 4, 2, '285.3 pts', 'SILVER', 3),
(17, 4, 3, '139.8 pts', 'BRONZE', 3);

-- Halvor Granerud (athlete_id=18) - One Gold
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(18, 4, 1, '287.2 pts', 'GOLD', 3),
(18, 4, 4, '138.5 pts', NULL, 3),
(18, 4, 5, '137.2 pts', NULL, 3);

-- Markus Eisenbichler (athlete_id=19) - No medals
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(19, 4, 5, '139.2 pts', NULL, 3),
(19, 4, 6, '275.8 pts', NULL, 3),
(19, 4, 7, '136.4 pts', NULL, 3),
(19, 4, 8, '135.1 pts', NULL, 3);

-- Marius Lindvik (athlete_id=20) - One Silver, one Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(20, 4, 2, '143.6 pts', 'SILVER', 3),
(20, 4, 3, '280.4 pts', 'BRONZE', 3);

-- Eileen Gu (athlete_id=21) - Multiple Golds and one Silver
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(21, 6, 1, '96.50 pts', 'GOLD', 2),
(21, 6, 1, '188.25 pts', 'GOLD', 2),
(21, 6, 2, '86.23 pts', 'SILVER', 2);

-- Mikael Kingsbury (athlete_id=22) - All Gold
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(22, 6, 1, '89.12 pts', 'GOLD', 2),
(22, 6, 1, '87.65 pts', 'GOLD', 2),
(22, 6, 1, '88.98 pts', 'GOLD', 2);

-- Perrine Laffont (athlete_id=23) - One Silver, two Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(23, 6, 2, '93.15 pts', 'SILVER', 2),
(23, 6, 3, '84.67 pts', 'BRONZE', 2),
(23, 6, 3, '85.12 pts', 'BRONZE', 2);

-- Walter Wallberg (athlete_id=24) - One Bronze only
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(24, 6, 3, '83.45 pts', 'BRONZE', 2),
(24, 6, 4, '82.78 pts', NULL, 2);

-- Chloe Kim (athlete_id=25) - All Gold
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(25, 7, 1, '95.25 pts', 'GOLD', 3),
(25, 7, 1, '93.75 pts', 'GOLD', 3);

-- Marcus Kleveland (athlete_id=26) - Mix of medals
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(26, 7, 2, '89.50 pts', 'SILVER', 3),
(26, 7, 3, '85.25 pts', 'BRONZE', 3),
(26, 7, 1, '92.10 pts', 'GOLD', 3);

-- Anna Gasser (athlete_id=27) - One Silver, one Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(27, 7, 2, '88.75 pts', 'SILVER', 3),
(27, 7, 3, '82.40 pts', 'BRONZE', 3);

-- Sebastien Toutant (athlete_id=28) - No medals, multiple attempts
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(28, 7, 4, '81.25 pts', NULL, 3),
(28, 7, 5, '79.50 pts', NULL, 3),
(28, 7, 6, '77.85 pts', NULL, 3);

-- Zoi Sadowski-Synnott (athlete_id=29) - One Gold, one Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(29, 7, 1, '92.88 pts', 'GOLD', 3),
(29, 7, 3, '83.76 pts', 'BRONZE', 3);

-- Nathan Chen (athlete_id=30) - Multiple Golds
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(30, 8, 1, '335.30 pts', 'GOLD', 2),
(30, 8, 1, '224.92 pts', 'GOLD', 2);

-- Yuzuru Hanyu (athlete_id=31) - Gold, Silver, Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(31, 8, 1, '317.85 pts', 'GOLD', 2),
(31, 8, 2, '311.84 pts', 'SILVER', 2),
(31, 8, 3, '291.43 pts', 'BRONZE', 2);

-- Kamila Valieva (athlete_id=32) - One Silver
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(32, 8, 2, '268.35 pts', 'SILVER', 2),
(32, 8, 4, '245.12 pts', NULL, 2);

-- Kaori Sakamoto (athlete_id=33) - One Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(33, 8, 3, '253.82 pts', 'BRONZE', 2),
(33, 8, 4, '238.56 pts', NULL, 2);

-- Shoma Uno (athlete_id=34) - No medals
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(34, 8, 4, '289.34 pts', NULL, 2),
(34, 8, 5, '277.18 pts', NULL, 2),
(34, 8, 6, '265.45 pts', NULL, 2);

-- Ireen Wust (athlete_id=35) - Many Golds and Silvers
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(35, 9, 1, '3:55.32', 'GOLD', 3),
(35, 9, 2, '1:54.67', 'SILVER', 3),
(35, 9, 1, '7:56.12', 'GOLD', 3),
(35, 9, 1, '3:56.45', 'GOLD', 3);

-- Kjeld Nuis (athlete_id=36) - Multiple Golds
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(36, 9, 1, '1:43.21', 'GOLD', 3),
(36, 9, 1, '6:08.95', 'GOLD', 3),
(36, 9, 2, '12:45.32', 'SILVER', 3);

-- Nils van der Poel (athlete_id=37) - All Gold
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(37, 9, 1, '6:07.56', 'GOLD', 3),
(37, 9, 1, '12:30.74', 'GOLD', 3);

-- Miho Takagi (athlete_id=38) - Silver and Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(38, 9, 2, '3:57.89', 'SILVER', 3),
(38, 9, 3, '1:55.23', 'BRONZE', 3),
(38, 9, 2, '8:02.45', 'SILVER', 3);

-- Thomas Krol (athlete_id=39) - One Gold, one Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(39, 9, 1, '1:43.87', 'GOLD', 3),
(39, 9, 3, '6:11.76', 'BRONZE', 3);

-- Suzanne Schulting (athlete_id=40) - Multiple Golds
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(40, 10, 1, '1:28.12', 'GOLD', 2),
(40, 10, 1, '42.56', 'GOLD', 2),
(40, 10, 1, '2:17.35', 'GOLD', 2);

-- Hwang Daeheon (athlete_id=41) - Gold and Silver
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(41, 10, 1, '40.92', 'GOLD', 2),
(41, 10, 2, '2:09.78', 'SILVER', 2),
(41, 10, 1, '1:20.45', 'GOLD', 2);

-- Arianna Fontana (athlete_id=42) - All types of medals
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(42, 10, 1, '42.89', 'GOLD', 2),
(42, 10, 2, '1:28.92', 'SILVER', 2),
(42, 10, 3, '2:18.67', 'BRONZE', 2),
(42, 10, 3, '43.21', 'BRONZE', 2);

-- Steven Dubois (athlete_id=43) - One Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(43, 10, 3, '1:21.34', 'BRONZE', 2),
(43, 10, 4, '41.56', NULL, 2);

-- Connor McDavid (athlete_id=44) - No individual medals (team sport)
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(44, 11, 2, '10 pts', 'SILVER', 3),
(44, 11, 4, '8 pts', NULL, 3);

-- Auston Matthews (athlete_id=45) - One Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(45, 11, 3, '9 pts', 'BRONZE', 3);

-- Marie-Philip Poulin (athlete_id=46) - Multiple Golds
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(46, 11, 1, '16 pts', 'GOLD', 3),
(46, 11, 1, '18 pts', 'GOLD', 3);

-- Hilary Knight (athlete_id=47) - Silver and Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(47, 11, 2, '12 pts', 'SILVER', 3),
(47, 11, 3, '11 pts', 'BRONZE', 3);

-- Leon Draisaitl (athlete_id=48) - No medals
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(48, 11, 4, '7 pts', NULL, 3),
(48, 11, 5, '6 pts', NULL, 3);

-- Niklas Edin (athlete_id=49) - All Gold
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(49, 12, 1, '10 wins', 'GOLD', 2),
(49, 12, 1, '9 wins', 'GOLD', 2);

-- Anna Hasselborg (athlete_id=50) - Gold and Silver
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(50, 12, 1, '9 wins', 'GOLD', 2),
(50, 12, 2, '8 wins', 'SILVER', 2);

-- Brad Gushue (athlete_id=51) - One Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(51, 12, 3, '7 wins', 'BRONZE', 2);

-- Jennifer Jones (athlete_id=52) - No medals
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(52, 12, 4, '6 wins', NULL, 2),
(52, 12, 5, '5 wins', NULL, 2);

-- Francesco Friedrich (athlete_id=53) - Multiple Golds
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(53, 13, 1, '3:55.78', 'GOLD', 3),
(53, 13, 1, '3:37.23', 'GOLD', 3),
(53, 13, 1, '3:56.12', 'GOLD', 3);

-- Johannes Lochner (athlete_id=54) - Silver and Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(54, 13, 2, '3:57.89', 'SILVER', 3),
(54, 13, 3, '3:38.45', 'BRONZE', 3);

-- Kaillie Humphries (athlete_id=55) - Two Golds
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(55, 13, 1, '4:04.98', 'GOLD', 3),
(55, 13, 1, '4:05.23', 'GOLD', 3);

-- Elana Meyers Taylor (athlete_id=56) - Silver and Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(56, 13, 2, '4:07.12', 'SILVER', 3),
(56, 13, 3, '4:07.89', 'BRONZE', 3);

-- Christopher Grotheer (athlete_id=57) - Two Golds
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(57, 14, 1, '3:58.45', 'GOLD', 2),
(57, 14, 1, '3:58.87', 'GOLD', 2);

-- Martins Dukurs (athlete_id=58) - Only Silver
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(58, 14, 2, '3:59.78', 'SILVER', 2),
(58, 14, 2, '4:00.12', 'SILVER', 2),
(58, 14, 2, '3:59.89', 'SILVER', 2);

-- Hannah Neise (athlete_id=59) - One Gold, one Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(59, 14, 1, '4:07.12', 'GOLD', 2),
(59, 14, 3, '4:08.34', 'BRONZE', 2);

-- Kimberley Bos (athlete_id=60) - No medals this time
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(60, 14, 4, '4:09.45', NULL, 2),
(60, 14, 5, '4:09.78', NULL, 2);

-- Johannes Ludwig (athlete_id=61) - Multiple Golds
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(61, 15, 1, '3:34.12', 'GOLD', 3),
(61, 15, 1, '3:34.34', 'GOLD', 3);

-- Felix Loch (athlete_id=62) - Gold, Silver, Bronze
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(62, 15, 1, '3:34.45', 'GOLD', 3),
(62, 15, 2, '3:35.12', 'SILVER', 3),
(62, 15, 3, '3:35.67', 'BRONZE', 3);

-- Natalie Geisenberger (athlete_id=63) - All Gold
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(63, 15, 1, '3:38.12', 'GOLD', 3),
(63, 15, 1, '3:37.89', 'GOLD', 3),
(63, 15, 1, '3:38.23', 'GOLD', 3);

-- Julia Taubitz (athlete_id=64) - One Silver, multiple participations
INSERT IGNORE INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(64, 15, 2, '3:39.01', 'SILVER', 3),
(64, 15, 4, '3:39.89', NULL, 3),
(64, 15, 5, '3:40.12', NULL, 3);

