package couch.joycouch.handlers;

import couch.joycouch.analog.AnalogStickPos;
import couch.joycouch.joycon.SingleJoycon;

public interface JoyconAnalogInputHandler {
    void handleAnalogInput(SingleJoycon source, AnalogStickPos analogStickPos);
}
