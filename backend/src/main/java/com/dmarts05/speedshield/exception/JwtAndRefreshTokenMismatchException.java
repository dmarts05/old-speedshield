package com.dmarts05.speedshield.exception;

/**
 * Exception thrown when a JWT and refresh token do not match.
 */
public class JwtAndRefreshTokenMismatchException extends RuntimeException {

    /**
     * Constructs a new JwtAndRefreshTokenMismatchException with a default message.
     */
    public JwtAndRefreshTokenMismatchException() {
        super("JWT and refresh token do not match");
    }

    /**
     * Constructs a new JwtAndRefreshTokenMismatchException with a specified message.
     *
     * @param message The detail message.
     */
    public JwtAndRefreshTokenMismatchException(String message) {
        super(message);
    }
}
