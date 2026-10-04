package com.datapilot.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(DatasetAlreadyExistsException.class)
   public ResponseEntity<Map<String, Object>> handleDatasetAlreadyExists(
           DatasetAlreadyExistsException ex){
       Map<String, Object> body = Map.of(
               "timestamp", Instant.now(),
               "status", 409,
               "error", "Conflict",
               "message", ex.getMessage()
       );
       return ResponseEntity
               .status(HttpStatus.CONFLICT)
               .body(body);
   }
}
