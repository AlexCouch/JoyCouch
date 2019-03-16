package couch.joycouch.joycon;

import couch.joycouch.JoyconManager;
import couch.joycouch.Rumble;
import couch.joycouch.analog.AnalogStickCalibrator;
import couch.joycouch.io.input.JoyconInputHandlerThread;
import couch.joycouch.io.input.JoyconInputReportHandler;
import couch.joycouch.io.input.hid.*;
import couch.joycouch.io.output.JoyconOutputReportFactory;
import couch.joycouch.spi.MemoryManager;
import couch.joycouch.spi.SPIMemory;
import purejavahidapi.HidDevice;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class is a wrapper that contains all the information for working with a JoyCon. It includes the HidDevice,<br>
 * the side (left, right, or shared), methods for sending and receiving data which are really handled by the input report
 * handlers. This JoyCon class contains a list of {@link JoyconInputReportHandler JoyconInputReportHandlers} which are all<br>
 * invoked by the {@link HIDInputReportHandler JoyCon's HID input report handler}.
 */
public class Joycon extends Thread{
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
    private Queue<ActionRequest> joyconActonRequest = new ConcurrentLinkedQueue<>();

    public interface ActionRequest{

        void requestAction(Joycon joycon);
    }
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

    private HIDInputReportHandler hidInputReportHandler = new HIDInputReportHandler(this);
    private AnalogStickCalibrator calibrator = null;

    private JoyconInputHandlerThread inputHandlerThread = new JoyconInputHandlerThread();
    private MemoryManager memoryManager = new MemoryManager(this);
    private boolean running = false;

    public Joycon(HidDevice device) {
        this.device = device;
    }

    @Override
    public void run() {
        JoyconManager.LOGGER.debug("Initializing JoyCon...");
        device.setInputReportListener(hidInputReportHandler);
        inputHandlerThread.start();
        createStickCalibrator();
        this.setPlayerLED();
        this.setInputReportMode();
        running = true;
        while(running){
            ActionRequest request = this.joyconActonRequest.poll();
            if(request != null){
                request.requestAction(this);
            }
        }
    }

    public void shutdown(){
        this.reset();
        running = false;
    }

    public JoyconInputHandlerThread getInputHandlerThread(){ return this.inputHandlerThread; }

    public MemoryManager getMemoryManager(){ return this.memoryManager; }

    public synchronized void reset(){
        JoyconOutputReportFactory.INSTANCE
                .setOutputReportID((byte)0x01)
                .setSubcommandID((byte)0x03)
                .setSubcommandArg((byte)0x3F)
                .sendTo(this);
    }

    public AnalogStickCalibrator getStickCalibrator(){
        return this.calibrator;
    }

    private synchronized void createStickCalibrator(){
        JoyconManager.LOGGER.debug("Creating analog stick calibrator...");
        if (this.side == 0) { //Right
            SPIMemory calMem = this.memoryManager.readMemory((byte)0x60, (byte)0x46, (byte)9);
            SPIMemory devParamMem = this.memoryManager.readMemory((byte)0x60, (byte)0x98, (byte)16);
            this.calibrator = new AnalogStickCalibrator(
                    this.side,
                    calMem,
                    devParamMem
            );
        } else if (this.side == 1) { //Left
            SPIMemory calMem = this.memoryManager.readMemory((byte)0x60, (byte)0x3D, (byte)9);
            SPIMemory devParamMem = this.memoryManager.readMemory((byte)0x60, (byte)0x86, (byte)16);
            this.calibrator = new AnalogStickCalibrator(
                    this.side,
                    calMem,
                    devParamMem
            );
        }
        JoyconManager.LOGGER.debug("\tDone!");
    }

    public void setSide(int side){ this.side = side; }

    public void setBatteryLife(int batteryLife){ this.batteryLife = batteryLife; }

    public int getBatteryLife(){ return this.batteryLife; }

    public void addInputReportHandler(JoyconInputReportHandler handler){ this.inputReportHandlers.add(handler); }
    public void addHIDInputReportHandler(JoyconHIDInputHandler inputHandler){ this.hidInputReportHandlers.add(inputHandler); }
    public void addHIDSubcommandInputHandler(JoyconHIDSubcommandInputHandler inputHandler){ this.hidSubcommandInputHandlers.add(inputHandler); }

    public List<JoyconInputReportHandler> getInputReportHandlers(){ return this.inputReportHandlers; }
    public List<JoyconHIDSubcommandInputHandler> getHidSubcommandInputHandlers(){ return this.hidSubcommandInputHandlers; }
    public List<JoyconHIDInputHandler> getHidInputReportHandlers(){ return this.hidInputReportHandlers; }

    public void requestAction(ActionRequest actionRequest){
        this.joyconActonRequest.add(actionRequest);
    }

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
