ALTER TABLE results
    ADD COLUMN score_type ENUM('PTS','WINS','TIME') NULL AFTER time_or_points;

