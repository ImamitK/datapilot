package com.datapilot.api.exception;

import com.datapilot.api.dto.ErrorResponse;
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

   @ExceptionHandler(DatasetNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDatasetNotFound(DatasetNotFoundException ex){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "timestamp", Instant.now(),
                        "status", 404,
                        "error",
                        "Conflict",
                        "message", ex.getMessage()
                ));
   }

    @ExceptionHandler(DatasetVersionNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDatasetVersionNotFound(
            DatasetVersionNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "timestamp", Instant.now(),
                        "status", 404,
                        "error", "Not Found",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception) {

        ErrorResponse error = new ErrorResponse();
        error.setMessage(exception.getMessage());

        return ResponseEntity.badRequest().body(error);
    }
}
