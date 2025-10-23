package edu.curtin.app;

import edu.curtin.app.probe.Drone;
import edu.curtin.app.probe.Location;
import edu.curtin.app.probe.Probe;
import edu.curtin.app.state.ProbeState;

/**
 * Entry point into the application. To change the package, and/or the name of this class, make
 * sure to update the 'mainClass = ...' line in build.gradle.
 */
public class App
{
    public static void main(String[] args) {
        Location location = new Location(-11.45, 45.56)
        Probe drone = new Drone("1", location);

        System.out.println(Hello.getHello());
    }
}
