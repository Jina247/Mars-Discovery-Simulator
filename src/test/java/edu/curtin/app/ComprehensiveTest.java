package edu.curtin.app;

import edu.curtin.app.probe.*;
import edu.curtin.app.simulation.*;
import edu.curtin.app.simulation.Observer;
import edu.curtin.app.state.*;
import edu.curtin.app.communication.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Comprehensive test suite for Mars Probe Simulation.
 * Tests all aspects of the implementation in detail.
 */
public class ComprehensiveTest {

    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║   MARS PROBE SIMULATION - COMPREHENSIVE TEST SUITE        ║");
        System.out.println("╔════════════════════════════════════════════════════════════╗\n");

        // Clean up any previous test files
        cleanup();

        // Run all test categories
        testProbeFactory();
        testLocationOperations();
        testMessageParsing();
        testStateTransitions();
        testMovementBehavior();
        testMeasurementBehavior();
        testActivityRecording();
        testObserverPattern();
        testFileOutput();
        testIntegration();
        testEdgeCases();
        testErrorHandling();

        // Print final summary
        printSummary();

        // Clean up test files
        cleanup();
    }

    // ========================================================================
    // TEST CATEGORY 1: Probe Factory
    // ========================================================================

    private static void testProbeFactory() {
        printCategory("Probe Factory Tests");

        test("Factory creates Rover with correct max distance", () -> {
            ProbeFactory factory = new ProbeFactory();
            Probe rover = factory.build("rover", "rover-0", new Location(0, 0));
            assertEqual(0.004, rover.getMaxDistance(), "Rover max distance");
            return rover.getName().equals("rover-0");
        });

        test("Factory creates Drone with correct max distance", () -> {
            ProbeFactory factory = new ProbeFactory();
            Probe drone = factory.build("drone", "drone-0", new Location(0, 0));
            assertEqual(0.018, drone.getMaxDistance(), "Drone max distance");
            return drone.getName().equals("drone-0");
        });

        test("Factory sets initial location correctly", () -> {
            ProbeFactory factory = new ProbeFactory();
            Location loc = new Location(10.5, 20.3);
            Probe probe = factory.build("rover", "test", loc);
            return Math.abs(probe.getCurrentLocation().getLatitude() - 10.5) < 0.0001 &&
                    Math.abs(probe.getCurrentLocation().getLongitude() - 20.3) < 0.0001;
        });

        test("Factory throws exception for invalid probe type", () -> {
            ProbeFactory factory = new ProbeFactory();
            try {
                factory.build("invalid", "test", new Location(0, 0));
                return false; // Should have thrown exception
            } catch (IllegalArgumentException e) {
                return true;
            }
        });
    }

    // ========================================================================
    // TEST CATEGORY 2: Location Operations
    // ========================================================================

    private static void testLocationOperations() {
        printCategory("Location Operations Tests");

        test("Distance calculation for horizontal movement", () -> {
            Location a = new Location(0, 0);
            Location b = new Location(0, 0.005);
            double dist = a.distanceTo(b);
            return Math.abs(dist - 0.005) < 0.0001;
        });

        test("Distance calculation for vertical movement", () -> {
            Location a = new Location(0, 0);
            Location b = new Location(0.003, 0);
            double dist = a.distanceTo(b);
            return Math.abs(dist - 0.003) < 0.0001;
        });

        test("Distance calculation for diagonal movement (3-4-5 triangle)", () -> {
            Location a = new Location(0, 0);
            Location b = new Location(0.003, 0.004);
            double dist = a.distanceTo(b);
            return Math.abs(dist - 0.005) < 0.0001;
        });

        test("Location toString format is correct", () -> {
            Location loc = new Location(10.123456, -20.654321);
            String str = loc.toString();
            return str.equals("10.123456 -20.654321");
        });

        test("Distance calculation with negative coordinates", () -> {
            Location a = new Location(-10.5, -20.3);
            Location b = new Location(-10.5, -20.3);
            return Math.abs(a.distanceTo(b)) < 0.0001;
        });
    }

    // ========================================================================
    // TEST CATEGORY 3: Message Parsing
    // ========================================================================

    private static void testMessageParsing() {
        printCategory("Message Parsing Tests");

        MessageParser parser = new MessageParser();

        test("Parse valid 'at' message", () -> {
            ParsedMessage result = parser.parse("rover-0 at -11.024183 111.363450");
            return result.isValid() &&
                    result.getMessage().getMsgType().equals("at") &&
                    result.getMessage() instanceof InitLocation;
        });

        test("Parse valid 'move' message", () -> {
            ParsedMessage result = parser.parse("rover-1 move -29.099804 92.474808");
            return result.isValid() &&
                    result.getMessage().getMsgType().equals("move") &&
                    result.getMessage() instanceof MoveMsg;
        });

        test("Parse valid 'measure' message", () -> {
            ParsedMessage result = parser.parse("drone-2 measure dust-concentration 14");
            return result.isValid() &&
                    result.getMessage().getMsgType().equals("measure") &&
                    result.getMessage() instanceof MeasureMsg;
        });

        test("Parse valid 'status' message", () -> {
            ParsedMessage result = parser.parse("rover-3 status");
            return result.isValid() &&
                    result.getMessage().getMsgType().equals("status");
        });

        test("Parse valid 'history' message", () -> {
            ParsedMessage result = parser.parse("drone-1 history");
            return result.isValid() &&
                    result.getMessage().getMsgType().equals("history");
        });

        test("Reject empty message", () -> {
            ParsedMessage result = parser.parse("");
            return !result.isValid();
        });

        test("Reject message with only probe name", () -> {
            ParsedMessage result = parser.parse("rover-0");
            return !result.isValid();
        });

        test("Reject invalid command", () -> {
            ParsedMessage result = parser.parse("rover-0 invalid");
            return !result.isValid();
        });

        test("Reject move with missing coordinates", () -> {
            ParsedMessage result = parser.parse("rover-0 move");
            return !result.isValid();
        });

        test("Reject measure with missing duration", () -> {
            ParsedMessage result = parser.parse("rover-0 measure temperature");
            return !result.isValid();
        });
    }

    // ========================================================================
    // TEST CATEGORY 4: State Transitions
    // ========================================================================

    private static void testStateTransitions() {
        printCategory("State Transition Tests");

        test("Probe starts in LowPowerMode", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            return rover.getState() instanceof LowPowerMode;
        });

        test("Transition from LowPower to Moving", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            rover.setState(new MovingState(new Location(0.001, 0.001)));
            return rover.getState() instanceof MovingState;
        });

        test("Transition from LowPower to Measuring", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            rover.setState(new MeasureState("temperature", 5));
            return rover.getState() instanceof MeasureState;
        });

        test("Transition from Moving to LowPower when destination reached", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            rover.setState(new MovingState(new Location(0.001, 0.001)));
            rover.handleSol(1);
            return rover.getState() instanceof LowPowerMode;
        });

        test("Transition from Measuring to LowPower when duration expires", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            rover.setState(new MeasureState("temperature", 1));
            rover.handleSol(1);
            return rover.getState() instanceof LowPowerMode;
        });

        test("State names are correct", () -> {
            ProbeState lowPower = new LowPowerMode();
            ProbeState moving = new MovingState(new Location(0, 0));
            ProbeState measuring = new MeasureState("temperature", 5);

            return lowPower.getState().contains("LOW") &&
                    moving.getState().equals("DRIVING") &&
                    measuring.getState().contains("MEASURING");
        });
    }

    // ========================================================================
    // TEST CATEGORY 5: Movement Behavior
    // ========================================================================

    private static void testMovementBehavior() {
        printCategory("Movement Behavior Tests");

        test("Rover moves correct distance in one sol", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            rover.setState(new MovingState(new Location(0.003, 0.003)));
            rover.handleSol(1);

            double dist = new Location(0, 0).distanceTo(rover.getCurrentLocation());
            return Math.abs(dist - 0.003 * Math.sqrt(2)) < 0.0001;
        });

        test("Rover doesn't exceed max distance per sol", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            rover.setState(new MovingState(new Location(1.0, 1.0))); // Very far
            rover.handleSol(1);

            double dist = new Location(0, 0).distanceTo(rover.getCurrentLocation());
            return dist <= 0.004;
        });

        test("Drone can move faster than rover", () -> {
            Probe drone = new Drone("test", new Location(0, 0));
            drone.setState(new MovingState(new Location(0.01, 0.01)));
            drone.handleSol(1);

            double dist = new Location(0, 0).distanceTo(drone.getCurrentLocation());
            return dist <= 0.018 && dist > 0.004;
        });

        test("Probe reaches exact destination", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            Location dest = new Location(0.002, 0.002);
            rover.setState(new MovingState(dest));
            rover.handleSol(1);

            Location current = rover.getCurrentLocation();
            return Math.abs(current.getLatitude() - dest.getLatitude()) < 0.0001 &&
                    Math.abs(current.getLongitude() - dest.getLongitude()) < 0.0001;
        });

        test("Multi-sol journey works correctly", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            rover.setState(new MovingState(new Location(0.01, 0.01)));

            // Move for several sols
            for (int sol = 1; sol <= 5; sol++) {
                rover.handleSol(sol);
                if (rover.getState() instanceof LowPowerMode) {
                    break; // Reached destination
                }
            }

            Location final_loc = rover.getCurrentLocation();
            Location dest = new Location(0.01, 0.01);
            double remaining = final_loc.distanceTo(dest);
            return remaining < 0.0001; // Should have reached destination
        });
    }

    // ========================================================================
    // TEST CATEGORY 6: Measurement Behavior
    // ========================================================================

    private static void testMeasurementBehavior() {
        printCategory("Measurement Behavior Tests");

        test("Measurements continue for specified duration", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            rover.setState(new MeasureState("temperature", 3));

            rover.handleSol(1);
            boolean stillMeasuring1 = rover.getState() instanceof MeasureState;

            rover.handleSol(2);
            boolean stillMeasuring2 = rover.getState() instanceof MeasureState;

            rover.handleSol(3);
            boolean stopped = rover.getState() instanceof LowPowerMode;

            return stillMeasuring1 && stillMeasuring2 && stopped;
        });

        test("Multiple measurement types can be added", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            MeasureState state = new MeasureState("temperature", 5);
            state.updateMeasurementType("pressure", 5);
            state.updateMeasurementType("wind-speed", 5);
            rover.setState(state);

            String stateStr = rover.getState().getState();
            return stateStr.contains("TEMPERATURE") &&
                    stateStr.contains("PRESSURE") &&
                    stateStr.contains("WIND-SPEED");
        });

        test("Duration updates when new measurement added", () -> {
            MeasureState state = new MeasureState("temperature", 5);
            state.updateMeasurementType("pressure", 10);

            Probe rover = new Rover("test", new Location(0, 0));
            rover.setState(state);

            // Process 7 sols
            for (int i = 1; i <= 7; i++) {
                rover.handleSol(i);
            }

            // Should still be measuring (10 sol duration)
            return rover.getState() instanceof MeasureState;
        });

        test("Duplicate measurement type doesn't get added twice", () -> {
            MeasureState state = new MeasureState("temperature", 5);
            state.updateMeasurementType("temperature", 5);
            state.updateMeasurementType("temperature", 5);

            String stateStr = state.getState();
            int count = stateStr.split("TEMPERATURE").length - 1;
            return count == 1;
        });
    }

    // ========================================================================
    // TEST CATEGORY 7: Activity Recording
    // ========================================================================

    private static void testActivityRecording() {
        printCategory("Activity Recording Tests");

        test("Activities are recorded correctly", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            rover.record(1, "+0.001 +0.001");
            rover.record(2, "TEMPERATURE=0.5");

            Map<Integer, String> activities = rover.getProbeActivities();
            return activities.size() == 2 &&
                    activities.containsKey(1) &&
                    activities.containsKey(2);
        });

        test("Movement activities are recorded", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            rover.setState(new MovingState(new Location(0.001, 0.001)));
            rover.handleSol(1);

            Map<Integer, String> activities = rover.getProbeActivities();
            return activities.size() > 0 && activities.containsKey(1);
        });

        test("Measurement activities are recorded", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            rover.setState(new MeasureState("temperature", 2));
            rover.handleSol(1);
            rover.handleSol(2);

            Map<Integer, String> activities = rover.getProbeActivities();
            return activities.size() == 2 &&
                    activities.get(1).contains("TEMPERATURE") &&
                    activities.get(2).contains("TEMPERATURE");
        });

        test("Activity history is in correct order", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            rover.record(3, "activity3");
            rover.record(1, "activity1");
            rover.record(2, "activity2");

            Map<Integer, String> activities = rover.getProbeActivities();
            List<Integer> keys = new ArrayList<>(activities.keySet());

            return keys.get(0) == 1 && keys.get(1) == 2 && keys.get(2) == 3;
        });

        test("Empty history is handled correctly", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            Map<Integer, String> activities = rover.getProbeActivities();
            return activities.isEmpty();
        });
    }

    // ========================================================================
    // TEST CATEGORY 8: Observer Pattern
    // ========================================================================

    private static void testObserverPattern() {
        printCategory("Observer Pattern Tests");

        test("Observers can be added to MarSciSat", () -> {
            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            TestObserver obs = new TestObserver();
            sat.addObserver(obs);

            return true; // If no exception, success
        });

        test("Observers are notified on sol complete", () -> {
            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            TestObserver obs = new TestObserver();
            sat.addObserver(obs);
            sat.initialiseProbes();
            sat.processSol(1);

            return obs.solNotifications > 0;
        });

        test("Multiple observers all get notified", () -> {
            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            TestObserver obs1 = new TestObserver();
            TestObserver obs2 = new TestObserver();
            sat.addObserver(obs1);
            sat.addObserver(obs2);

            sat.initialiseProbes();
            sat.processSol(1);

            return obs1.solNotifications > 0 && obs2.solNotifications > 0;
        });

        test("StatusObserver writes to file", () -> {
            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            sat.addObserver(new StatusObserver());
            sat.initialiseProbes();
            sat.processSol(1);

            File file = new File("diagnostic.txt");
            return file.exists() && file.length() > 0;
        });

        test("HistoryObserver responds to history requests", () -> {
            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            TestObserver obs = new TestObserver();
            sat.addObserver(obs);
            sat.initialiseProbes();

            // Trigger history request
            sat.handleHistoryCommand("rover-0");

            return obs.historyRequests > 0;
        });
    }

    // ========================================================================
    // TEST CATEGORY 9: File Output
    // ========================================================================

    private static void testFileOutput() {
        printCategory("File Output Tests");

        test("diagnostic.txt is created", () -> {
            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            sat.addObserver(new StatusObserver());
            sat.initialiseProbes();
            sat.processSol(1);

            return new File("diagnostic.txt").exists();
        });

        test("diagnostic.txt contains sol headers", () -> {
            try {
                String content = new String(Files.readAllBytes(Paths.get("diagnostic.txt")));
                return content.contains("SOL");
            } catch (IOException e) {
                return false;
            }
        });

        test("diagnostic.txt contains probe information", () -> {
            try {
                String content = new String(Files.readAllBytes(Paths.get("diagnostic.txt")));
                return content.contains("rover") || content.contains("drone");
            } catch (IOException e) {
                return false;
            }
        });

        test("diagnostic.txt is appended, not overwritten", () -> {
            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            sat.addObserver(new StatusObserver());
            sat.initialiseProbes();
            sat.processSol(1);

            long size1 = new File("diagnostic.txt").length();

            sat.processSol(2);

            long size2 = new File("diagnostic.txt").length();

            return size2 > size1;
        });
    }

    // ========================================================================
    // TEST CATEGORY 10: Integration Tests
    // ========================================================================

    private static void testIntegration() {
        printCategory("Integration Tests");

        test("Full system initialization", () -> {
            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            sat.addObserver(new StatusObserver());
            sat.addObserver(new HistoryObserver());
            sat.initialiseProbes();

            return true; // If no exception, success
        });

        test("Multiple sols can be processed", () -> {
            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            sat.initialiseProbes();

            for (int sol = 1; sol <= 10; sol++) {
                sat.processSol(sol);
            }

            return true; // If no exception, success
        });

        test("Invalid messages don't crash system", () -> {
            MessageParser parser = new MessageParser();

            String[] invalid = {
                    "",
                    "rover-0",
                    "invalid",
                    "rover-0 move abc def",
                    null
            };

            for (String msg : invalid) {
                try {
                    ParsedMessage result = parser.parse(msg);
                    if (result == null) return false;
                } catch (Exception e) {
                    return false;
                }
            }

            return true;
        });

        test("System handles all message types", () -> {
            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            sat.addObserver(new TestObserver());
            sat.initialiseProbes();

            // Process multiple sols to get various message types
            for (int sol = 1; sol <= 20; sol++) {
                sat.processSol(sol);
            }

            return true; // If no exception, success
        });
    }

    // ========================================================================
    // TEST CATEGORY 11: Edge Cases
    // ========================================================================

    private static void testEdgeCases() {
        printCategory("Edge Case Tests");

        test("Probe at exact max distance moves in one sol", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            Location dest = new Location(0.004, 0);
            rover.setState(new MovingState(dest));
            rover.handleSol(1);

            return rover.getState() instanceof LowPowerMode &&
                    rover.getCurrentLocation().distanceTo(dest) < 0.0001;
        });

        test("Zero distance movement", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            rover.setState(new MovingState(new Location(0, 0)));
            rover.handleSol(1);

            return rover.getState() instanceof LowPowerMode;
        });

        test("Very long journey eventually completes", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            rover.setState(new MovingState(new Location(1.0, 1.0)));

            int maxSols = 1000;
            for (int sol = 1; sol <= maxSols; sol++) {
                rover.handleSol(sol);
                if (rover.getState() instanceof LowPowerMode) {
                    return true;
                }
            }

            return false; // Didn't complete in reasonable time
        });

        test("Measurement with duration 1 stops after one sol", () -> {
            Probe rover = new Rover("test", new Location(0, 0));
            rover.setState(new MeasureState("temperature", 1));
            rover.handleSol(1);

            return rover.getState() instanceof LowPowerMode;
        });

        test("Negative coordinates work correctly", () -> {
            Location a = new Location(-10.5, -20.3);
            Location b = new Location(-10.0, -20.0);
            double dist = a.distanceTo(b);

            return dist > 0;
        });
    }

    // ========================================================================
    // TEST CATEGORY 12: Error Handling
    // ========================================================================

    private static void testErrorHandling() {
        printCategory("Error Handling Tests");

        test("MessageParser handles null input", () -> {
            MessageParser parser = new MessageParser();
            ParsedMessage result = parser.parse(null);
            return !result.isValid();
        });

        test("MessageParser handles empty string", () -> {
            MessageParser parser = new MessageParser();
            ParsedMessage result = parser.parse("");
            return !result.isValid();
        });

        test("MessageParser handles whitespace-only string", () -> {
            MessageParser parser = new MessageParser();
            ParsedMessage result = parser.parse("   ");
            return !result.isValid();
        });

        test("Factory rejects invalid probe type", () -> {
            ProbeFactory factory = new ProbeFactory();
            try {
                factory.build("invalid-type", "test", new Location(0, 0));
                return false;
            } catch (IllegalArgumentException e) {
                return true;
            }
        });

        test("System handles unknown probe in commands", () -> {
            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            sat.initialiseProbes();

            // Try to command non-existent probe
            sat.handleMoveCommand("rover-99", 0, 0);

            return true; // Should not crash
        });
    }

    // ========================================================================
    // Test Helper Classes and Methods
    // ========================================================================

    private static class TestObserver implements Observer {
        int solNotifications = 0;
        int historyRequests = 0;

        @Override
        public void onSolComplete(int sol, Collection<Probe> probes) {
            solNotifications++;
        }

        @Override
        public void onHistoryRequest(String probeName, Collection<Probe> probes) {
            historyRequests++;
        }
    }

    private static void test(String name, TestCase testCase) {
        totalTests++;
        try {
            boolean result = testCase.run();
            if (result) {
                passedTests++;
                System.out.println("   ✓ " + name);
            } else {
                failedTests++;
                System.out.println("   ✗ " + name);
            }
        } catch (Exception e) {
            failedTests++;
            System.out.println("   ✗ " + name + " (Exception: " + e.getMessage() + ")");
        }
    }

    private static void assertEqual(double expected, double actual, String message) {
        if (Math.abs(expected - actual) > 0.0001) {
            throw new AssertionError(message + ": expected " + expected + ", got " + actual);
        }
    }

    private static void printCategory(String category) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("  " + category);
        System.out.println("═".repeat(60));
    }

    private static void printSummary() {
        System.out.println("\n" + "╔".repeat(60));
        System.out.println("                    TEST SUMMARY");
        System.out.println("╚".repeat(60));
        System.out.printf("Total Tests:  %d\n", totalTests);
        System.out.printf("Passed:       %d (%.1f%%)\n", passedTests,
                (passedTests * 100.0 / totalTests));
        System.out.printf("Failed:       %d\n", failedTests);
        System.out.println("═".repeat(60));

        if (failedTests == 0) {
            System.out.println("\n🎉 ALL TESTS PASSED! 🎉");
            System.out.println("Your implementation is excellent!");
        } else if (passedTests > totalTests * 0.9) {
            System.out.println("\n✓ VERY GOOD! Most tests passed.");
            System.out.println("Review the failed tests above and make minor fixes.");
        } else if (passedTests > totalTests * 0.7) {
            System.out.println("\n△ GOOD PROGRESS! Many tests passed.");
            System.out.println("Review the failed tests and continue working.");
        } else {
            System.out.println("\n⚠ NEEDS WORK. Several tests failed.");
            System.out.println("Focus on the failed tests and apply the critical fixes.");
        }

        System.out.println("\n" + "═".repeat(60));
    }

    private static void cleanup() {
        // Clean up test files
        try {
            Files.deleteIfExists(Paths.get("diagnostic.txt"));
            Files.deleteIfExists(Paths.get("simulation.log"));
        } catch (IOException e) {
            // Ignore cleanup errors
        }
    }

    @FunctionalInterface
    private interface TestCase {
        boolean run() throws Exception;
    }
}