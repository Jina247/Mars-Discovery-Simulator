package edu.curtin.app.probe;

import edu.curtin.app.state.LowPowerMode;
import edu.curtin.app.state.ProbeState;

import java.util.Map;
import java.util.TreeMap;

public abstract class Probe {
    private final String name;
    private Location currentLocation;
    private ProbeState state;
    private final Map<Integer, String> probeActivities;

    public Probe(String name, Location initialLocation) {
        this.name = name;
        this.currentLocation = initialLocation;
        this.probeActivities = new TreeMap<>();
        this.state = new LowPowerMode();
    }

    public String getName() { return this.name; }
    public Location getCurrentLocation() { return this.currentLocation; }
    public ProbeState getState() { return this.state; }

    public void setCurrentLocation(Location location) {
        this.currentLocation = location;
    }

    public void setState(ProbeState state) {
        this.state = state;
    }

    public void handleSol(int sol) {
        state.handleSol(this, sol);
    }

    public void record(int sol, String activity) {
        probeActivities.put(sol, activity);
    }

    public Map<Integer, String> getProbeActivities(int sol, String activity) {
        return new TreeMap<>(probeActivities);
    }

    public void printHistory() {
        System.out.println("TO EARTH: " + name.toUpperCase() + " ACTIVITIES:");
        if (probeActivities.isEmpty()) {
            System.out.println("    (No activities recorded)");
            return;
        }

        for (Map.Entry<Integer, String> entry : probeActivities.entrySet()) {
            System.out.println("    SOL " + entry.getKey() + ": " + entry.getValue());
        }
    }
    public abstract double getMaxDistance();
}
