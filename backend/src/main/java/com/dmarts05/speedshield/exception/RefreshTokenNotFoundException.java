package com.dmarts05.speedshield.exception;

/**
 * Exception thrown when a refresh token is not found.
 */
public class RefreshTokenNotFoundException extends RuntimeException {

    /**
     * Constructs a new RefreshTokenNotFoundException with a default message.
     */
    public RefreshTokenNotFoundException() {
        super("Refresh token not found");
    }

    /**
     * Constructs a new RefreshTokenNotFoundException with a specified message.
     *
     * @param message The detail message.
     */
    public RefreshTokenNotFoundException(String message) {
        super(message);
    }
}
