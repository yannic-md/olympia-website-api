INSERT INTO users (id, username, password_hash, role, email) VALUES
(1, 'admin', '$2a$12$PHxCrCaCIaKBv8uY8.H4M.OvA0wNY3jjMenKYmZIFrFBTNRZ.p4kO', 'ADMIN', 'admin@example.com'),
(2, 'judge1', '$2a$12$QwMbgi/l1GL0MCRVwXL/U.0/MfV41FUQesyslrzTXbg3shSBV.y7q', 'JUDGE', 'judge1@example.com'),
(3, 'judge2', '$2a$12$ZJPS84EyrxU6CNhWWNCok.zFhqU.CR62voW9G08uGkDmwWKDN/reC', 'JUDGE', 'judge2@example.com');

INSERT INTO countries (id, code, name) VALUES
(1, 'USA', 'United States'),
(2, 'GER', 'Germany'),
(3, 'FRA', 'France'),
(4, 'NOR', 'Norway'),
(5, 'SWE', 'Sweden'),
(6, 'CAN', 'Canada'),
(7, 'AUT', 'Austria'),
(8, 'SUI', 'Switzerland'),
(9, 'ITA', 'Italy'),
(10, 'JPN', 'Japan'),
(11, 'CHN', 'China'),
(12, 'KOR', 'South Korea'),
(13, 'RUS', 'Russia'),
(14, 'FIN', 'Finland'),
(15, 'NED', 'Netherlands'),
(16, 'CZE', 'Czech Republic'),
(17, 'POL', 'Poland'),
(18, 'GBR', 'Great Britain'),
(19, 'ESP', 'Spain'),
(20, 'AUS', 'Australia');

INSERT INTO sports (id, name) VALUES
(1, 'Alpine Skiing'),
(2, 'Biathlon'),
(3, 'Cross-Country Skiing'),
(4, 'Ski Jumping'),
(5, 'Nordic Combined'),
(6, 'Freestyle Skiing'),
(7, 'Snowboarding'),
(8, 'Figure Skating'),
(9, 'Speed Skating'),
(10, 'Short Track Speed Skating'),
(11, 'Ice Hockey'),
(12, 'Curling'),
(13, 'Bobsleigh'),
(14, 'Skeleton'),
(15, 'Luge');

INSERT INTO athletes (id, first_name, last_name, country_id) VALUES
-- Alpine Skiing
(1, 'Mikaela', 'Shiffrin', 1),
(2, 'Marco', 'Odermatt', 8),
(3, 'Petra', 'Vlhova', 16),
(4, 'Alexis', 'Pinturault', 3),
(5, 'Sofia', 'Goggia', 9),
-- Biathlon
(6, 'Johannes', 'Boe', 4),
(7, 'Marte', 'Roeiseland', 4),
(8, 'Dorothea', 'Wierer', 9),
(9, 'Martin', 'Fourcade', 3),
(10, 'Tiril', 'Eckhoff', 4),
-- Cross-Country Skiing
(11, 'Johannes', 'Klaebo', 4),
(12, 'Therese', 'Johaug', 4),
(13, 'Alexander', 'Bolshunov', 13),
(14, 'Ebba', 'Andersson', 5),
(15, 'Iivo', 'Niskanen', 14),
-- Ski Jumping
(16, 'Ryoyu', 'Kobayashi', 10),
(17, 'Karl', 'Geiger', 2),
(18, 'Halvor', 'Granerud', 4),
(19, 'Markus', 'Eisenbichler', 2),
(20, 'Marius', 'Lindvik', 4),
-- Freestyle Skiing
(21, 'Eileen', 'Gu', 11),
(22, 'Mikael', 'Kingsbury', 6),
(23, 'Perrine', 'Laffont', 3),
(24, 'Walter', 'Wallberg', 5),
-- Snowboarding
(25, 'Chloe', 'Kim', 1),
(26, 'Marcus', 'Kleveland', 4),
(27, 'Anna', 'Gasser', 7),
(28, 'Sebastien', 'Toutant', 6),
(29, 'Zoi', 'Sadowski-Synnott', 20),
-- Figure Skating
(30, 'Nathan', 'Chen', 1),
(31, 'Yuzuru', 'Hanyu', 10),
(32, 'Kamila', 'Valieva', 13),
(33, 'Kaori', 'Sakamoto', 10),
(34, 'Shoma', 'Uno', 10),
-- Speed Skating
(35, 'Ireen', 'Wust', 15),
(36, 'Kjeld', 'Nuis', 15),
(37, 'Nils', 'van der Poel', 5),
(38, 'Miho', 'Takagi', 10),
(39, 'Thomas', 'Krol', 15),
-- Short Track
(40, 'Suzanne', 'Schulting', 15),
(41, 'Hwang', 'Daeheon', 12),
(42, 'Arianna', 'Fontana', 9),
(43, 'Steven', 'Dubois', 6),
-- Ice Hockey
(44, 'Connor', 'McDavid', 6),
(45, 'Auston', 'Matthews', 1),
(46, 'Marie-Philip', 'Poulin', 6),
(47, 'Hilary', 'Knight', 1),
(48, 'Leon', 'Draisaitl', 2),
-- Curling
(49, 'Niklas', 'Edin', 5),
(50, 'Anna', 'Hasselborg', 5),
(51, 'Brad', 'Gushue', 6),
(52, 'Jennifer', 'Jones', 6),
-- Bobsleigh
(53, 'Francesco', 'Friedrich', 2),
(54, 'Johannes', 'Lochner', 2),
(55, 'Kaillie', 'Humphries', 1),
(56, 'Elana', 'Meyers Taylor', 1),
-- Skeleton
(57, 'Christopher', 'Grotheer', 2),
(58, 'Martins', 'Dukurs', 17),
(59, 'Hannah', 'Neise', 2),
(60, 'Kimberley', 'Bos', 15),
-- Luge
(61, 'Johannes', 'Ludwig', 2),
(62, 'Felix', 'Loch', 2),
(63, 'Natalie', 'Geisenberger', 2),
(64, 'Julia', 'Taubitz', 2);

INSERT INTO results (id, event_id, athlete_id, rank, time_or_points, medal, created_by) VALUES
-- Alpine Skiing - Downhill Women
(1, 1, 5, 1, '1:32.03', 'GOLD', 2),
(2, 1, 1, 2, '1:32.47', 'SILVER', 2),
(3, 1, 3, 3, '1:32.88', 'BRONZE', 2),
-- Alpine Skiing - Giant Slalom Men
(4, 1, 2, 1, '2:27.58', 'GOLD', 2),
(5, 1, 4, 2, '2:28.01', 'SILVER', 2),
-- Biathlon - 10km Sprint Men
(6, 2, 6, 1, '23:45.2', 'GOLD', 3),
(7, 2, 9, 2, '24:01.8', 'SILVER', 3),
(8, 2, 13, 3, '24:15.4', 'BRONZE', 3),
-- Biathlon - 7.5km Sprint Women
(9, 2, 7, 1, '20:23.1', 'GOLD', 3),
(10, 2, 10, 2, '20:35.7', 'SILVER', 3),
(11, 2, 8, 3, '20:48.3', 'BRONZE', 3),
-- Cross-Country Skiing - 15km Men
(12, 3, 11, 1, '33:28.5', 'GOLD', 2),
(13, 3, 13, 2, '33:42.1', 'SILVER', 2),
(14, 3, 15, 3, '34:01.2', 'BRONZE', 2),
-- Cross-Country Skiing - 10km Women
(15, 3, 12, 1, '24:54.2', 'GOLD', 2),
(16, 3, 14, 2, '25:12.8', 'SILVER', 2),
-- Ski Jumping - Large Hill Men
(17, 4, 16, 1, '145.5 pts', 'GOLD', 3),
(18, 4, 18, 2, '143.8 pts', 'SILVER', 3),
(19, 4, 17, 3, '142.1 pts', 'BRONZE', 3),
(20, 4, 19, 4, '140.5 pts', NULL, 3),
(21, 4, 20, 5, '138.9 pts', NULL, 3),
-- Freestyle Skiing - Halfpipe Women
(22, 6, 21, 1, '95.25 pts', 'GOLD', 2),
(23, 6, 23, 2, '92.50 pts', 'SILVER', 2),
-- Freestyle Skiing - Moguls Men
(24, 6, 22, 1, '88.45 pts', 'GOLD', 2),
(25, 6, 24, 2, '85.33 pts', 'SILVER', 2),
-- Snowboarding - Halfpipe Women
(26, 7, 25, 1, '94.00 pts', 'GOLD', 3),
(27, 7, 27, 2, '87.25 pts', 'SILVER', 3),
(28, 7, 29, 3, '84.51 pts', 'BRONZE', 3),
-- Snowboarding - Slopestyle Men
(29, 7, 26, 1, '91.75 pts', 'GOLD', 3),
(30, 7, 28, 2, '88.53 pts', 'SILVER', 3),
-- Figure Skating - Men Single
(31, 8, 30, 1, '332.60 pts', 'GOLD', 2),
(32, 8, 31, 2, '310.05 pts', 'SILVER', 2),
(33, 8, 34, 3, '293.00 pts', 'BRONZE', 2),
-- Figure Skating - Women Single
(34, 8, 32, 1, '272.71 pts', 'GOLD', 2),
(35, 8, 33, 2, '255.95 pts', 'SILVER', 2),
-- Speed Skating - 5000m Men
(36, 9, 37, 1, '6:08.84', 'GOLD', 3),
(37, 9, 36, 2, '6:09.31', 'SILVER', 3),
(38, 9, 39, 3, '6:10.53', 'BRONZE', 3),
-- Speed Skating - 3000m Women
(39, 9, 35, 1, '3:56.93', 'GOLD', 3),
(40, 9, 38, 2, '3:58.09', 'SILVER', 3),
-- Short Track - 1500m Men
(41, 10, 41, 1, '2:09.25', 'GOLD', 2),
(42, 10, 43, 2, '2:09.48', 'SILVER', 2),
-- Short Track - 1000m Women
(43, 10, 40, 1, '1:28.39', 'GOLD', 2),
(44, 10, 42, 2, '1:28.66', 'SILVER', 2),
-- Ice Hockey - Tournament Results
(45, 11, 44, 1, '12 pts', 'GOLD', 3),
(46, 11, 45, 1, '12 pts', 'GOLD', 3),
(47, 11, 46, 1, '15 pts', 'GOLD', 3),
(48, 11, 47, 2, '10 pts', 'SILVER', 3),
-- Curling - Tournament
(49, 12, 49, 1, '9 wins', 'GOLD', 2),
(50, 12, 50, 1, '8 wins', 'GOLD', 2),
(51, 12, 51, 2, '7 wins', 'SILVER', 2),
(52, 12, 52, 2, '7 wins', 'SILVER', 2),
-- Bobsleigh - Two-Man
(53, 13, 53, 1, '3:56.89', 'GOLD', 3),
(54, 13, 54, 2, '3:57.38', 'SILVER', 3),
-- Bobsleigh - Two-Woman
(55, 13, 55, 1, '4:05.84', 'GOLD', 3),
(56, 13, 56, 2, '4:06.72', 'SILVER', 3),
-- Skeleton - Men
(57, 14, 57, 1, '3:59.02', 'GOLD', 2),
(58, 14, 58, 2, '3:59.45', 'SILVER', 2),
-- Skeleton - Women
(59, 14, 59, 1, '4:07.62', 'GOLD', 2),
(60, 14, 60, 2, '4:08.13', 'SILVER', 2),
(61, 14, 60, 3, '4:08.89', 'BRONZE', 2),
-- Luge - Men Single
(62, 15, 61, 1, '3:34.56', 'GOLD', 3),
(63, 15, 62, 2, '3:34.89', 'SILVER', 3),
-- Luge - Women Single
(64, 15, 63, 1, '3:38.45', 'GOLD', 3),
(65, 15, 64, 2, '3:38.78', 'SILVER', 3),
(66, 15, 64, 3, '3:39.21', 'BRONZE', 3),
-- Additional participants without medals
(67, 1, 4, 5, '1:33.92', NULL, 2),
(68, 3, 14, 4, '25:45.3', NULL, 2),
(69, 6, 24, 3, '82.10 pts', 'BRONZE', 2),
(70, 7, 26, 4, '79.33 pts', NULL, 3),
(71, 9, 35, 4, '6:12.45', NULL, 3),
(72, 10, 40, 3, '1:28.95', 'BRONZE', 2),
(73, 13, 56, 3, '4:07.25', 'BRONZE', 3),
(74, 5, 20, 1, '24:45.8', 'GOLD', 3),
(75, 5, 18, 2, '25:01.2', 'SILVER', 3),
(76, 5, 17, 3, '25:18.7', 'BRONZE', 3);

INSERT INTO imports (id, filename, imported_by) VALUES
(1, 'athletes_import_2026.csv', 2),
(2, 'results_import_2026.csv', 3);

INSERT INTO login_logs (id, user_id, ip_address) VALUES
(1, 1, '192.0.2.1'),
(2, 2, '198.51.100.5'),
(3, 3, '203.0.113.10');


