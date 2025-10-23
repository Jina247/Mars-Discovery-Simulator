package edu.curtin.app.state;

import edu.curtin.app.probe.Probe;

public class LowPowerMode implements ProbeState {
    @Override
    public String getState() {
        return "LOW_MODE";
    }

    @Override
    public void handleSol(Probe probe, int sol) {

    }
}
