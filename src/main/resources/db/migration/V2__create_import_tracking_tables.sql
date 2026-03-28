-- Create import tracking tables for the new import system
-- These tables track import operations, including status, statistics, and detailed error/success records

-- Main import log table tracking each import operation
CREATE TABLE IF NOT EXISTS import_logs (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    filename VARCHAR(255) NOT NULL,
    import_type VARCHAR(50) NOT NULL,
    total_records INT NOT NULL DEFAULT 0,
    successful_records INT NOT NULL DEFAULT 0,
    failed_records INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'IN_PROGRESS',
    error_message TEXT NULL,
    imported_by BIGINT NULL,
    imported_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    INDEX idx_import_status (status),
    INDEX idx_import_imported_at (imported_at),
    CONSTRAINT fk_import_logs_user FOREIGN KEY (imported_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Error tracking for failed records during import
CREATE TABLE IF NOT EXISTS import_errors (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    import_log_id BIGINT NOT NULL,
    `row_number` INT NOT NULL,
    error_code VARCHAR(50) NOT NULL,
    error_message TEXT NOT NULL,
    field_name VARCHAR(100) NULL,
    field_value VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_import_errors_log (import_log_id),
    INDEX idx_import_errors_code (error_code),
    CONSTRAINT fk_import_errors_log FOREIGN KEY (import_log_id) REFERENCES import_logs(id) ON DELETE CASCADE
);

-- Audit trail for successfully imported/updated entities
CREATE TABLE IF NOT EXISTS import_details (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    import_log_id BIGINT NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NULL,
    action VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_import_details_log (import_log_id),
    INDEX idx_import_details_type (entity_type),
    CONSTRAINT fk_import_details_log FOREIGN KEY (import_log_id) REFERENCES import_logs(id) ON DELETE CASCADE
);
