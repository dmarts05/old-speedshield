package com.dmarts05.speedshield.exception;

/**
 * Exception thrown when a JWT token is not found in the request.
 */
public class JwtNotFoundException extends Exception {

    /**
     * Constructs a new JwtNotFoundException with a default message.
     */
    public JwtNotFoundException() {
        super("JWT token not found");
    }

    /**
     * Constructs a new JwtNotFoundException with a specified message.
     *
     * @param message The detail message.
     */
    public JwtNotFoundException(String message) {
        super(message);
    }
}
