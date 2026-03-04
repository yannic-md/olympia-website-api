package de.olympia.main.entity;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "countries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 8)
    private String code;

    /** Raw/original country name (used as CRUD key and legacy fallback). */
    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "name_en", length = 150)
    private String nameEn;

    @Column(name = "name_de", length = 150)
    private String nameDe;

    @Column(name = "name_fr", length = 150)
    private String nameFr;
}

