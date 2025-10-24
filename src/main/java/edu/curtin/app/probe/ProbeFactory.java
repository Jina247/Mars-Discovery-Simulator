package edu.curtin.app.probe;

public class ProbeFactory {
    public Probe build(String type, String name, Location location) {
        if (type.equals("drone")) {
            return new Drone(name, location);
        } else if (type.equals("rover")) {
            return new Rover(name, location);
        } else {
            throw new IllegalArgumentException("Unknown probe type " + type);
        }
    }
}
