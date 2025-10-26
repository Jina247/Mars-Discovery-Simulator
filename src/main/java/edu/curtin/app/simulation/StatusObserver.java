package edu.curtin.app.simulation;

import edu.curtin.app.probe.Probe;
import edu.curtin.app.state.MeasureState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

public class StatusObserver implements Observer {
    @Override
    public void onSolComplete(int sol, Collection<Probe> probes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("diagnostic.txt", true))) {
            writer.write("SOL " + sol + "\n");
            for (Probe p : probes) {
                String status;
                if (!(p.getState().getState().toUpperCase().equals("MEASURE"))) {
                    status = String.format("%s at %.6f %.6f, %s\n", p.getName().toUpperCase(), p.getCurrentLocation().getLatitude(),
                            p.getCurrentLocation().getLongitude(), p.getState().getState().toUpperCase());
                } else {
                    MeasureState measureState = (MeasureState) p.getState();
                    status = String.format("%s at %.6f %.6f, %s: %s\n", p.getName().toUpperCase(), p.getCurrentLocation().getLatitude(),
                            p.getCurrentLocation().getLongitude(), p.getState().getState().toUpperCase(), measureState.getLastMeasurementResults());
                }
                writer.write(status);
            }
            writer.write("\n");
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error writing to diagnostic.txt: " + e.getMessage());
        }
    }

    @Override
    public void onHistoryRequest(String probeName, Collection<Probe> probes) {
        // Handled by HistoryObserver
    }
}

