package couch.joycouch.joycon;

import couch.joycouch.JoyconManager;
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
    private JoyconInputHandlerThread inputHandlerThread = new JoyconInputHandlerThread();

    public Joycon(HidDevice device) {
        this.device = device;
    }

    public void init(){
        JoyconManager.LOGGER.debug("Initializing JoyCon...");
        device.setInputReportListener(hidInputReportHandler);
        inputHandlerThread.start();
        reset();
        createStickCalibrator();
        this.setPlayerLED();
        this.setInputReportMode();
    }

    public JoyconInputHandlerThread getInputHandlerThread(){ return this.inputHandlerThread; }

    private synchronized void reset(){
        try{
            JoyconOutputReportFactory.INSTANCE
                    .setOutputReportID((byte)0x01)
                    .setSubcommandID((byte)0x03)
                    .setSubcommandArg((byte)0x3F)
                    .sendTo(this);
            this.wait();
        }catch(InterruptedException e){
            JoyconManager.LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public AnalogStickCalibrator getStickCalibrator(){
        return this.calibrator;
    }

    private synchronized void createStickCalibrator(){
        JoyconManager.LOGGER.debug("Creating analog stick calibrator...");
        if (this.side == 0) { //Right
            try {
                byte[] leftAddress = new byte[]{(byte) 0x60, (byte) 0x3D};
                byte[] leftDevParams = new byte[]{(byte) 0x60, (byte) 0x86};
                JoyconOutputReportFactory.INSTANCE.setOutputReportID((byte) 0x01).setSubcommandID((byte) 0x10).setSubcommandArgs(leftAddress).sendTo(this);
                JoyconManager.LOGGER.debug("Waiting for calibration data...");
                this.wait();
                JoyconManager.LOGGER.debug("About to send output report with id {} and subcommand id {}", 0x01, 0x10);
                JoyconOutputReportFactory.INSTANCE.setOutputReportID((byte) 0x01).setSubcommandID((byte) 0x10).setSubcommandArgs(leftDevParams).sendTo(this);
                JoyconManager.LOGGER.debug("Waiting for device parameters data...");
                this.wait();
                JoyconManager.LOGGER.debug("Calibration data received...creating calibrator...");
                this.calibrator = new AnalogStickCalibrator(
                        this.side,
                        this.getMemoryReader(leftAddress),
                        this.getMemoryReader(leftDevParams)
                );
            } catch (InterruptedException e) {
                JoyconManager.LOGGER.error(e.getMessage());
                e.printStackTrace();
            }
        } else if (this.side == 1) { //Left
            try {
                byte[] rightAddress = new byte[]{(byte) 0x60, (byte) 0x46};
                byte[] rightDevParams = new byte[]{(byte) 0x60, (byte) 0x98};
                JoyconOutputReportFactory.INSTANCE.setOutputReportID((byte) 0x01).setSubcommandID((byte) 0x10).setSubcommandArgs(rightAddress).sendTo(this);
                JoyconOutputReportFactory.INSTANCE.setOutputReportID((byte) 0x01).setSubcommandID((byte) 0x10).setSubcommandArgs(rightDevParams).sendTo(this);
                this.wait();
                this.calibrator = new AnalogStickCalibrator(
                        this.side,
                        this.getMemoryReader(rightAddress),
                        this.getMemoryReader(rightDevParams)
                );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        JoyconManager.LOGGER.debug("\tDone!");
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

    private synchronized void setInputReportMode(){
        try {
            JoyconOutputReportFactory.INSTANCE
                    .setOutputReportID((byte) 0x01)
                    .setSubcommandID((byte) 0x03)
                    .setSubcommandArg((byte) 0x30)
                    .sendTo(this);
            this.wait();
        }catch(InterruptedException e){
            JoyconManager.LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isCombined(){ return this.isCombined; }

    public HidDevice getDevice() {
        return this.device;
    }

    public int getSide(){ return this.side; }

    public void detach() {
        this.device.close();
    }

    public synchronized void enableRumble() {
        try{
            JoyconOutputReportFactory.INSTANCE
                    .setOutputReportID((byte)0x01)
                    .setSubcommandID((byte)0x48)
                    .setSubcommandArg((byte)0x1)
                    .sendTo(this);
            this.wait();
            this.rumbleOn = true; //Don't set this to true until the reply input report comes back
        }catch(InterruptedException e){
            JoyconManager.LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public void rumbleJoycon() { //Does the rumble command have a response?
        if(!rumbleOn) enableRumble();
        JoyconOutputReportFactory.INSTANCE
                .setOutputReportID((byte)0x10)
                .setOutoutReportData(new Rumble(160f, 320f, 0.6f).getRumbleData())
                .sendTo(this);
        /*try{
            this.wait();
        }catch(InterruptedException e){
            JoyconManager.LOGGER.error(e.getMessage());
            e.printStackTrace();
        }*/
    }

    public int getPlayerNumber(){
        return this.playerNumber;
    }

    public synchronized void setPlayerLED() {
        try {
            JoyconOutputReportFactory.INSTANCE
                    .setOutputReportID((byte) 0x01)
                    .setSubcommandID((byte) 0x30)
                    .setSubcommandArg((byte) 0x1)
                    .sendTo(this);
            this.wait();
        }catch(InterruptedException e){
            JoyconManager.LOGGER.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
