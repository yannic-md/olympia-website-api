-- Add more diverse results for existing athletes to create varied medal profiles
-- This creates scenarios: many medals, few medals, single medal type, no medals, etc.

-- Mikaela Shiffrin (athlete_id=1) - CHAMPION with many different medals (multiple Gold, Silver, Bronze)
INSERT INTO results (athlete_id, event_id, rank, time_or_points, medal, created_by) VALUES
(1, 1, 1, '1:31.45', 'GOLD', 2),
(1, 1, 2, '1:32.89', 'SILVER', 2),
(1, 1, 1, '2:15.34', 'GOLD', 2),
(1, 1, 3, '2:18.67', 'BRONZE', 2),
(1, 1, 1, '1:55.23', 'GOLD', 2),
(1, 1, 2, '1:56.12', 'SILVER', 2);
