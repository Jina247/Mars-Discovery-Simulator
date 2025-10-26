package edu.curtin.app.simulation;

import edu.curtin.app.probe.Probe;
import edu.curtin.app.state.MeasureState;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Observer that writes diagnostic information about all probes to a file,
 * records the state and location of each probe at the end of every Sol.
 */
public class StatusObserver implements Observer {
    private static final Logger logger = Logger.getLogger(StatusObserver.class.getName());
    public static final String DIAGNOSTIC_FILE = "diagnostic.txt";

    /**
     * Called when a Sol is completed. Writes all probe statuses to diagnostic.txt.
     * For probes in MEASURE state, includes their current measurement results.
     *
     * @param sol the completed Sol number
     * @param probes collection of all active probes
     */
    @Override
    public void onSolComplete(int sol, Collection<Probe> probes) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DIAGNOSTIC_FILE, true))) {
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
            logger.log(Level.FINE, "Diagnostic written for Sol {0}", sol);
        } catch (IOException e) {
            System.err.println("Error writing to diagnostic.txt: " + e.getMessage());
            logger.log(Level.SEVERE, "Failed to write diagnostic file", e);
        }
    }


    @Override
    public void onHistoryRequest(String probeName, Collection<Probe> probes) {
        // Handled by HistoryObserver
    }
}

