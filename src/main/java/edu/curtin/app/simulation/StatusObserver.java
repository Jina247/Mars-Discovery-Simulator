package edu.curtin.app.simulation;

import edu.curtin.app.probe.Probe;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class StatusObserver implements Observer {
    @Override
    public void onSolComplete(int sol, Collection<Probe> probes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("diagnostic.txt", true))) {
            for (Probe p : probes) {
                String status = String.format("%s at %.6f %.6f, %s", p.getName(), p.getCurrentLocation().getLatitude(),
                        p.getCurrentLocation().getLongitude(), p.getState().getState().toUpperCase());
                writer.write(status);
            }
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error writing to diagnostic.txt: " + e.getMessage());
        }
    }

    @Override
    public void onHistoryRequest(Probe probe) {

    }
}
