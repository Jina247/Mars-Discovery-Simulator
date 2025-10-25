package edu.curtin.app.simulation;

import edu.curtin.app.probe.Probe;

import java.util.Collection;

public class HistoryObserver implements Observer {
    @Override
    public void onSolComplete(int sol, Collection<Probe> probes) {
        // Handled by StatusObserver
    }

    @Override
    public void onHistoryRequest(String probeName, Collection<Probe> probes) {
        for (Probe p : probes) {
            if (p.getName().equals(probeName)) {
                p.printHistory();
                return;
            }
        }
        System.out.println("TO EARTH: MESSAGE ERROR \"Unknown probe: " + probeName + "\"");
    }
}
