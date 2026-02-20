package de.olympia.main.importer.exception;

/**
 * Exception thrown when imported data fails validation
 */
public class InvalidImportDataException extends ImportException {

    public InvalidImportDataException(String message) {
        super(message);
    }

    public InvalidImportDataException(String message, String errorCode) {
        super(message, errorCode);
    }

    public InvalidImportDataException(String message, String errorCode, Integer rowNumber, String fieldName) {
        super(message, errorCode, rowNumber, fieldName);
    }
}

