package de.olympia.main.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sports {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Raw/original sport name used as the stable key for CRUD operations. */
    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(name = "name_en", length = 150)
    private String nameEn;

    @Column(name = "name_de", length = 150)
    private String nameDe;

    @Column(name = "name_fr", length = 150)
    private String nameFr;

    /** Score format for this sport's single competition (PTS, WINS, TIME). */
    @Enumerated(EnumType.STRING)
    @Column(name = "score_type")
    private ScoreType scoreType;

    public enum ScoreType {
        PTS, WINS, TIME
    }
}

