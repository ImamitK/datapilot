package com.datapilot.api.exception;

public class DatasetNotFoundException extends RuntimeException{

    public DatasetNotFoundException(String message) {
        super(message);
    }
}
