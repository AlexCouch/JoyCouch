package couch.joycouch.io.input.hid;

import couch.joycouch.joycon.Joycon;
import purejavahidapi.HidDevice;
import purejavahidapi.InputReportListener;

public class HIDInputReportHandler implements InputReportListener {

    private Joycon joycon;

    public HIDInputReportHandler(Joycon joycon){
        this.joycon = joycon;
    }

    @Override
    public void onInputReport(HidDevice source, byte reportID, byte[] reportData, int reportLength) {
        this.joycon.getInputHandlerThread().handleDelegatedInput(source, reportID, reportData, reportLength);
    }
}
