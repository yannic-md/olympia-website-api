package de.olympia.main.importer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "import_errors", indexes = {
    @Index(name = "idx_import_errors_log", columnList = "import_log_id"),
    @Index(name = "idx_import_errors_code", columnList = "error_code")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "import_log_id", nullable = false)
    private Long importLogId;

    @Column(name = "`row_number`", nullable = false)
    private Integer rowNumber;

    @Column(nullable = false, length = 50)
    private String errorCode;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String errorMessage;

    @Column(length = 100)
    private String fieldName;

    @Column(length = 255)
    private String fieldValue;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

