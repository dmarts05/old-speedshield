package com.dmarts05.speedshield.exception;

/**
 * Exception thrown when attempting to use a username that is already taken.
 */
public class UsernameAlreadyTakenException extends RuntimeException {

    /**
     * Constructs a new UsernameAlreadyTakenException with a default message.
     */
    public UsernameAlreadyTakenException() {
        super("Username has already been taken");
    }

    /**
     * Constructs a new UsernameAlreadyTakenException with a specified message.
     *
     * @param message The detail message.
     */
    public UsernameAlreadyTakenException(String message) {
        super(message);
    }
}
