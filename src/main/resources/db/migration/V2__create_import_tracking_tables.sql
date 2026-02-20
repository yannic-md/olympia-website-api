-- Create import_logs table to track file imports
CREATE TABLE IF NOT EXISTS import_logs (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    import_type VARCHAR(50) NOT NULL,
    total_records INT NOT NULL DEFAULT 0,
    successful_records INT NOT NULL DEFAULT 0,
    failed_records INT NOT NULL DEFAULT 0,
    status ENUM('PENDING','IN_PROGRESS','COMPLETED','FAILED') NOT NULL DEFAULT 'PENDING',
    imported_by BIGINT NULL,
    imported_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    error_message TEXT NULL,
    CONSTRAINT fk_import_logs_user FOREIGN KEY (imported_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_import_status (status),
    INDEX idx_import_imported_at (imported_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create import_errors table to track specific record-level errors
CREATE TABLE IF NOT EXISTS import_errors (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    import_log_id BIGINT NOT NULL,
    row_number INT NOT NULL,
    error_code VARCHAR(50) NOT NULL,
    error_message TEXT NOT NULL,
    field_name VARCHAR(100) NULL,
    field_value VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_import_errors_log FOREIGN KEY (import_log_id) REFERENCES import_logs(id) ON DELETE CASCADE,
    INDEX idx_import_errors_log (import_log_id),
    INDEX idx_import_errors_code (error_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create import_details table to store details about what was imported
CREATE TABLE IF NOT EXISTS import_details (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    import_log_id BIGINT NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NULL,
    action ENUM('INSERT','UPDATE','SKIP') NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_import_details_log FOREIGN KEY (import_log_id) REFERENCES import_logs(id) ON DELETE CASCADE,
    INDEX idx_import_details_log (import_log_id),
    INDEX idx_import_details_type (entity_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

