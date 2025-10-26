package edu.curtin.app.exception;

/**
 * Custom exception thrown when a message cannot be parsed or is invalid.
 */
public class InvalidMessageException extends Exception {
    /**
     * Constructs a new InvalidMessageException with the specified detail message.
     * @param message the detail message explaining why the message is invalid
     */
    public InvalidMessageException(String message) {
        super(message);
    }
}