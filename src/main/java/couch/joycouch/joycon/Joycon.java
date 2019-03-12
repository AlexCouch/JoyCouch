package couch.joycouch.joycon;

import couch.joycouch.Rumble;
import couch.joycouch.buttons.JoyconButtons;
import couch.joycouch.handlers.JoyconInputReportHandler;
import purejavahidapi.HidDevice;

public class Joycon {
    private HidDevice device;
    private boolean isCombined = false;

    protected int side;

    protected boolean rumbleOn = false;

    private JoyconInputReportHandler inputReportHandler = null;

    protected int playerNumber;

    public Joycon(HidDevice device, int side) {
        this.device = device;
        this.side = side;
        this.setPlayerLED();
        this.setInputReportMode();
        device.setInputReportListener((source, reportID, reportData, reportLength) -> {
            if(reportID == 48){
                if(reportData[3 + this.side] != 0){
                    JoyconInputReport inputReport = new JoyconInputReport(this);
                    inputReport.setButtonStatus(JoyconButtons.getButtonFromInt(reportData[3 + this.side]));
                    if(inputReportHandler != null) inputReportHandler.handleInputReport(inputReport);
                }
            }
        });
        device.setDeviceRemovalListener(HidDevice::close);
    }

    public void setInputReportHandler(JoyconInputReportHandler handler){ this.inputReportHandler = handler; }
    public JoyconInputReportHandler getInputReportHandler(){ return this.inputReportHandler; }

    private void setInputReportMode(){
        byte[] buf = new byte[50];
        buf[0] = 0x01;
        buf[10] = 0x03;
        buf[11] = 0x30;
        this.device.setOutputReport(buf[0], buf, buf.length);
    }

    public boolean isCombined(){ return this.isCombined; }

    public HidDevice getDevice() {
        return this.device;
    }

    public int getSide(){ return this.side; }

    public void detach() {
        this.device.close();
    }

    public void enableRumble() {
        byte[] buf = new byte[49];
        buf[0] = 0x01;
        buf[10] = 0x48;
        buf[11] = 0x1;
        this.device.setOutputReport(buf[0], buf, buf.length);
        this.rumbleOn = true;
    }

    public void rumbleJoycon() {
        if(!rumbleOn) enableRumble();
        byte[] buf = new byte[49];
        buf[0] = 0x10;
        byte[] rumbleData = new Rumble(160f, 320f, 0.6f).getRumbleData();
        System.arraycopy(rumbleData, 0, buf, 1, rumbleData.length);
        this.device.setOutputReport((byte)0x10, buf, 50);
    }

    public int getPlayerNumber(){
        return this.playerNumber;
    }

    public void setPlayerLED() {
        byte[] buf = new byte[49];
        buf[0] = 0x01;
        buf[10] = 0x30;
        buf[11] = (byte)2;
        this.device.setOutputReport(buf[10], buf, buf.length);
    }
}
