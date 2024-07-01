package com.dmarts05.speedshield.exception;

/**
 * Exception thrown when a user is not found in the database.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new UsernameAlreadyTakenException with a default message.
     */
    public UserNotFoundException() {
        super("User not found");
    }

    /**
     * Constructs a new UsernameAlreadyTakenException with a specified message.
     *
     * @param message The detail message.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}
