package edu.curtin.app.state;

import edu.curtin.app.probe.Probe;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the Measuring State in the State design pattern.
 * A probe enters this state when it needs to perform one or more
 * scientific measurements over a fixed number of Sols.
 */
public class MeasureState implements ProbeState {
    private List<String> measurementTypes;
    private int duration;
    private Map<String, Double> lastMeasurements;

    public MeasureState(String type, int duration) {
        this.measurementTypes = new ArrayList<>();
        this.measurementTypes.add(type);
        this.duration = duration;
        this.lastMeasurements = new TreeMap<>();
    }
    @Override
    public String getState() {
        return "MEASURE";
    }
    /**
     * Performs one Sol of measurement activity. Each Sol:
     *  1. Generates random values for each measurement type
     *  2. Logs and records the results
     *  3. Decreases the remaining duration
     *  4. Switches to LowPowerMode when all Sols are completed
     *
     * @param probe The probe performing the measurement.
     * @param sol The current Sol number.
     */
    @Override
    public void handleSol(Probe probe, int sol) {
        String result = "TO " + probe.getName().toUpperCase() + ": MEASURE ";

        Map<String, Double> measurements = new TreeMap<>();
        Random random = new Random();
        for (String type : measurementTypes) {
            double randomValue = random.nextDouble();
            measurements.put(type, randomValue);
        }
        this.lastMeasurements = measurements;
        String activity = activityString(measurements);
        System.out.println(result + activity);
        probe.record(sol, activity);
        duration--;
        if (duration <= 0) {
            probe.setState(new LowPowerMode());
        }
    }

    /**
     * Updates the current measurement settings. If a new type is added,
     * it will be included in future Sols. Also updates duration.
     *
     * @param type New measurement type to add.
     * @param newDuration New duration for measurement.
     */
    @Override
    public void updateMeasurementType(String type, int newDuration) {
        if (type == null) {
            throw new IllegalArgumentException("Measurement type cannot be null");
        }
        // convert to lowercase for checking duplicate
        String formatType = type.trim().toLowerCase();
        boolean exists = false;
        for (String existing : measurementTypes) {
            if (existing.equalsIgnoreCase(formatType)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            measurementTypes.add(type.trim());
        }
        this.duration = newDuration;
    }
    /**
     * Converts measurement results into a string Log format:
     * Example: DUST=0.9732, VISIBILITY=0.4411
     */
    private String activityString(Map<String, Double> measurements) {
        return measurements.entrySet()
                .stream().map(entry ->
                        entry.getKey().replace(" ", "-").toUpperCase() + "=" + String.format("%.4f", entry.getValue()))
                .collect(Collectors.joining(", "));
    }

    // Returns the most recently collected measurement values.
    public String getLastMeasurementResults() {
        if (lastMeasurements.isEmpty()) {
            return "No measurements yet";
        }
        return activityString(lastMeasurements);
    }

}
