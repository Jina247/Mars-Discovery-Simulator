package edu.curtin.app;

import edu.curtin.app.probe.*;
import edu.curtin.app.simulation.*;
import edu.curtin.app.state.*;
import java.io.*;
import java.nio.file.*;

/**
 * Stress testing for the Mars Probe Simulation.
 * Tests performance, memory usage, and stability over extended runs.
 */
public class StressTest {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║        MARS PROBE SIMULATION - STRESS TEST SUITE          ║");
        System.out.println("╔════════════════════════════════════════════════════════════╗\n");

        System.out.println("These tests will run for several seconds...\n");

        // Run stress tests
        testLongRunningSimulation();
        testMemoryUsage();
        testFileGrowth();
        testRandomSeeds();
        testConcurrentObservers();
        testHighFrequencyCommands();

        System.out.println("\n" + "═".repeat(60));
        System.out.println("           STRESS TESTS COMPLETED");
        System.out.println("═".repeat(60));
    }

    // ========================================================================
    // Test 1: Long Running Simulation
    // ========================================================================

    private static void testLongRunningSimulation() {
        System.out.println("Test 1: Long Running Simulation (100 sols)");
        System.out.println("─".repeat(60));

        try {
            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            sat.addObserver(new StatusObserver());
            sat.initialiseProbes();

            long startTime = System.currentTimeMillis();

            for (int sol = 1; sol <= 100; sol++) {
                sat.processSol(sol);

                if (sol % 10 == 0) {
                    System.out.printf("   Processed %d sols... ", sol);
                    long elapsed = System.currentTimeMillis() - startTime;
                    System.out.printf("(%.2f seconds elapsed)\n", elapsed / 1000.0);
                }
            }

            long totalTime = System.currentTimeMillis() - startTime;
            System.out.printf("   ✓ Completed 100 sols in %.2f seconds\n", totalTime / 1000.0);
            System.out.printf("   Average: %.2f ms per sol\n", totalTime / 100.0);

        } catch (Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ========================================================================
    // Test 2: Memory Usage
    // ========================================================================

    private static void testMemoryUsage() {
        System.out.println("Test 2: Memory Usage Monitoring");
        System.out.println("─".repeat(60));

        try {
            Runtime runtime = Runtime.getRuntime();

            // Force garbage collection to get baseline
            System.gc();
            Thread.sleep(100);
            long baselineMemory = runtime.totalMemory() - runtime.freeMemory();
            System.out.printf("   Baseline memory: %.2f MB\n", baselineMemory / (1024.0 * 1024.0));

            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            sat.addObserver(new StatusObserver());
            sat.initialiseProbes();

            long afterInitMemory = runtime.totalMemory() - runtime.freeMemory();
            System.out.printf("   After init: %.2f MB (%.2f MB increase)\n",
                    afterInitMemory / (1024.0 * 1024.0),
                    (afterInitMemory - baselineMemory) / (1024.0 * 1024.0));

            // Run 50 sols
            for (int sol = 1; sol <= 50; sol++) {
                sat.processSol(sol);
            }

            long after50Sols = runtime.totalMemory() - runtime.freeMemory();
            System.out.printf("   After 50 sols: %.2f MB (%.2f MB increase)\n",
                    after50Sols / (1024.0 * 1024.0),
                    (after50Sols - afterInitMemory) / (1024.0 * 1024.0));

            // Run another 50 sols
            for (int sol = 51; sol <= 100; sol++) {
                sat.processSol(sol);
            }

            long after100Sols = runtime.totalMemory() - runtime.freeMemory();
            System.out.printf("   After 100 sols: %.2f MB (%.2f MB increase from 50)\n",
                    after100Sols / (1024.0 * 1024.0),
                    (after100Sols - after50Sols) / (1024.0 * 1024.0));

            double growthRate = (after100Sols - after50Sols) / (1024.0 * 1024.0);
            if (growthRate < 5.0) {
                System.out.println("   ✓ Memory usage is reasonable");
            } else {
                System.out.println("   ⚠ Warning: High memory growth rate");
            }

        } catch (Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ========================================================================
    // Test 3: File Growth
    // ========================================================================

    private static void testFileGrowth() {
        System.out.println("Test 3: File Growth Analysis");
        System.out.println("─".repeat(60));

        try {
            // Clean up first
            Files.deleteIfExists(Paths.get("diagnostic.txt"));

            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            sat.addObserver(new StatusObserver());
            sat.initialiseProbes();

            long[] fileSizes = new long[5];

            for (int i = 0; i < 5; i++) {
                int startSol = i * 20 + 1;
                int endSol = (i + 1) * 20;

                for (int sol = startSol; sol <= endSol; sol++) {
                    sat.processSol(sol);
                }

                File file = new File("diagnostic.txt");
                if (file.exists()) {
                    fileSizes[i] = file.length();
                    System.out.printf("   After %d sols: %d bytes (%.2f KB)\n",
                            endSol, fileSizes[i], fileSizes[i] / 1024.0);
                }
            }

            // Check if growth is roughly linear
            boolean linearGrowth = true;
            for (int i = 1; i < 4; i++) {
                long diff1 = fileSizes[i] - fileSizes[i-1];
                long diff2 = fileSizes[i+1] - fileSizes[i];
                double ratio = (double) diff2 / diff1;

                if (ratio < 0.5 || ratio > 2.0) {
                    linearGrowth = false;
                    break;
                }
            }

            if (linearGrowth) {
                System.out.println("   ✓ File growth is roughly linear (expected)");
            } else {
                System.out.println("   ⚠ Warning: File growth is not linear");
            }

            // Estimate size for 1000 sols
            long avgGrowthPer20 = (fileSizes[4] - fileSizes[0]) / 4;
            long estimatedFor1000 = fileSizes[0] + (avgGrowthPer20 * 50);
            System.out.printf("   Estimated size for 1000 sols: %.2f KB\n",
                    estimatedFor1000 / 1024.0);

        } catch (Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ========================================================================
    // Test 4: Random Seeds
    // ========================================================================

    private static void testRandomSeeds() {
        System.out.println("Test 4: Multiple Random Seeds");
        System.out.println("─".repeat(60));

        try {
            long[] seeds = {12345L, 67890L, 11111L, 99999L, 55555L};

            for (long seed : seeds) {
                CommsGenerator comms = new CommsGenerator(seed);
                ProbeFactory factory = new ProbeFactory();
                MarSciSat sat = new MarSciSat(comms, factory);

                sat.initialiseProbes();

                for (int sol = 1; sol <= 20; sol++) {
                    sat.processSol(sol);
                }

                System.out.printf("   ✓ Seed %d: Completed 20 sols successfully\n", seed);
            }

            System.out.println("   ✓ All seeds tested successfully");

        } catch (Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ========================================================================
    // Test 5: Concurrent Observers
    // ========================================================================

    private static void testConcurrentObservers() {
        System.out.println("Test 5: Multiple Observers Performance");
        System.out.println("─".repeat(60));

        try {
            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            // Add multiple observers
            for (int i = 0; i < 10; i++) {
                sat.addObserver(new TestObserver("Observer-" + i));
            }
            sat.addObserver(new StatusObserver());
            sat.addObserver(new HistoryObserver());

            sat.initialiseProbes();

            long startTime = System.currentTimeMillis();

            for (int sol = 1; sol <= 50; sol++) {
                sat.processSol(sol);
            }

            long elapsed = System.currentTimeMillis() - startTime;

            System.out.printf("   ✓ Processed 50 sols with 12 observers in %.2f seconds\n",
                    elapsed / 1000.0);
            System.out.printf("   Average: %.2f ms per sol\n", elapsed / 50.0);

            if (elapsed / 50.0 < 100) {
                System.out.println("   ✓ Performance is good");
            } else {
                System.out.println("   ⚠ Warning: Performance may be slow");
            }

        } catch (Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ========================================================================
    // Test 6: High Frequency Commands
    // ========================================================================

    private static void testHighFrequencyCommands() {
        System.out.println("Test 6: High Frequency Command Processing");
        System.out.println("─".repeat(60));

        try {
            CommsGenerator comms = new CommsGenerator(12345L);
            ProbeFactory factory = new ProbeFactory();
            MarSciSat sat = new MarSciSat(comms, factory);

            sat.initialiseProbes();

            long startTime = System.currentTimeMillis();
            int messageCount = 0;

            // Process many sols quickly
            for (int sol = 1; sol <= 200; sol++) {
                String msg;
                while ((msg = comms.nextMessage()) != null) {
                    messageCount++;
                    // Just count, don't process to test message generation speed
                }
            }

            long elapsed = System.currentTimeMillis() - startTime;

            System.out.printf("   Generated %d messages in %.2f seconds\n",
                    messageCount, elapsed / 1000.0);
            System.out.printf("   Average: %.2f messages per second\n",
                    messageCount / (elapsed / 1000.0));

            // Now actually process them
            comms = new CommsGenerator(12345L);
            sat = new MarSciSat(comms, factory);
            sat.initialiseProbes();

            startTime = System.currentTimeMillis();

            for (int sol = 1; sol <= 200; sol++) {
                sat.processSol(sol);
            }

            elapsed = System.currentTimeMillis() - startTime;

            System.out.printf("   Processed 200 sols in %.2f seconds\n", elapsed / 1000.0);
            System.out.printf("   Average: %.2f ms per sol\n", elapsed / 200.0);

            System.out.println("   ✓ High frequency processing successful");

        } catch (Exception e) {
            System.out.println("   ✗ FAILED: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println();
    }

    // ========================================================================
    // Helper Classes
    // ========================================================================

    private static class TestObserver implements Observer {
        private final String name;

        public TestObserver(String name) {
            this.name = name;
        }

        @Override
        public void onSolComplete(int sol, java.util.Collection<Probe> probes) {
            // Silent observer for testing
        }

        @Override
        public void onHistoryRequest(String probeName, java.util.Collection<Probe> probes) {
            // Silent observer for testing
        }
    }
}