package edu.curtin.app.probe;

public class Rover extends Probe {
    public Rover(String name, Location initialLocation) {
        super(name, initialLocation);
    }
    @Override
    public double getMaxDistance() {
        return 0.018;
    }
}
