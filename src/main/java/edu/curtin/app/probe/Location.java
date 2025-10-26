package edu.curtin.app.probe;

public class Location {
    private final double latitude;
    private final double longitude;

    // Constructor
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /**
     * Calculates the distance between current and destination
     * @param dest destination
     */
    public double distanceTo(Location dest) {
        double latDiff = dest.latitude - this.latitude;
        double longDiff = dest.longitude - this.longitude;
        return Math.sqrt(latDiff * latDiff + longDiff * longDiff);
    }

    /**
     * Formated string representing the location
     */
    @Override
    public String toString() {
        return String.format("%.6f %.6f", latitude, longitude);
    }
}
