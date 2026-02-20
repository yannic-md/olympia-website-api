package de.olympia.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "results")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "athlete_id", nullable = false)
    private Long athleteId;

    @Column
    private Integer rank;

    @Column(length = 100)
    private String timeOrPoints;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private Medal medal;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Medal {
        GOLD, SILVER, BRONZE
    }
}

