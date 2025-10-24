package edu.curtin.app.communication;

public class MessageParser {
    public ParsedMessage parse(String rawMsg) {
        if (rawMsg == null || rawMsg.trim().isEmpty()) {
            return new ParsedMessage("Empty message");
        }

        String[] parts = rawMsg.trim().split("\\s+");
        if (parts.length < 2) {
            return new ParsedMessage("Invalid message format");
        }

        String probeName = parts[0];
        String commandType = parts[1].toLowerCase();

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
                return new ParsedMessage("Unknown commandType: " + commandType);
        }
    }

    private ParsedMessage parseMeasure(String probeName, String[] parts) {
        if (parts.length != 4) {
            return new ParsedMessage("Invalid measure format");
        }
        String measureType = parts[2];
        int duration = Integer.parseInt(parts[3]);

        return new ParsedMessage(new MeasureMsg(probeName, measureType, duration));
    }

    private ParsedMessage parseMove(String probeName, String[] parts) {
        if (parts.length != 4) {
            return new ParsedMessage("Invalid location format");
        }
        double lat = Double.parseDouble(parts[2]);
        double lon = Double.parseDouble(parts[3]);
        return new ParsedMessage(new MoveMsg(probeName, lat, lon));
    }
    private ParsedMessage parseStatus(String probeName, String[] parts) {
        if (parts.length != 2) {
            return new ParsedMessage("Invalid command");
        }
        return new ParsedMessage(new StatusMsg(probeName));
    }
    private ParsedMessage parseHistory(String probeName, String[] parts) {
        if (parts.length != 2) {
            return new ParsedMessage("Invalid command");
        }
        return new ParsedMessage(new HistoryMsg(probeName));
    }
    private ParsedMessage parseInitLocation(String probeName, String[] parts) {
        if (parts.length != 4) {
            return new ParsedMessage("Invalid location format");
        }
        double lat = Double.parseDouble(parts[2]);
        double lon = Double.parseDouble(parts[3]);
        return new ParsedMessage(new InitLocation(probeName, lat, lon));
    }
}
