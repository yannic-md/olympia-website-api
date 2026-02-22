-- Nordic Combined (event_id=5) was missing in V3_1 — set score_type to PTS
UPDATE results SET score_type = 'PTS' WHERE event_id = 5 AND score_type IS NULL;

-- Safety net: set TIME for any remaining results with no score_type
UPDATE results SET score_type = 'TIME' WHERE score_type IS NULL;

