package edu.curtin.app.communication;

import edu.curtin.app.simulation.MarSciSat;

public interface Message {
    void readMessage(MarSciSat sat);
    String getMsgType();
}
