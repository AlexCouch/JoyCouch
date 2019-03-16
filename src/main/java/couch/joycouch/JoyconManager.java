package couch.joycouch;

import couch.joycouch.handlers.*;
import couch.joycouch.io.input.hid.HIDInputReportHandler;
import couch.joycouch.joycon.Joycon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import purejavahidapi.*;

import java.util.List;

/**
 * A simple Joycon Manager. This simply just manages all the joy-cons connected. This is where you would do general
 * operations on your joy-cons such as pairing and unpairing, or even sending unsafe data, aka your own custom packets
 * of data. WIP
 */
public class JoyconManager {
    public static final Logger LOGGER = LogManager.getLogger();

    public static final JoyconManager INSTANCE = new JoyconManager();

    /**
     * The left and right JoyCons; either of these can be null, just know that accessing these while null will result
     * in a failure. There is no exception handling at the moment, so you must do that yourself.
     */
    private Joycon left, right;

    /**
     * This field is used to decide how long to wait until the next input report is accepted.
     *
     * @see HIDInputReportHandler#onInputReport(HidDevice, byte, byte[], int) HIDInputReportHandler#onInputReport
     */
    private int frequency = 50;

    private ShutdownHook shutdownHook = new ShutdownHook();

    private JoyconManager(){}

    public void setInputFrequency(int frequency){
        this.frequency = frequency;
    }

    public int getInputFrequency(){ return this.frequency; }

    public ShutdownHook getShutdownHook(){ return this.shutdownHook; }

    /**
     * <p>
     * This initializes the JoyCon to be connected. It scans for both a left and right JoyCon to be connected.
     * <br><br>
     * This only supports one left and one right; however it is possible to have only one of them at a time.
     * </p>
     */
    public void init(){
        LOGGER.info("Initializing JoyCon Manager.");
        Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        List<HidDeviceInfo> connectedDevices = PureJavaHidApi.enumerateDevices();
        for(HidDeviceInfo device : connectedDevices){
            if(device.getProductString() != null && device.getPath() != null && device.getSerialNumberString() != null){
                if(device.getPath().startsWith("Bluetooth") && device.getProductString().startsWith("Joy-Con")){
                    LOGGER.debug("Found JoyCon {}", device.getProductString());
                    try {
                        HidDevice hd = PureJavaHidApi.openDevice(device);
                        Joycon jc = new Joycon(hd);
                        if(device.getProductString().endsWith("(L)")){
                            jc.setSide(2);
                            left = jc;
                            LOGGER.debug("\tFound left JoyCon.");
                        }else if(device.getProductString().endsWith("(R)")){
                            jc.setSide(0);
                            right = jc;
                            LOGGER.debug("\tFound left JoyCon.");
                        }else{
                            LOGGER.error("Could not recognize JoyCon!");
                        }
                        jc.addHIDInputReportHandler(new JoyconFullInputReportHandler());
                        jc.addHIDSubcommandInputHandler(new JoyconSPIMemoryInputHandler());
                        jc.addHIDSubcommandInputHandler(new JoyconBatteryLifeInfoHandler());
                        jc.start();
                        LOGGER.info("Added Joy-Con: {} with batter life {}", device.getProductString(), jc.getBatteryLife());
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Joycon getLeft(){
        return this.left;
    }
    public Joycon getRight(){
        return this.right;
    }
}
