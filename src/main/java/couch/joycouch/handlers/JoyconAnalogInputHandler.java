package couch.joycouch.handlers;

import couch.joycouch.Joycon;
import couch.joycouch.analog.AnalogStickPos;

public interface JoyconAnalogInputHandler {
    void handleAnalogInput(Joycon source, AnalogStickPos analogStickPos);
}
