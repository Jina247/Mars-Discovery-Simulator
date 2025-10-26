package edu.curtin.app.state;

import edu.curtin.app.probe.Probe;

import java.util.*;
import java.util.stream.Collectors;

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

    private String activityString(Map<String, Double> measurements) {
        return measurements.entrySet()
                .stream().map(entry ->
                        entry.getKey().replace(" ", "-").toUpperCase() + "=" + String.format("%.4f", entry.getValue()))
                .collect(Collectors.joining(", "));
    }

    public String getLastMeasurementResults() {
        if (lastMeasurements.isEmpty()) {
            return "No measurements yet";
        }
        return activityString(lastMeasurements);
    }

}
