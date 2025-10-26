package edu.curtin.app;

import edu.curtin.app.exception.InvalidMessageException;
import edu.curtin.app.probe.ProbeFactory;
import edu.curtin.app.simulation.HistoryObserver;
import edu.curtin.app.simulation.MarSciSat;
import edu.curtin.app.simulation.StatusObserver;

import java.io.IOException;

/**
 * Entry point into the application.
 */
public class App
{
    public static void main(String[] args) {
        System.out.println("=== Mars Scientific Satellite Simulation ===");
        System.out.println("Press ENTER to stop the simulation\n");

        try {
            runSimulation();
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }

    }

    private static void runSimulation() throws IOException, InvalidMessageException {
        CommsGenerator commsGenerator = new CommsGenerator();
        ProbeFactory factory = new ProbeFactory();
        MarSciSat satellite = new MarSciSat(commsGenerator, factory);

        satellite.addObserver(new StatusObserver());
        satellite.addObserver(new HistoryObserver());

        satellite.initialiseProbes();
        System.out.println("\t==Initialise probes==");

        int sol = 0;
        while (System.in.available() == 0) {
            sol++;
            System.out.println("SOL " + sol);
            satellite.processSol(sol);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new AssertionError(e);
            }
        }
        System.out.println("\n=== Simulation Ended ===");
    }
}
