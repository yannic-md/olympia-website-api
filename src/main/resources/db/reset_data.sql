-- Reset script: Deletes all data except admin user
-- This script is executed manually via the /api/admin/reset endpoint

-- Clear all data tables in correct order (respecting foreign keys)
DELETE FROM login_logs;
DELETE FROM imports;
DELETE FROM results;
DELETE FROM athletes;
DELETE FROM sports;
DELETE FROM countries;

-- Clear users except admin (id = 1)
DELETE FROM users WHERE id != 1;


-- Reset auto increment values to start fresh
ALTER TABLE login_logs AUTO_INCREMENT = 1;
ALTER TABLE imports AUTO_INCREMENT = 1;
ALTER TABLE results AUTO_INCREMENT = 1;
ALTER TABLE athletes AUTO_INCREMENT = 1;
ALTER TABLE sports AUTO_INCREMENT = 1;
ALTER TABLE countries AUTO_INCREMENT = 1;
ALTER TABLE users AUTO_INCREMENT = 2;

