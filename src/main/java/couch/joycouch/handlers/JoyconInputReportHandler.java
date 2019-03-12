package couch.joycouch.handlers;

import couch.joycouch.joycon.JoyconInputReport;

/**
 * This interface must be implemented for JoyCon input handling. This is called upon input being received by the<br>
 * HID device representing the JoyCon. The input data is abstracted into a {@link JoyconInputReport} which contains all<br>
 * the necessary information needed to handle input.
 *
 * @author Alex Couch
 * @version 0.0.1
 * @see JoyconInputReport
 *
 */
public interface JoyconInputReportHandler {
    void handleInputReport(JoyconInputReport report);
}
