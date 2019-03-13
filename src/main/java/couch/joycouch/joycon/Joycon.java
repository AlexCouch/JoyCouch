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

    /**
     * An integer representing the battery life of this JoyCon.
     *
     * 8 - Full
     * 6 - Medium
     * 4 - Low
     * 2 - Critical
     * 0 - Empty
     */
    private int batteryLife = -1;

    protected int playerNumber;

    public Joycon(HidDevice device) {
        this.device = device;
        this.setPlayerLED();
        this.setInputReportMode();
        device.setInputReportListener(new JoyconHIDInputHandler(this));
        device.setDeviceRemovalListener(HidDevice::close);
    }

    public void setSide(int side){ this.side = side; }

    public void setBatteryLife(int batteryLife){ this.batteryLife = batteryLife; }

    public int getBatteryLife(){ return this.batteryLife; }

    public void addInputReportHandler(JoyconInputReportHandler handler){ this.inputReportHandlers.add(handler); }
    public List<JoyconInputReportHandler> getInputReportHandlers(){ return this.inputReportHandlers; }

    private void setInputReportMode(){
        JoyconOutputReportFactory.INSTANCE
                .setOutputReportID((byte)0x01)
                .setSubcommandID((byte)0x03)
                .setSubcommandArg((byte)0x30)
                .sendTo(this);
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
        JoyconOutputReportFactory.INSTANCE
                .setOutputReportID((byte)0x01)
                .setSubcommandID((byte)0x48)
                .setSubcommandArg((byte)0x1)
                .sendTo(this);
        this.rumbleOn = true;
    }

    public void rumbleJoycon() {
        if(!rumbleOn) enableRumble();
        JoyconOutputReportFactory.INSTANCE
                .setOutputReportID((byte)0x10)
                .setOutoutReportData(new Rumble(160f, 320f, 0.6f).getRumbleData())
                .sendTo(this);
    }

    public int getPlayerNumber(){
        return this.playerNumber;
    }

    public void setPlayerLED() {
        JoyconOutputReportFactory.INSTANCE
                .setOutputReportID((byte)0x01)
                .setSubcommandID((byte)0x30)
                .setSubcommandArg((byte)0x1)
                .sendTo(this);
    }
}
