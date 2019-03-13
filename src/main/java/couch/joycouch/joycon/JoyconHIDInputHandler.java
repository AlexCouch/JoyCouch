package couch.joycouch.joycon;

import couch.joycouch.JoyconManager;
import couch.joycouch.analog.AnalogStickStatus;
import couch.joycouch.buttons.ButtonStatus;
import purejavahidapi.HidDevice;
import purejavahidapi.InputReportListener;

public class JoyconHIDInputHandler implements InputReportListener {

    private Joycon joycon;

    public JoyconHIDInputHandler(Joycon joycon){
        this.joycon = joycon;
    }

    @Override
    public void onInputReport(HidDevice source, byte reportID, byte[] reportData, int reportLength) {
        if(reportID == 48){
            this.joycon.setBatteryLife(((reportData[2] & 0xF0) >> 4));
            int timer = reportData[1];
            if(timer < 0) timer = -timer;
            if(timer <= JoyconManager.INSTANCE.getInputFrequency()) {
                ButtonStatus buttonStatus = new ButtonStatus(reportData[3], reportData[4], reportData[5]);
                AnalogStickStatus analogStickStatus = new AnalogStickStatus(
                        reportData[6],
                        reportData[7],
                        reportData[8],
                        reportData[9],
                        reportData[10],
                        reportData[11]);
                JoyconInputReport inputReport = new JoyconInputReport(this.joycon);
                inputReport.setButtonStatus(buttonStatus);
                inputReport.setAnalogStickStatus(analogStickStatus);
                this.joycon.getInputReportHandlers().forEach(h -> h.handleInputReport(inputReport));
            }
        }
    }
}
