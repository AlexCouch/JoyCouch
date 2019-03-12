package couch.joycouch.joycon;

import couch.joycouch.JoyconManager;
import couch.joycouch.buttons.JoyconButtons;
import purejavahidapi.HidDevice;
import purejavahidapi.InputReportListener;

public class JoyconHIDInputHandler implements InputReportListener {

    private Joycon joycon;
    private int lastInputSent = -255;

    public JoyconHIDInputHandler(Joycon joycon){
        this.joycon = joycon;
    }

    @Override
    public void onInputReport(HidDevice source, byte reportID, byte[] reportData, int reportLength) {
        if(joycon.getSide() == 0) return;
        if(reportID == 48){
            if(enoughTimeElapsed(reportData[1]) || lastInputSent != -255) {
                lastInputSent = reportData[1];
                if (reportData[3 + this.joycon.getSide()] != 0) {
                    JoyconInputReport inputReport = new JoyconInputReport(this.joycon);
                    inputReport.setButtonStatus(JoyconButtons.getButton(reportData[3 + this.joycon.getSide()], this.joycon.getSide()));
                    this.joycon.getInputReportHandlers().forEach(h -> h.handleInputReport(inputReport));
                }
            }
        }
    }

    private boolean enoughTimeElapsed(byte time){
        if(this.lastInputSent > time){
            return this.lastInputSent - time > JoyconManager.INSTANCE.getInputFrequency();
        }else if(this.lastInputSent < time){
            return time - this.lastInputSent > JoyconManager.INSTANCE.getInputFrequency();
        }else{
            return false;
        }
    }
}
