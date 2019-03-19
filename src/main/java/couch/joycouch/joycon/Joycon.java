package couch.joycouch.joycon;

import couch.joycouch.Rumble;
import couch.joycouch.analog.AnalogStickCalibrator;
import couch.joycouch.io.input.JoyconInputHandlerThread;
import couch.joycouch.io.input.JoyconInputReportHandler;
import couch.joycouch.io.input.hid.*;
import couch.joycouch.io.output.JoyconOutputReportFactory;
import couch.joycouch.joycon.action.ActionRequestProcessorThread;
import couch.joycouch.joycon.init.JoyconInitializer;
import couch.joycouch.joycon.properties.battery.BatteryInformation;
import couch.joycouch.joycon.properties.spi.MemoryManager;
import purejavahidapi.HidDevice;

import java.util.ArrayList;
import java.util.List;

public class Joycon extends Thread{

    /*
                        Properties
     */
    private HidDevice device;

    private int side;
    protected boolean rumbleOn = false;
    private BatteryInformation batteryInfo;
    protected int playerNumber;
    private AnalogStickCalibrator calibrator = null;
    private boolean running = false, initializing = false;

    /*
                        Handlers
     */
    private List<JoyconInputReportHandler> inputReportHandlers = new ArrayList<>();

    private List<JoyconHIDInputHandler> hidInputReportHandlers = new ArrayList<>();
    private List<JoyconHIDSubcommandInputHandler> hidSubcommandInputHandlers = new ArrayList<>();
    private HIDInputReportHandler hidInputReportHandler = new HIDInputReportHandler(this);

    /*
                        Misc
     */
    private JoyconInputHandlerThread inputHandlerThread = new JoyconInputHandlerThread(this);
    private ActionRequestProcessorThread actionRequestProcessor = new ActionRequestProcessorThread(this);
    private final JoyconInitializer initializer = new JoyconInitializer(this);

    private MemoryManager memoryManager = new MemoryManager(this);

    public Joycon(HidDevice device) {
        super("JoyCon-" + device.getHidDeviceInfo().getSerialNumberString());
        this.device = device;
        this.running = true;
        this.start();
        this.actionRequestProcessor.start();
    }


    /*
                        Startup/Shutdown
     */

    @Override
    public synchronized void run(){
        this.device.setInputReportListener(initializer);
        initializer.start();
        try {
            initializer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.actionRequestProcessor.requestAction(Joycon::setInputReportMode);
        this.actionRequestProcessor.requestAction(Joycon::setPlayerLED);
        device.setInputReportListener(hidInputReportHandler);
        inputHandlerThread.start();
    }

    public void shutdown(){
        this.reset();
        this.actionRequestProcessor.shutdown();
        running = false;
    }

    public void reset(){
        JoyconOutputReportFactory.INSTANCE
                .setOutputReportID((byte)0x01)
                .setSubcommandID((byte)0x03)
                .setSubcommandArg((byte)0x3F)
                .sendTo(this);
        /*JoyconOutputReportFactory.INSTANCE
                .setOutputReportID((byte)0x01)
                .setSubcommandID((byte)0x06)
                .setSubcommandArg((byte)0x0)
                .sendTo(this);*/
    }

    /*
                            Getters/Setters/Adders
     */

    //Handler adders/removers
    public void addInputReportHandler(JoyconInputReportHandler handler){ this.inputReportHandlers.add(handler); }
    public void removeInputReportHandler(JoyconInputReportHandler handler){ this.inputReportHandlers.remove(handler); }
    public void addHIDInputReportHandler(JoyconHIDInputHandler inputHandler){ this.hidInputReportHandlers.add(inputHandler); }
    public void removeHIDInputReportHandler(JoyconHIDInputHandler handler){ this.hidInputReportHandlers.remove(handler); }
    public void addHIDSubcommandInputHandler(JoyconHIDSubcommandInputHandler inputHandler){ this.hidSubcommandInputHandlers.add(inputHandler); }
    public void removeHIDSubcommandInputReportHandler(JoyconHIDSubcommandInputHandler handler){ this.hidSubcommandInputHandlers.remove(handler); }

    //Property setters
    public void setSide(int side){ this.side = side; }
    public void setBatteryInfo(BatteryInformation batteryInfo){ this.batteryInfo = batteryInfo; }
    public void setCalibrator(AnalogStickCalibrator calibrator){ this.calibrator = calibrator; }

    //Property getters
    public boolean isRunning(){ return this.running; }

    public BatteryInformation getBatteryInfo(){ return this.batteryInfo; }

    public void updateBatteryInformation(){

    }
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
        this.actionRequestProcessor.requestAction(actionRequest);
    }

    private void setInputReportMode(){
        JoyconOutputReportFactory.INSTANCE
                .setOutputReportID((byte) 0x01)
                .setSubcommandID((byte) 0x03)
                .setSubcommandArg((byte) 0x30)
                .sendTo(this);
    }

    public void enableRumble() {
        JoyconOutputReportFactory.INSTANCE
                .setOutputReportID((byte)0x01)
                .setSubcommandID((byte)0x48)
                .setSubcommandArg((byte)0x1)
                .sendTo(this);
        this.rumbleOn = true; //Don't set this to true until the reply input report comes back
    }

    public void rumbleJoycon() { //Does the rumble command have a response?
        if(!rumbleOn) enableRumble();
        JoyconOutputReportFactory.INSTANCE
                .setOutputReportID((byte)0x10)
                .setOutoutReportData(new Rumble(160f, 320f, 0.6f).getRumbleData())
                .sendTo(this);
    }

    public synchronized void setPlayerLED() {
        JoyconOutputReportFactory.INSTANCE
                .setOutputReportID((byte) 0x01)
                .setSubcommandID((byte) 0x30)
                .setSubcommandArgs(new byte[]{(byte)0x1})
                .sendTo(this);
    }

    public interface ActionRequest{
        void requestAction(Joycon joycon);
    }
}
