-- Fix inconsistent time formats in results: times under 1 minute should use 0:SS.XX format
-- Affects Short Track Speed Skating (event_id=10) entries from V1_3

-- Suzanne Schulting - 500m Short Track
UPDATE results SET time_or_points = '0:42.56' WHERE athlete_id = 40 AND time_or_points = '42.56';

-- Hwang Daeheon - 500m Short Track
UPDATE results SET time_or_points = '0:40.92' WHERE athlete_id = 41 AND time_or_points = '40.92';

-- Arianna Fontana - 500m Short Track
UPDATE results SET time_or_points = '0:42.89' WHERE athlete_id = 42 AND time_or_points = '42.89';
UPDATE results SET time_or_points = '0:43.21' WHERE athlete_id = 42 AND time_or_points = '43.21';

-- Steven Dubois - 500m Short Track
UPDATE results SET time_or_points = '0:41.56' WHERE athlete_id = 43 AND time_or_points = '41.56';

