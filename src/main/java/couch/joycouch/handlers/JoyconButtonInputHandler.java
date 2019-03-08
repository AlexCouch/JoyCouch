package couch.joycouch.handlers;

import couch.joycouch.buttons.JoyconButtons;
import couch.joycouch.joycon.SingleJoycon;

public interface JoyconButtonInputHandler {
    void handleButtonInput(SingleJoycon source, JoyconButtons button);
}
