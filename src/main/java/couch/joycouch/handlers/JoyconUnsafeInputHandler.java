package couch.joycouch.handlers;

import couch.joycouch.Joycon;
import purejavahidapi.HidDevice;

/**
 * This is for unsafe handling of input and output. Please know what you are doing.
 */
public interface JoyconUnsafeInputHandler {
    void handleUnsafeInput(HidDevice handle, Joycon source, byte[] inputData);
}
