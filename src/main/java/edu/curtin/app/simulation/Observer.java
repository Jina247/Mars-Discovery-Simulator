package edu.curtin.app.simulation;

import edu.curtin.app.probe.Probe;

import java.util.Collection;

/**
 * Observer that write diagnostic information into a file or display probes' history activities.
 */
public interface Observer {
    void onSolComplete(int sol, Collection<Probe> probes);
    void onHistoryRequest(String probeName, Collection<Probe> probes);
}
