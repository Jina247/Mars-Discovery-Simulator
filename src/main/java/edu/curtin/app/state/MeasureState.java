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
        return "MEASURE";
    }

    @Override
    public void handleSol(Probe probe, int sol) {
        String types = measurementTypes.stream()
                .map(String::toUpperCase)
                .collect(Collectors.joining(", "));
        System.out.println("TO " + probe.getName().toUpperCase() + ": MEASURE " + types);

        Map<String, Double> measurements = new HashMap<>();
        Random random = new Random();
        for (String type : measurementTypes) {
            double randomValue = random.nextDouble();
            measurements.put(type, randomValue);
            System.out.println(type + " = " + String.format("%.4f", randomValue));
        }
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
