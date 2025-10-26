package edu.curtin.app.state;

import edu.curtin.app.probe.Location;
import edu.curtin.app.probe.Probe;

public class MovingState implements ProbeState {
    private final Location desLocation;

    public MovingState(Location desLocation) {
        this.desLocation = desLocation;
    }

    @Override
    public String getState() {
        return "DRIVING";
    }

    @Override
    public void handleSol(Probe probe, int sol) {
        Location current = probe.getCurrentLocation();
        double distance = current.distanceTo(desLocation);
        if (distance <= probe.getMaxDistance()) {
            String moveCommand = toStringFormat(current, desLocation);
            System.out.println("TO " + probe.getName().toUpperCase() + ": MOVE BY " + moveCommand);
            probe.record(sol, moveCommand);
            probe.setCurrentLocation(desLocation);
            probe.setState(new LowPowerMode());
        } else {
            Location next = calcDistance(current, desLocation, probe.getMaxDistance());
            String moveCommand = toStringFormat(current, next);
            System.out.println("TO " + probe.getName().toUpperCase() + ": MOVE BY " + moveCommand);
            probe.record(sol, moveCommand);
            probe.setCurrentLocation(next);
        }
    }


    public Location calcDistance(Location before, Location after, double maxDistance) {
        double latDiff = after.getLatitude() - before.getLatitude();
        double longDiff = after.getLongitude() - before.getLongitude();
        double totalDist = Math.sqrt(latDiff * latDiff + longDiff * longDiff);
        if (totalDist <= maxDistance) {
            return after;
        }
        double ratio = maxDistance / totalDist;
        double newLat = before.getLatitude() + (latDiff * ratio);
        double newLong = before.getLongitude() + (longDiff * ratio);
        return new Location(newLat, newLong);
    }

    // Helper method to construct a string representing the location
    private String toStringFormat(Location before, Location after) {
        double latDiff = after.getLatitude() - before.getLatitude();
        double longDiff = after.getLongitude() - before.getLongitude();
        String latSign = (latDiff >= 0) ? "+" : "";
        String longSign = (longDiff >= 0) ? "+" : "";

        return String.format("%s%.6f %s%.6f", latSign, latDiff, longSign, longDiff);
    }

    @Override
    public void updateMeasurementType(String type, int duration) {
        // Only for measuring state
    }
}
