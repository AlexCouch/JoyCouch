package couch.joycouch.io.input.hid;

import couch.joycouch.io.input.delegate.HandlerData;
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
        if(reportID == 0x3F) return;
        final HandlerData handlerData = new HandlerData(source, reportID, reportData, reportLength);
        this.joycon.getInputHandlerThread().addHIDInputHandler(handlerData);
    }
}
