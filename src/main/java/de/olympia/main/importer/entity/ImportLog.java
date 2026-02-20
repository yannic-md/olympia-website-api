package de.olympia.main.importer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "import_logs", indexes = {
    @Index(name = "idx_import_status", columnList = "status"),
    @Index(name = "idx_import_imported_at", columnList = "imported_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String filename;

    @Column(nullable = false, length = 50)
    private String importType; // COUNTRIES, ATHLETES, RESULTS

    @Column(nullable = false)
    private Integer totalRecords = 0;

    @Column(nullable = false)
    private Integer successfulRecords = 0;

    @Column(nullable = false)
    private Integer failedRecords = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImportStatus status = ImportStatus.PENDING;

    @Column(name = "imported_by")
    private Long importedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime importedAt = LocalDateTime.now();

    @Column
    private LocalDateTime completedAt;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    public enum ImportStatus {
        PENDING, IN_PROGRESS, COMPLETED, FAILED
    }
}

