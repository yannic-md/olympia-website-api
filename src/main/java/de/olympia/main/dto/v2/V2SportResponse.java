package de.olympia.main.dto.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class V2SportResponse {

    private Long id;

    /** Raw DB name — stable key for CRUD operations. */
    private String rawName;

    /** Translated display name in the requested language. */
    private String name;

    /** Score format for this sport (PTS, WINS, TIME). */
    private String scoreType;

    /** All athletes that participated in this sport, sorted by rank. */
    private List<ParticipantEntry> participants;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParticipantEntry {
        private Long athleteId;
        private String firstName;
        private String lastName;
        private Long countryId;
        private String countryCode;
        private String countryName;
        private String medal;
        private String result;
        private Integer rank;
        private Long resultId;
    }
}

