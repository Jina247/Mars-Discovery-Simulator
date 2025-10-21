package edu.curtin.app.probe;

public interface ProbeState {
    void lowPowerMode();
    void move();
    void measure();
}