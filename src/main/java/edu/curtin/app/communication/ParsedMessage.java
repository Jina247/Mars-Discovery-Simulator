package edu.curtin.app.communication;

public class ParsedMessage {
    private final boolean valid;
    private final Message message;
    private final String error;

    // Invalid message
    public ParsedMessage(String error) {
        this.valid = false;
        this.message = null;
        this.error = error;
    }

    // Valid message
    public ParsedMessage(Message message) {
        this.valid = true;
        this.message = message;
        this.error = null;
    }

    public boolean isValid() {
        return valid;
    }

    public Message getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }
}
