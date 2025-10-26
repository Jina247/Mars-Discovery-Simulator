package edu.curtin.app.communication;
import edu.curtin.app.exception.InvalidMessageException;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Parser for converting raw string messages into structured Message objects.
 * Handles validation and error reporting for various message types including
 * initialisation, movement, measurement, status, and history commands.
 */
public class MessageParser {
    private static final Logger logger = Logger.getLogger(MessageParser.class.getName());

    /**
     * Parses a raw message string into a ParsedMessage containing either
     * a valid Message object or an error description.
     *
     * @param rawMsg the raw message string to parse
     * @return ParsedMessage containing either a valid Message or error information
     * @throws InvalidMessageException if rawMsg is null
     */
    public ParsedMessage parse(String rawMsg) throws InvalidMessageException {
        if (rawMsg == null) {
            logger.log(Level.SEVERE, "Null message received");
            throw new InvalidMessageException("Message cannot be null");
        }
        if (rawMsg.trim().isEmpty()) {
            logger.log(Level.WARNING, "Empty message received");
            return new ParsedMessage("Empty message");
        }
        // Split the message by space
        String[] parts = rawMsg.trim().split("\\s+");
        if (parts.length < 2) {
            logger.log(Level.WARNING, "Invalid message format: {0}", rawMsg);
            return new ParsedMessage("Invalid message format");
        }

        String probeName = parts[0];
        String commandType = parts[1].toLowerCase();

        // Return appropriate parser based on command type
        switch (commandType) {
            case "measure":
                return parseMeasure(probeName, parts);

            case "move":
                return parseMove(probeName, parts);

            case "status":
                return parseStatus(probeName, parts);

            case "history":
                return parseHistory(probeName, parts);

            case "at":
                return parseInitLocation(probeName, parts);

            default:
                logger.log(Level.WARNING, "Unknown command type: {0}", commandType);
                return new ParsedMessage("Unknown commandType: " + commandType);
        }
    }

    // Private methods to parse different types of messages
    private ParsedMessage parseMeasure(String probeName, String[] parts) {
        if (parts.length != 4) {
            logger.log(Level.WARNING, "Invalid measure format");
            return new ParsedMessage("Invalid measure format");
        }
        String measureType = parts[2];
        int duration = Integer.parseInt(parts[3]);

        return new ParsedMessage(new MeasureMsg(probeName, measureType, duration));
    }

    private ParsedMessage parseMove(String probeName, String[] parts) {
        if (parts.length != 4) {
            logger.log(Level.WARNING, "Invalid location format");
            return new ParsedMessage("Invalid location format");
        }
        double lat = Double.parseDouble(parts[2]);
        double lon = Double.parseDouble(parts[3]);
        return new ParsedMessage(new MoveMsg(probeName, lat, lon));
    }
    private ParsedMessage parseStatus(String probeName, String[] parts) {
        if (parts.length != 2) {
            logger.log(Level.WARNING, "Invalid status command");
            return new ParsedMessage("Invalid command");
        }
        return new ParsedMessage(new StatusMsg(probeName));
    }
    private ParsedMessage parseHistory(String probeName, String[] parts) {
        if (parts.length != 2) {
            logger.log(Level.WARNING, "Invalid history command");
            return new ParsedMessage("Invalid command");
        }
        return new ParsedMessage(new HistoryMsg(probeName));
    }
    private ParsedMessage parseInitLocation(String probeName, String[] parts) {
        if (parts.length != 4) {
            logger.log(Level.WARNING, "Invalid location format");
            return new ParsedMessage("Invalid location format");
        }
        double lat = Double.parseDouble(parts[2]);
        double lon = Double.parseDouble(parts[3]);
        return new ParsedMessage(new InitLocation(probeName, lat, lon));
    }
}
