package couch.joycouch.joycon;

import couch.joycouch.Rumble;
import couch.joycouch.handlers.JoyconInputReportHandler;
import purejavahidapi.HidDevice;

import java.util.ArrayList;
import java.util.List;

public class Joycon {
    private HidDevice device;
    private boolean isCombined = false;

    private int side;

    protected boolean rumbleOn = false;

    private List<JoyconInputReportHandler> inputReportHandlers = new ArrayList<>();

    protected int playerNumber;

    public Joycon(HidDevice device) {
        this.device = device;
        this.setPlayerLED();
        this.setInputReportMode();
        device.setInputReportListener(new JoyconHIDInputHandler(this));
        device.setDeviceRemovalListener(HidDevice::close);
    }

    public void setSide(int side){ this.side = side; }

    public void addInputReportHandler(JoyconInputReportHandler handler){ this.inputReportHandlers.add(handler); }
    public List<JoyconInputReportHandler> getInputReportHandlers(){ return this.inputReportHandlers; }

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
