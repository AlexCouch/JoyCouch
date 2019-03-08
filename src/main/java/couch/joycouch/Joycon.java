package couch.joycouch;

import couch.joycouch.analog.AnalogStick;
import couch.joycouch.buttons.JoyconButtons;
import couch.joycouch.handlers.*;
import purejavahidapi.HidDevice;

public class Joycon {
    private HidDevice handle;
    private int side;

    private boolean shared = false;
    private Joycon sharedJc = null;

    private AnalogStick analogStick;
    private JoyconButtons currentButtonDown = null;

    private JoyconButtonInputHandler buttonInputHandler = null;
    private JoyconAnalogInputHandler analogInputHandler = null;
    private JoyconUnsafeInputHandler unsafeInputHandler = null;

    private boolean rumbleOn = false;

    public Joycon(HidDevice handle, int side){
        this.handle = handle;
        this.side = side;
        this.analogStick = new AnalogStick(this);
        this.handle.setInputReportListener((source, reportID, reportData, reportLength) -> {
            this.analogStick.updatePos(reportData[3]);
            for(JoyconButtons code : JoyconButtons.values()){
                if(reportData[code.getReportIndex()] == code.getCode() && code.getSide() == this.side){
                    this.currentButtonDown = code;
                    break;
                }
            }
            if(buttonInputHandler != null) buttonInputHandler.handleButtonInput(this, this.currentButtonDown);
            if(analogInputHandler != null) analogInputHandler.handleAnalogInput(this, this.analogStick.getPos());
            if(unsafeInputHandler != null) unsafeInputHandler.handleUnsafeInput(this.handle, this, reportData);
        });
    }

    public void detach(){
        this.handle.close();
    }

    public boolean isShared(){ return this.shared; }

    public Joycon getSharedJoycon(){ return this.sharedJc; }

    public void addSharedJoycon(Joycon sharedJc){
        this.shared = true;
        this.sharedJc = sharedJc;
    }

    /**
     * This is just for retrieving button information for button mappings.
     */
    public void testInput(){
        this.handle.setInputReportListener((source, reportID, reportData, reportLength) -> {
            int buttons1 = reportData[1];
            int buttons2 = reportData[2];
            System.out.println("Buttons1: " + buttons1);
            System.out.println("Buttons2: " + buttons2);
            System.out.println("Stick pos: " + reportData[3]);
            System.out.println();
            rumbleJoycon(160, 320, 0);
        });
    }

    public void rumbleJoycon(float lowFreq, float highFreq, float amplitude){
        if(!rumbleOn) enableRumble();
        byte[] buf = new byte[49];
        buf[0] = 0x10;
        byte[] rumbleData = new Rumble(lowFreq, highFreq, amplitude).getRumbleData();
        System.arraycopy(rumbleData, 0, buf, 1, rumbleData.length);
        this.handle.setOutputReport((byte)0x10, buf, 50);
    }

    public void enableRumble(){
        byte[] buf = new byte[49];
        buf[0] = 0x1;
        buf[10] = 0x48;
        buf[11] = 0x1;
        this.handle.setOutputReport((byte)0x1, buf, buf.length);
        this.rumbleOn = true;
    }

    public int getSide(){
        return this.side;
    }

    public void setJoyconUnsafeInputHandler(JoyconUnsafeInputHandler unsafeInputHandler){
        this.unsafeInputHandler = unsafeInputHandler;
    }

    public void setJoyconButtonInputHandler(JoyconButtonInputHandler buttonInputHandler){
        this.buttonInputHandler = buttonInputHandler;
    }

    public void setJoyconAnalogInputHandler(JoyconAnalogInputHandler analogInputHandler){
        this.analogInputHandler = analogInputHandler;
    }
}
