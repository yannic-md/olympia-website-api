package de.olympia.main.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImportResponseDto {

    private Long importLogId;
    private String status;
    private String importType;
    private String filename;
    private Integer totalRecords;
    private Integer successfulRecords;
    private Integer failedRecords;
    private String message;
    private List<ImportErrorDto> errors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportErrorDto {
        private Integer rowNumber;
        private String errorCode;
        private String errorMessage;
        private String fieldName;
        private String fieldValue;
    }
}

