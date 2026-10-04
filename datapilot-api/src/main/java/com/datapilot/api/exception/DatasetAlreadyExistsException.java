package com.datapilot.api.exception;

public class DatasetAlreadyExistsException extends RuntimeException{

    public DatasetAlreadyExistsException(String message) {
        super(message);
    }
}
