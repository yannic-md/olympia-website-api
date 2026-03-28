package de.olympia.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "import_details", indexes = {
    @Index(name = "idx_import_details_log", columnList = "import_log_id"),
    @Index(name = "idx_import_details_type", columnList = "entity_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "import_log_id", nullable = false)
    private Long importLogId;

    @Column(nullable = false, length = 50)
    private String entityType; // COUNTRY, ATHLETE, RESULT

    @Column(name = "entity_id")
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ImportAction action; // INSERT, UPDATE, SKIP

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum ImportAction {
        INSERT, UPDATE, SKIP
    }
}


