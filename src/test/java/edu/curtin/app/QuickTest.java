package edu.curtin.app;

import edu.curtin.app.probe.*;
import edu.curtin.app.simulation.*;
import edu.curtin.app.state.*;
import edu.curtin.app.communication.*;

/**
 * Quick test runner to verify your implementation works.
 * Run this BEFORE fixing everything to see what breaks,
 * then AFTER fixing to verify everything works.
 */
public class QuickTest {
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    MARS PROBE QUICK TEST SUITE");
        System.out.println("========================================\n");

        int passed = 0;
        int failed = 0;

        // Test 1: Probe Creation
        System.out.println("Test 1: Probe Factory");
        try {
            ProbeFactory factory = new ProbeFactory();
            Probe rover = factory.build("rover", "rover-0", new Location(0, 0));
            Probe drone = factory.build("drone", "drone-0", new Location(0, 0));

            assert rover.getMaxDistance() == 0.004 : "Rover max distance wrong";
            assert drone.getMaxDistance() == 0.018 : "Drone max distance wrong";

            System.out.println("   ✓ PASSED\n");
            passed++;
        } catch (AssertionError | Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage() + "\n");
            failed++;
        }

        // Test 2: Location Distance
        System.out.println("Test 2: Location Distance Calculation");
        try {
            Location a = new Location(0, 0);
            Location b = new Location(0.003, 0.004);
            double dist = a.distanceTo(b);
            double expected = 0.005;

            assert Math.abs(dist - expected) < 0.0001 :
                    "Distance wrong: expected " + expected + ", got " + dist;

            System.out.println("   ✓ PASSED\n");
            passed++;
        } catch (AssertionError | Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage() + "\n");
            failed++;
        }

        // Test 3: Message Parsing
        System.out.println("Test 3: Message Parsing");
        try {
            MessageParser parser = new MessageParser();

            ParsedMessage valid = parser.parse("rover-0 at -11.024183 111.363450");
            assert valid.isValid() : "Valid message marked invalid";
            assert valid.getMessage().getMsgType().equals("at") : "Wrong message type";

            ParsedMessage invalid = parser.parse("invalid");
            assert !invalid.isValid() : "Invalid message marked valid";

            System.out.println("   ✓ PASSED\n");
            passed++;
        } catch (AssertionError | Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage() + "\n");
            failed++;
        }

        // Test 4: State Transitions
        System.out.println("Test 4: State Transitions");
        try {
            Probe rover = new Rover("test-rover", new Location(0, 0));

            assert rover.getState() instanceof LowPowerMode : "Wrong initial state";

            rover.setState(new MovingState(new Location(0.001, 0.001)));
            assert rover.getState() instanceof MovingState : "State didn't change to Moving";

            rover.setState(new MeasureState("temperature", 5));
            assert rover.getState() instanceof MeasureState : "State didn't change to Measure";

            System.out.println("   ✓ PASSED\n");
            passed++;
        } catch (AssertionError | Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage() + "\n");
            failed++;
        }

        // Test 5: Activity Recording
        System.out.println("Test 5: Activity Recording");
        try {
            Probe rover = new Rover("test-rover", new Location(0, 0));

            rover.record(1, "+0.001 +0.001");
            rover.record(2, "TEMPERATURE=0.5");

            var activities = rover.getProbeActivities();
            assert activities.size() == 2 : "Wrong activity count: " + activities.size();
            assert activities.containsKey(1) : "Sol 1 activity missing";
            assert activities.containsKey(2) : "Sol 2 activity missing";

            System.out.println("   ✓ PASSED\n");
            passed++;
        } catch (AssertionError | Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage() + "\n");
            e.printStackTrace();
            failed++;
        }

        // Test 6: Movement Calculation
        System.out.println("Test 6: Movement Calculation");
        try {
            Location start = new Location(0, 0);
            Location target = new Location(0.01, 0.01); // Far away
            double maxDist = 0.004;

            MovingState state = new MovingState(target);
            Location next = state.calcDistance(start, target, maxDist);

            double actualDist = start.distanceTo(next);
            assert Math.abs(actualDist - maxDist) < 0.0001 :
                    "Movement distance wrong: " + actualDist;

            System.out.println("   ✓ PASSED\n");
            passed++;
        } catch (AssertionError | Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage() + "\n");
            failed++;
        }

        // Test 7: Observer Pattern
        System.out.println("Test 7: Observer Pattern");
        try {
            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            TestObserver observer = new TestObserver();
            sat.addObserver(observer);

            sat.initialiseProbes();
            sat.processSol(1);

            assert observer.notifyCount > 0 : "Observer never notified";

            System.out.println("   ✓ PASSED\n");
            passed++;
        } catch (AssertionError | Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage() + "\n");
            e.printStackTrace();
            failed++;
        }

        // Test 8: Full Simulation (3 sols)
        System.out.println("Test 8: Full Simulation (3 sols)");
        try {
            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            sat.addObserver(new StatusObserver());
            sat.initialiseProbes();

            for (int sol = 1; sol <= 3; sol++) {
                sat.processSol(sol);
            }

            System.out.println("   ✓ PASSED\n");
            passed++;
        } catch (Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage() + "\n");
            e.printStackTrace();
            failed++;
        }

        // Summary
        System.out.println("========================================");
        System.out.println("           TEST SUMMARY");
        System.out.println("========================================");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total:  " + (passed + failed));
        System.out.println("========================================");

        if (failed == 0) {
            System.out.println("\n🎉 ALL TESTS PASSED! 🎉");
            System.out.println("Your implementation looks good!");
        } else {
            System.out.println("\n⚠️  SOME TESTS FAILED");
            System.out.println("Review the failures above and apply fixes.");
            System.out.println("See the 'Critical Fixes Required' document.");
        }
    }

    // Test helper class
    private static class TestObserver implements Observer {
        int notifyCount = 0;

        @Override
        public void onSolComplete(int sol, java.util.Collection<Probe> probes) {
            notifyCount++;
        }

        @Override
        public void onHistoryRequest(String probeName, java.util.Collection<Probe> probes) {
            notifyCount++;
        }
    }
}