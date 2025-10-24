package edu.curtin.app.communication;

import edu.curtin.app.simulation.MarSciSat;

public class InitLocation implements Message {
    private final String name;
    private final double lat;
    private final double lon;

    public InitLocation(String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public void readMessage(MarSciSat sat) {
        sat.handleInitLocation(name, lat, lon);
    }

    @Override
    public String getMsgType() {
        return "at";
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
}
