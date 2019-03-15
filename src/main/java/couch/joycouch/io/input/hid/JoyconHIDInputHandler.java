package couch.joycouch.io.input.hid;

import couch.joycouch.joycon.Joycon;

public interface JoyconHIDInputHandler {
   byte getReportID();
   void handleInput(Joycon source, byte[] reportData);
}
