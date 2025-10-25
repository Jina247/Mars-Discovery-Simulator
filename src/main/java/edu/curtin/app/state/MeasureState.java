package edu.curtin.app.state;

import edu.curtin.app.probe.Probe;

import java.util.*;
import java.util.stream.Collectors;

public class MeasureState implements ProbeState {
    private List<String> measurementTypes;
    private int duration;

    public MeasureState(String type, int duration) {
        this.measurementTypes = new ArrayList<>();
        this.measurementTypes.add(type);
        this.duration = duration;
    }
    @Override
    public String getState() {
        return "MEASURE: " + String.join(", ", measurementTypes).toUpperCase();
    }

    @Override
    public void handleSol(Probe probe, int sol) {
        String types = String.join(", ", measurementTypes);
        System.out.println("TO " + probe.getName().toUpperCase() + ": MEASURE " + types.toUpperCase());

        Map<String, Double> measurements = new TreeMap<>();
        Random random = new Random();
        for (String type : measurementTypes) {
            double randomValue = random.nextDouble();
            measurements.put(type, randomValue);
        }
        String activity = measurements.entrySet()
                        .stream().map(entry ->
                        entry.getKey().replace(" ", "-").toUpperCase() + "=" + String.format("%.4f", entry.getValue()))
                        .collect(Collectors.joining(", "));
        System.out.println(activity);
        probe.record(sol, activity);
        duration--;
        if (duration <= 0) {
            probe.setState(new LowPowerMode());
        }
    }

    public void updateMeasurementType(String type, int newDuration) {
        if (!(measurementTypes.contains(type))) {
            measurementTypes.add(type);
        }
        this.duration = newDuration;
    }
}
