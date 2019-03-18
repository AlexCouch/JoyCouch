package couch.joycouch.handlers;

import couch.joycouch.JoyconManager;
import couch.joycouch.analog.AnalogStickStatus;
import couch.joycouch.buttons.ButtonStatus;
import couch.joycouch.io.input.JoyconInputReport;
import couch.joycouch.io.input.hid.JoyconHIDInputHandler;
import couch.joycouch.joycon.Joycon;

public class JoyconFullInputReportHandler implements JoyconHIDInputHandler {
    @Override
    public byte getReportID() {
        return 0x30;
    }

    @Override
    public void handleInput(Joycon source, byte[] reportData) {
        int timer = reportData[1];
        if(timer < 0) timer = -timer;
        if(timer % JoyconManager.INSTANCE.getInputFrequency() == 0) {
            ButtonStatus buttonStatus = new ButtonStatus(reportData[3], reportData[4], reportData[5]);
            byte[] stickData = new byte[3];
            if(source.getSide() == 2){
                stickData[0] = reportData[6];
                stickData[1] = reportData[7];
                stickData[2] = reportData[8];
            }else if(source.getSide() == 0){
                stickData[0] = reportData[9];
                stickData[1] = reportData[10];
                stickData[2] = reportData[11];
            }else{
                return;
            }
            AnalogStickStatus analogStickStatus = new AnalogStickStatus(
                    source.getStickCalibrator(),
                    stickData[0],
                    stickData[1],
                    stickData[2]);
            JoyconInputReport inputReport = new JoyconInputReport(source);
            inputReport.setButtonStatus(buttonStatus);
            inputReport.setAnalogStickStatus(analogStickStatus);
            source.getInputReportHandlers().forEach(h -> h.handleInputReport(inputReport));
        }
    }
}
