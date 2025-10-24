package edu.curtin.app.communication;

import edu.curtin.app.simulation.MarSciSat;

public class MoveMsg implements Message {
    private final String name;
    private double lat;
    private double lon;

    public MoveMsg(String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public void readMessage(MarSciSat sat) {
        sat.handleMoveCommand(name, lat, lon);
    }

    @Override
    public String getMsgType() {
        return "move";
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
