package edu.curtin.app.state;

import edu.curtin.app.probe.Probe;

public interface ProbeState {
    String getState();
    void handleSol(Probe probe, int sol);
}