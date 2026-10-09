package com.datapilot.api.exception;

public class DatasetVersionNotFoundException extends RuntimeException {

    public DatasetVersionNotFoundException(String message) {
        super(message);
    }
}
