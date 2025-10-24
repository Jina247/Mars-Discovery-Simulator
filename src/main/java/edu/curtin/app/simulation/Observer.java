package edu.curtin.app.simulation;

import edu.curtin.app.probe.Probe;

import java.util.Collection;

public interface Observer {
    void onSolComplete(int sol, Collection<Probe> probes);
    void onHistoryRequest(String probeName);
}
