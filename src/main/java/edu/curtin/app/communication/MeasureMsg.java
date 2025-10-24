package edu.curtin.app.communication;

import edu.curtin.app.simulation.MarSciSat;

import java.util.List;

public class MeasureMsg implements Message {
    private final String name;
    private final String measureType;
    private int duration;

    public MeasureMsg(String name, String measureType, int duration) {
        this.name = name;
        this.measureType = measureType;
        this.duration = duration;
    }

    @Override
    public void readMessage(MarSciSat sat) {

    }

    @Override
    public String getMsgType() {
        return "measure";
    }
}
