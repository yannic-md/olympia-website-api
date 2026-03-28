-- Add foreign key constraint for results.event_id -> sports.id
ALTER TABLE results
ADD CONSTRAINT fk_results_sport
FOREIGN KEY (event_id) REFERENCES sports(id) ON DELETE CASCADE;



