package edu.curtin.app.communication;

import edu.curtin.app.simulation.MarSciSat;

public class StatusMsg implements Message {
    private final String name;

    public StatusMsg(String name) {
        this.name = name;
    }

    @Override
    public void readMessage(MarSciSat sat) {

    }
}
