package couch.joycouch.io.input.hid;

import couch.joycouch.joycon.Joycon;

public interface JoyconHIDSubcommandInputHandler {
    byte getSubcommandID();
    void handleSubcommandInput(Joycon joycon, byte[] subcommandInput);
}
