package com.dmarts05.speedshield.exception;

/**
 * Exception thrown when login credentials provided are invalid.
 */
public class InvalidLoginException extends RuntimeException {

    /**
     * Constructs a new InvalidLoginException with a default message.
     */
    public InvalidLoginException() {
        super("Username and password are not correct");
    }

    /**
     * Constructs a new InvalidLoginException with a specified message.
     *
     * @param message The detail message.
     */
    public InvalidLoginException(String message) {
        super(message);
    }
}
