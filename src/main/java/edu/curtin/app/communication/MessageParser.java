package edu.curtin.app.communication;

import edu.curtin.app.probe.Location;

public class MessageParser {
    public ParsedMessage parser(String message) {
        if (message.trim().isEmpty() || message == null) {
            return new ParsedMessage(message);
        }
        
        String[] parts = message.trim().split("\\s+");
        if (parts.length == 4 && parts[1].equals("move")) {
            try {
                String probeName = parts[0];
                double lat = Double.parseDouble(parts[2]);
                double lon = Double.parseDouble(parts[3]);
                return new ParsedMessage(new MoveMsg(probeName, lat, lon));
            } catch (NumberFormatException e) {
                return new ParsedMessage("Invalid location");
            }
        } else if (parts.length == 4 && parts[1].equals("measure")) {
            try {
                String probeName = parts[0];
                String measurementTypes = parts[2];
                int duration = Integer.parseInt(parts[3]);

                return new ParsedMessage(new MeasureMsg())
            }
        }
        return null;

        
    }
}
