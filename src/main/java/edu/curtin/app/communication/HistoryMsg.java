package edu.curtin.app.communication;

import edu.curtin.app.simulation.MarSciSat;

public class HistoryMsg implements Message {
    private final String name;

    public HistoryMsg(String name) {
        this.name = name;
    }

    @Override
    public void readMessage(MarSciSat sat) {

    }
}
