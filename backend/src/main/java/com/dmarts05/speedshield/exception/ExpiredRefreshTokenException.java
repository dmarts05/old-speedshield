package com.dmarts05.speedshield.exception;

/**
 * Exception thrown when a refresh token has expired.
 */
public class ExpiredRefreshTokenException extends RuntimeException {

    /**
     * Constructs a new ExpiredRefreshTokenException with a default message.
     */
    public ExpiredRefreshTokenException() {
        super("Refresh token has expired");
    }

    /**
     * Constructs a new ExpiredRefreshTokenException with a specified message.
     *
     * @param message The detail message.
     */
    public ExpiredRefreshTokenException(String message) {
        super(message);
    }
}
