package couch.joycontest;

import purejavahidapi.*;

public class Joycon {
    private HidDevice handle;
    private int side;
    private JoyconCodes currentButtonDown = null;
    private int[] currentStickPos = new int[]{ 8, 6 };
    private JoyconInputHandler inputHandler = null;
    private JoyconUnsafeInputHandler unsafeInputHandler = null;
    private boolean rumbleOn = false;

    public Joycon(HidDevice handle, int side){
        this.handle = handle;
        this.side = side;
        this.handle.setInputReportListener((source, reportID, reportData, reportLength) -> {
            for(JoyconCodes code : JoyconCodes.values()){
                if(reportData[code.getReportIndex()] == code.getCode() && code.getSide() == this.side){
                    this.currentButtonDown = code;
                    if(inputHandler != null) inputHandler.handleInput(this, this.currentButtonDown);
                    break;
                }
            }
            if(unsafeInputHandler != null) unsafeInputHandler.handleUnsafeInput(this, reportData);
        });
    }

    public void detach(){
        this.handle.close();
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

    public void setJoyconUnsafeInputHandler(JoyconUnsafeInputHandler unsafeInputHandler){
        this.unsafeInputHandler = unsafeInputHandler;
    }

    public void setJoyconInputHandler(JoyconInputHandler inputHandler){
        this.inputHandler = inputHandler;
    }
}
