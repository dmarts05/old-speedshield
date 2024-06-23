/**
 * GlobalExceptionHandler handles exceptions globally for the application.
 * It provides methods to handle specific exceptions and return appropriate
 * HTTP responses with error details.
 */
package com.dmarts05.speedshield.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller advice to handle exceptions globally across all controllers.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles MethodArgumentNotValidException thrown when @Validated fails.
     *
     * @param ex The exception instance.
     * @return ResponseEntity with error details and HTTP status BAD_REQUEST.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles InvalidLoginException thrown when login credentials are invalid.
     *
     * @param ex The exception instance.
     * @return ResponseEntity with error message and HTTP status BAD_REQUEST.
     */
    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<Map<String, String>> handleInvalidLoginException(InvalidLoginException ex) {
        Map<String, String> response = Map.of("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles UsernameAlreadyTakenException thrown when attempting to use an already taken username.
     *
     * @param ex The exception instance.
     * @return ResponseEntity with error message and HTTP status CONFLICT.
     */
    @ExceptionHandler(UsernameAlreadyTakenException.class)
    public ResponseEntity<Map<String, String>> handleUsernameAlreadyTakenException(UsernameAlreadyTakenException ex) {
        Map<String, String> response = Map.of("message", ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
