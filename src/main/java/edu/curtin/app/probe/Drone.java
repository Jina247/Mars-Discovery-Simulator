package edu.curtin.app.probe;

public class Drone extends Probe {
    public Drone(String name, Location initialLocation) {
        super(name, initialLocation);
    }

    @Override
    public double getMaxDistance() {

         return 0;
    }
}
