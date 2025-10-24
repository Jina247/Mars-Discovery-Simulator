package edu.curtin.app.communication;

import edu.curtin.app.simulation.MarSciSat;

public class MoveMsg implements Message {
    private final String probeName;
    private double lat;
    private double lon;

    public MoveMsg(String probeName, double lat, double lon) {
        this.probeName = probeName;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public void readMessage(MarSciSat sat) {

    }

    @Override
    public String getMsgType() {
        return "move";
    }

}
