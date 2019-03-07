package couch.joycontest;

import purejavahidapi.*;

public class Joycon {
    private HidDevice handle;
    private int side;
    private JoyconCodes currentButtonDown = null;
    private JoyconInputHandler inputHandler = null;
    private boolean rumbleOn = false;

    public Joycon(HidDevice handle, int side){
        this.handle = handle;
        this.side = side;
        this.handle.setInputReportListener((source, reportID, reportData, reportLength) -> {
            for(JoyconCodes code : JoyconCodes.values()){
                if(reportData[code.getReportIndex()] == code.getCode() && code.getSide() == this.side){
                    this.currentButtonDown = code;
                    inputHandler.handleInput(this.currentButtonDown);
                    break;
                }
            }
        });
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
            System.out.println();
            rumbleJoycon();
        });
    }

    public void rumbleJoycon(){
        if(!rumbleOn) enableRumble();
        byte[] buf = new byte[49];
        buf[0] = 0x10;
        byte[] rumbleData = collectRumbleData();
        System.arraycopy(rumbleData, 0, buf, 1, rumbleData.length);
        this.handle.setOutputReport((byte)0x10, buf, 50);
    }

    private void enableRumble(){
        byte[] buf = new byte[49];
        buf[0] = 0x1;
        buf[10] = 0x48;
        buf[11] = 0x1;
        this.handle.setOutputReport((byte)0x1, buf, buf.length);
        this.rumbleOn = true;
    }

    private static byte[] collectRumbleData(){
        byte[] buf = new byte[8];
        buf[0] = 0x0;
        buf[1] = 0x1;
        buf[2] = 0x40;
        buf[3] = 0x40;
        for(int i = 0; i < 4; ++i){
            buf[4 + i] = buf[i];
        }
        return buf;
    }

    public void setJoyconInputHandler(JoyconInputHandler inputHandler){
        this.inputHandler = inputHandler;
    }

    public boolean isButtonDown(JoyconCodes code, int side){
        return this.currentButtonDown != null && this.currentButtonDown.getCode() == code.getCode() && this.currentButtonDown.getSide() == side;
    }

    public JoyconCodes getButtonDown(){
        return this.currentButtonDown;
    }
}
