package edu.curtin.app.probe;

import edu.curtin.app.state.LowPowerMode;
import edu.curtin.app.state.ProbeState;

public abstract class Probe {
    private final String name;
    private Location currentLocation;
    private ProbeState state;

    public Probe(String name, Location initialLocation) {
        this.name = name;
        this.currentLocation = initialLocation;
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

    public abstract double getMaxDistance();
}
