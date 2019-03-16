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

public class Joycon extends Thread{


    /*
                        Properties
     */
    private HidDevice device;

    private int side;
    protected boolean rumbleOn = false;
    private int batteryLife = -1;
    protected int playerNumber;
    private AnalogStickCalibrator calibrator = null;
    private boolean running = false;

    /*
                        Handlers
     */
    private List<JoyconInputReportHandler> inputReportHandlers = new ArrayList<>();

    private List<JoyconHIDInputHandler> hidInputReportHandlers = new ArrayList<>();
    private List<JoyconHIDSubcommandInputHandler> hidSubcommandInputHandlers = new ArrayList<>();
    private Queue<ActionRequest> joyconActonRequest = new ConcurrentLinkedQueue<>();
    private HIDInputReportHandler hidInputReportHandler = new HIDInputReportHandler(this);

    /*
                        Misc
     */
    private JoyconInputHandlerThread inputHandlerThread = new JoyconInputHandlerThread();

    private MemoryManager memoryManager = new MemoryManager(this);
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


    /*
                            Startup/Shutdown
     */
    public void shutdown(){
        this.reset();
        running = false;
    }

    public synchronized void reset(){
        JoyconOutputReportFactory.INSTANCE
                .setOutputReportID((byte)0x01)
                .setSubcommandID((byte)0x03)
                .setSubcommandArg((byte)0x3F)
                .sendTo(this);
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



    /*
                            Getters/Setters/Adders
     */

    //Handler adders
    public void addInputReportHandler(JoyconInputReportHandler handler){ this.inputReportHandlers.add(handler); }
    public void addHIDInputReportHandler(JoyconHIDInputHandler inputHandler){ this.hidInputReportHandlers.add(inputHandler); }
    public void addHIDSubcommandInputHandler(JoyconHIDSubcommandInputHandler inputHandler){ this.hidSubcommandInputHandlers.add(inputHandler); }

    //Property setters
    public void setSide(int side){ this.side = side; }
    public void setBatteryLife(int batteryLife){ this.batteryLife = batteryLife; }

    //Property getters
    public int getBatteryLife(){ return this.batteryLife; }
    public HidDevice getDevice() { return this.device; }
    public int getSide(){ return this.side; }
    public int getPlayerNumber(){ return this.playerNumber; }
    public AnalogStickCalibrator getStickCalibrator(){ return this.calibrator; }
    public JoyconInputHandlerThread getInputHandlerThread(){ return this.inputHandlerThread; }

    //Handler getters
    public List<JoyconInputReportHandler> getInputReportHandlers(){ return this.inputReportHandlers; }
    public List<JoyconHIDSubcommandInputHandler> getHidSubcommandInputHandlers(){ return this.hidSubcommandInputHandlers; }
    public List<JoyconHIDInputHandler> getHidInputReportHandlers(){ return this.hidInputReportHandlers; }

    //Misc
    public MemoryManager getMemoryManager(){ return this.memoryManager; }
    public synchronized Joycon getInstance(){
        return this;
    }

    /*
                        Actions
     */

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

    public interface ActionRequest{
        void requestAction(Joycon joycon);
    }
}
