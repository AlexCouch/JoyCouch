package couch.joycouch.handlers;

import couch.joycouch.joycon.JoyconInputReport;

public interface JoyconInputReportHandler {
    void handleInputReport(JoyconInputReport report);
}
