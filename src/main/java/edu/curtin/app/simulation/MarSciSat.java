package edu.curtin.app.simulation;

import edu.curtin.app.CommsGenerator;
import edu.curtin.app.communication.*;
import edu.curtin.app.exception.InvalidMessageException;
import edu.curtin.app.probe.*;
import edu.curtin.app.state.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main satellite controller that manages probes on Mars.
 * Handles communication with Earth, processes commands, and coordinates
 * probe activities across Sols.
 */
public class MarSciSat {
    private static final Logger logger = Logger.getLogger(MarSciSat.class.getName());
    private final CommsGenerator commsGen;
    private final ProbeFactory factory;
    private final MessageParser parser;
    private final Map<String, Probe> probes;
    private final List<Observer> observers;

    public MarSciSat(CommsGenerator commsGen, ProbeFactory factory) {
        this.commsGen = commsGen;
        this.factory = factory;
        this.parser = new MessageParser();
        this.probes = new HashMap<>();
        this.observers = new ArrayList<>();
    }

    /**
     * Registers an observer to receive notifications about probe activities.
     * @param observer the observer to add
     */
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Initializes all probes by reading their initial location messages.
     * Continues reading until all expected probes are initialized or no more
     * messages are available.
     */
    public void initialiseProbes() throws InvalidMessageException {
        // Read initial locations
        String msg;
        while ((msg = commsGen.nextMessage()) != null) {
            try {
                ParsedMessage parsed = parser.parse(msg);
                if (parsed.isValid() && parsed.getMessage().getMsgType().equals("at")) {
                    parsed.getMessage().readMessage(this);
                }

                if (probes.size() == 8) {  // 5 rovers + 3 drones
                    break;
                }
            } catch (InvalidMessageException e) {
                logger.log(Level.SEVERE, "Invalid message during probe initialisation: " + msg, e);
            }
        }
        System.out.println("Build " + probes.size() + " probes");
    }

    public void processSol(int sol) throws InvalidMessageException {
        // Get Earth messages
        String msg;
        while ((msg = commsGen.nextMessage()) != null) {
            ParsedMessage parsed = parser.parse(msg);
            if (parsed.isValid()) {
                parsed.getMessage().readMessage(this);
            } else {
                System.out.println("TO EARTH: MESSAGE ERROR \"" + msg + "\"");
                logger.log(Level.WARNING, "Invalid message on Sol {0}: {1}");
            }
        }

        // Process each probe
        for (Probe probe : probes.values()) {
            probe.handleSol(sol);
        }

        // Notify observers
        notifyObservers(sol);
    }

    // Handler methods for Message implementations
    public void handleInitLocation(String name, double lat, double lon) {
        String type = name.startsWith("rover") ? "rover" : "drone";
        Probe probe = factory.build(type, name, new Location(lat, lon));
        probes.put(name, probe);
    }

    public void handleMoveCommand(String probeName, double lat, double lon) {
        Probe probe = probes.get(probeName);
        if (probe != null) {
            probe.setState(new MovingState(new Location(lat, lon)));
        }
    }

    public void handleMeasureCommand(String probeName, String type, int duration) {
        Probe probe = probes.get(probeName);
        if (probe != null) {
            if (probe.getState().getState().equals("MEASURE")) {
                probe.getState().updateMeasurementType(type, duration);
            } else {
                probe.setState(new MeasureState(type, duration));
            }
        }
    }

    public void handleStatusCommand(String probeName) {
        Probe probe = probes.get(probeName);
        if (probe != null) {
            System.out.println("TO EARTH: " + probe.getName().toUpperCase() + " at " + probe.getCurrentLocation() + ", " +
                    probe.getState().getState());
        }
    }

    public void handleHistoryCommand(String probeName) {
        // Delegates to observers to display probe's history
        notifyHistoryRequest(probeName);
    }

    /**
     * Notifies all observers that a Sol has been completed.
     * @param sol the completed Sol number
     */
    private void notifyObservers(int sol) {
        for (Observer observer : observers) {
            observer.onSolComplete(sol, probes.values());
        }
    }

    /**
     * Notifies all observers of a history request.
     * @param probeName the probe whose history is requested
     */
    private void notifyHistoryRequest(String probeName) {
        for (Observer observer : observers) {
            observer.onHistoryRequest(probeName, probes.values());
        }
    }
}