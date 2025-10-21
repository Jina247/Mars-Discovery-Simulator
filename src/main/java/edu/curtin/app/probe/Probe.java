package edu.curtin.app.probe;

public abstract class Probe {
    private final String name;
    private Location currentLocation;
    private ProbeState state;

    public Probe(String name, Location initialLocation) {
        this.name = name;
        this.currentLocation = initialLocation;

    }

}
