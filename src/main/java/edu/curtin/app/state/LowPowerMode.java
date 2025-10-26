package edu.curtin.app.state;

import edu.curtin.app.probe.Probe;

public class LowPowerMode implements ProbeState {
    @Override
    public String getState() {
        return "LOW-POWER";
    }

    @Override
    public void handleSol(Probe probe, int sol) {

    }

    @Override
    public void updateMeasurementType(String type, int duration) {

    }
}
