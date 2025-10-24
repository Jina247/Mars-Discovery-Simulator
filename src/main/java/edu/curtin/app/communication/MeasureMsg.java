package edu.curtin.app.communication;

import edu.curtin.app.simulation.MarSciSat;

import java.util.List;

public class MeasureMsg implements Message {
    private final String name;
    private final List<String> measureTypes;
    private int duration;

    public MeasureMsg(String name, List<String> measureTypes, int duration) {
        this.name = name;
        this.measureTypes = measureTypes;
        this.duration = duration;
    }

    @Override
    public void readMessage(MarSciSat sat) {

    }
}
