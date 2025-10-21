package edu.curtin.app.probe;

public class Location {
    private final double latitude;
    private final double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double distanceTo(Location other) {
        double latDiff = other.latitude - this.latitude;
        double longDiff = other.longitude - this.longitude;
        return Math.sqrt(latDiff * latDiff + longDiff * longDiff);
    }


    @Override
    public String toString() {
        return String.format("%.6f %.6f", latitude, longitude);
    }
}
