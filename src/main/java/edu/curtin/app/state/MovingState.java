package edu.curtin.app.state;

import edu.curtin.app.probe.Location;
import edu.curtin.app.probe.Probe;

public class MovingState implements ProbeState {
    private Location desLocation;
    public MovingState(Location desLocation) {
        this.desLocation = desLocation;
    }

    @Override
    public String getState() {
        return "MOVING";
    }

    @Override
    public void handleSol(Probe probe, int sol) {
        Location current = probe.getCurrentLocation();
        double distance = current.distanceTo(desLocation);

        if (distance <= probe.getMaxDistance()) {
            System.out.println("TO " + probe.getName().toUpperCase() + ": MOVE BY " + toStringFormat(current, desLocation));
            probe.setCurrentLocation(desLocation);
            probe.setState(new LowPowerMode());
        } else {
            Location next = calcDistance(current, desLocation, probe.getMaxDistance());
            System.out.println("TO " + probe.getName().toUpperCase() + ": MOVE BY " + toStringFormat(current, next));
            probe.setCurrentLocation(next);
        }
    }

    public Location calcDistance(Location before, Location after, double maxDistance) {
        double latDiff = after.getLatitude() - before.getLatitude();
        double longDiff = after.getLongitude() - before.getLongitude();
        double totalDist = Math.sqrt(latDiff * latDiff + longDiff * longDiff);

        double ratio = maxDistance / totalDist;
        double newLat = before.getLatitude() + (latDiff * ratio);
        double newLong = before.getLongitude() + (longDiff * ratio);
        return new Location(newLat, newLong);
    }

    public String toStringFormat(Location before, Location after) {
        double latDiff = after.getLatitude() - before.getLatitude();
        double longDiff = after.getLongitude() - before.getLongitude();
        return String.format("%.6f %.6f", latDiff, longDiff);
    }
}
