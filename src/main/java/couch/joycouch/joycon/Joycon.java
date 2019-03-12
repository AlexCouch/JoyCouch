package couch.joycouch.joycon;

import couch.joycouch.Rumble;
import couch.joycouch.handlers.JoyconInputReportHandler;
import purejavahidapi.HidDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a wrapper that contains all the information for working with a JoyCon. It includes the HidDevice,<br>
 * the side (left, right, or shared), methods for sending and receiving data which are really handled by the input report
 * handlers. This JoyCon class contains a list of {@link JoyconInputReportHandler JoyconInputReportHandlers} which are all<br>
 * invoked by the {@link JoyconHIDInputHandler JoyCon's HID input report handler}.
 */
public class Joycon {
    /**
     * The HID device allowing communication with the JoyCons. This is how input and output reports are received/sent.
     */
    private HidDevice device;
    private boolean isCombined = false;

    /**
     * This is an offset for the input reports to get the right button status data.
     * <br><br>
     * {@code
     *  reportData[3 + side]
     * }
     *
     * 0 = right
     * 1 = shared
     * 2 = left
     */
    private int side;

    /**
     * Just a check for whether rumble is enabled. Not guaranteed to actually represent rumble status.
     */
    protected boolean rumbleOn = false;

    /**
     * A list of all input report handlers.
     */
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
