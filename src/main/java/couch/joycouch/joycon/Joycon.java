package couch.joycouch.joycon;

import couch.joycouch.Rumble;
import couch.joycouch.analog.AnalogStickCalibrator;
import couch.joycouch.io.input.JoyconInputHandlerThread;
import couch.joycouch.io.input.JoyconInputReportHandler;
import couch.joycouch.io.input.hid.*;
import couch.joycouch.io.output.JoyconOutputReportFactory;
import couch.joycouch.spi.SPIMemory;
import purejavahidapi.HidDevice;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a wrapper that contains all the information for working with a JoyCon. It includes the HidDevice,<br>
 * the side (left, right, or shared), methods for sending and receiving data which are really handled by the input report
 * handlers. This JoyCon class contains a list of {@link JoyconInputReportHandler JoyconInputReportHandlers} which are all<br>
 * invoked by the {@link HIDInputReportHandler JoyCon's HID input report handler}.
 */
public class Joycon {
    public synchronized Joycon getInstance(){
        return this;
    }

    /**
     * The HID device allowing communication with the JoyCons. This is how input and io reports are received/sent.
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
    private List<JoyconHIDInputHandler> hidInputReportHandlers = new ArrayList<>();
    private List<JoyconHIDSubcommandInputHandler> hidSubcommandInputHandlers = new ArrayList<>();

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

    private List<SPIMemory> memoryCaches = new ArrayList<>();
    private HIDInputReportHandler hidInputReportHandler = new HIDInputReportHandler(this);

    private AnalogStickCalibrator calibrator = null;
    private JoyconInputHandlerThread inputHandlerThread = new JoyconInputHandlerThread(this);

    public Joycon(HidDevice device) {
        this.device = device;
    }

    public void init(){
        reset();
        device.setInputReportListener(hidInputReportHandler);
        inputHandlerThread.start();
        createStickCalibrator();
        this.setPlayerLED();
        this.setInputReportMode();
    }

    public JoyconInputHandlerThread getInputHandlerThread(){ return this.inputHandlerThread; }

    private void reset(){
        JoyconOutputReportFactory.INSTANCE.setOutputReportID((byte)0x01).setSubcommandID((byte)0x03).setSubcommandArg((byte)0x3F).sendTo(this);
    }

    public AnalogStickCalibrator getStickCalibrator(){
        return this.calibrator;
    }

    private synchronized void createStickCalibrator(){
        if (this.side == 0) {
            byte[] leftAddress = new byte[]{(byte) 0x60, (byte) 0x3D};
            byte[] leftDevParams = new byte[]{(byte) 0x60, (byte) 0x86};
            JoyconOutputReportFactory.INSTANCE.setOutputReportID((byte) 0x01).setSubcommandID((byte) 0x10).setSubcommandArgs(leftAddress).sendTo(this);
            JoyconOutputReportFactory.INSTANCE.setOutputReportID((byte) 0x01).setSubcommandID((byte) 0x10).setSubcommandArgs(leftDevParams).sendTo(this);
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.calibrator = new AnalogStickCalibrator(
                    this.side,
                    this.getMemoryReader(leftAddress),
                    this.getMemoryReader(leftDevParams)
            );
        } else if (this.side == 1) {
            byte[] rightAddress = new byte[]{(byte) 0x60, (byte) 0x46};
            byte[] rightDevParams = new byte[]{(byte) 0x60, (byte) 0x98};
            JoyconOutputReportFactory.INSTANCE.setOutputReportID((byte) 0x01).setSubcommandID((byte) 0x10).setSubcommandArgs(rightAddress).sendTo(this);
            JoyconOutputReportFactory.INSTANCE.setOutputReportID((byte) 0x01).setSubcommandID((byte) 0x10).setSubcommandArgs(rightDevParams).sendTo(this);
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.calibrator = new AnalogStickCalibrator(
                    this.side,
                    this.getMemoryReader(rightAddress),
                    this.getMemoryReader(rightDevParams)
            );
        }
    }

    public void addMemoryCache(SPIMemory memory){ this.memoryCaches.add(memory); }
    public SPIMemory getMemoryReader(byte[] address){
        for(SPIMemory reader : memoryCaches){
            if(reader.checkAddress(address)){
                return reader;
            }
        }
        return null;
    }
    public List<SPIMemory> getMemoryCaches(){ return this.memoryCaches; }

    public void setSide(int side){ this.side = side; }

    public void setBatteryLife(int batteryLife){ this.batteryLife = batteryLife; }

    public int getBatteryLife(){ return this.batteryLife; }

    public void addInputReportHandler(JoyconInputReportHandler handler){ this.inputReportHandlers.add(handler); }
    public void addHIDInputReportHandler(JoyconHIDInputHandler inputHandler){ this.hidInputReportHandlers.add(inputHandler); }
    public void addHIDSubcommandInputHandler(JoyconHIDSubcommandInputHandler inputHandler){ this.hidSubcommandInputHandlers.add(inputHandler); }

    public List<JoyconInputReportHandler> getInputReportHandlers(){ return this.inputReportHandlers; }
    public List<JoyconHIDSubcommandInputHandler> getHidSubcommandInputHandlers(){ return this.hidSubcommandInputHandlers; }
    public List<JoyconHIDInputHandler> getHidInputReportHandlers(){ return this.hidInputReportHandlers; }

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
