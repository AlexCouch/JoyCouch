package couch.joycouch.handlers;

import couch.joycouch.Joycon;
import couch.joycouch.buttons.JoyconButtons;

public interface JoyconButtonInputHandler {
    void handleButtonInput(Joycon source, JoyconButtons button);
}
