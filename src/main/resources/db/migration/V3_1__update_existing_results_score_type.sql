-- Update existing results with the appropriate score_type based on their time_or_points values

-- Alpine Skiing (event_id=1) -> TIME
UPDATE results SET score_type = 'TIME' WHERE event_id = 1;

-- Biathlon (event_id=2) -> TIME
UPDATE results SET score_type = 'TIME' WHERE event_id = 2;

-- Cross-Country Skiing (event_id=3) -> TIME
UPDATE results SET score_type = 'TIME' WHERE event_id = 3;

-- Ski Jumping (event_id=4) -> PTS
UPDATE results SET score_type = 'PTS' WHERE event_id = 4;

-- Freestyle Skiing (event_id=6) -> PTS
UPDATE results SET score_type = 'PTS' WHERE event_id = 6;

-- Snowboarding (event_id=7) -> PTS
UPDATE results SET score_type = 'PTS' WHERE event_id = 7;

-- Figure Skating (event_id=8) -> PTS
UPDATE results SET score_type = 'PTS' WHERE event_id = 8;

-- Speed Skating (event_id=9) -> TIME
UPDATE results SET score_type = 'TIME' WHERE event_id = 9;

-- Short Track Speed Skating (event_id=10) -> TIME
UPDATE results SET score_type = 'TIME' WHERE event_id = 10;

-- Ice Hockey (event_id=11) -> PTS
UPDATE results SET score_type = 'PTS' WHERE event_id = 11;

-- Curling (event_id=12) -> WINS
UPDATE results SET score_type = 'WINS' WHERE event_id = 12;

-- Bobsleigh (event_id=13) -> TIME
UPDATE results SET score_type = 'TIME' WHERE event_id = 13;

-- Skeleton (event_id=14) -> TIME
UPDATE results SET score_type = 'TIME' WHERE event_id = 14;

-- Luge (event_id=15) -> TIME
UPDATE results SET score_type = 'TIME' WHERE event_id = 15;

