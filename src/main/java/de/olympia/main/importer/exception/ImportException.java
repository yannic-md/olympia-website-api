package de.olympia.main.importer.exception;

/**
 * Base exception for import-related errors
 */
public class ImportException extends RuntimeException {

    private String errorCode;
    private Integer rowNumber;
    private String fieldName;

    public ImportException(String message) {
        super(message);
    }

    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImportException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ImportException(String message, String errorCode, Integer rowNumber, String fieldName) {
        super(message);
        this.errorCode = errorCode;
        this.rowNumber = rowNumber;
        this.fieldName = fieldName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public String getFieldName() {
        return fieldName;
    }
}

