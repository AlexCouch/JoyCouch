package couch.joycouch.handlers;

import couch.joycouch.joycon.SingleJoycon;
import purejavahidapi.HidDevice;

/**
 * This is for unsafe handling of input and output. Please know what you are doing.
 */
public interface JoyconUnsafeInputHandler {
    void handleUnsafeInput(HidDevice handle, SingleJoycon source, byte[] inputData);
}
