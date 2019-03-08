package couch.joycouch.joycon;

import couch.joycouch.Rumble;
import couch.joycouch.analog.AnalogStick;
import couch.joycouch.buttons.JoyconButtons;
import couch.joycouch.handlers.*;
import purejavahidapi.HidDevice;

public class SingleJoycon implements Joycon{
    private HidDevice device;
    private int side;
    private boolean isCombined = false;

    protected AnalogStick analogStick;
    protected JoyconButtons currentButtonDown = null;

    protected JoyconButtonInputHandler buttonInputHandler = null;
    protected JoyconAnalogInputHandler analogInputHandler = null;
    protected JoyconUnsafeInputHandler unsafeInputHandler = null;

    protected boolean rumbleOn = false;

    protected int playerNumber;

    public SingleJoycon(HidDevice device, int side, int playerNumber) {
        this.device = device;
        this.side = side;
        this.analogStick = new AnalogStick(this);
        this.playerNumber = playerNumber;
        device.setInputReportListener((source, reportID, reportData, reportLength) -> {
            this.analogStick.updatePos(reportData[3]);
            for(JoyconButtons code : JoyconButtons.values()){
                if(reportData[code.getReportIndex()] == code.getCode() && code.getSide() == this.side){
                    this.currentButtonDown = code;
                    break;
                }
            }
            if(buttonInputHandler != null) buttonInputHandler.handleButtonInput(this, this.currentButtonDown);
            if(analogInputHandler != null) analogInputHandler.handleAnalogInput(this, this.analogStick.getPos());
            if(unsafeInputHandler != null) unsafeInputHandler.handleUnsafeInput(this.device, this, reportData);
        });
    }

    public void setCombined(boolean isCombined){ this.isCombined = isCombined; }

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
        buf[0] = 0x1;
        buf[10] = 0x48;
        buf[11] = 0x1;
        this.device.setOutputReport((byte)0x1, buf, buf.length);
        this.rumbleOn = true;
    }

    public void rumbleJoycon(float lowFreq, float highFreq, float amplitude) {
        if(!rumbleOn) enableRumble();
        byte[] buf = new byte[49];
        buf[0] = 0x10;
        byte[] rumbleData = new Rumble(lowFreq, highFreq, amplitude).getRumbleData();
        System.arraycopy(rumbleData, 0, buf, 1, rumbleData.length);
        this.device.setOutputReport((byte)0x10, buf, 50);
    }

    public int getPlayerNumber(){
        return this.playerNumber;
    }

    public void setPlayerLED() {
        byte[] buf = new byte[49];
        buf[0] = 0x1;
        buf[10] = 0x30;
        buf[11] = (byte)this.playerNumber;
        this.device.setOutputReport(buf[10], buf, buf.length);
    }

    public void setJoyconUnsafeInputHandler(JoyconUnsafeInputHandler unsafeInputHandler) {
        this.unsafeInputHandler = unsafeInputHandler;
    }

    public void setJoyconButtonInputHandler(JoyconButtonInputHandler buttonInputHandler) {
        this.buttonInputHandler = buttonInputHandler;
    }

    public void setJoyconAnalogInputHandler(JoyconAnalogInputHandler analogInputHandler) {
        this.analogInputHandler = analogInputHandler;
    }
}
