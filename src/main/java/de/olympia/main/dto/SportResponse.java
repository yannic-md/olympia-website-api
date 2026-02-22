package de.olympia.main.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SportResponse {
    /** Translated sport name for display */
    private String name;
    /** Original DB name — used as the key when submitting to the backend */
    private String rawName;
    /** Score type derived from the most recent result of this sport */
    private String scoreType;
}

