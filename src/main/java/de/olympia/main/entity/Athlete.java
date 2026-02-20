package de.olympia.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "athletes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Athlete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String firstName;

    @Column(nullable = false, length = 150)
    private String lastName;

    @Column(name = "country_id")
    private Long countryId;

    @Column(length = 1)
    private String gender; // M, F, D

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}

