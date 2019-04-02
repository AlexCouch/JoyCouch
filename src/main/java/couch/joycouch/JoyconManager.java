package couch.joycouch;

import com.sun.jna.Platform;
import couch.joycouch.handlers.*;
import couch.joycouch.handlers.subcommands.*;
import couch.joycouch.joycon.Joycon;
import couch.joycouch.joycon.properties.battery.BatteryInformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import purejavahidapi.*;

import java.util.List;

public class JoyconManager extends Thread{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final JoyconManager INSTANCE = new JoyconManager();

    private static final int LEFT_PROD_ID = 8198;
    private static final int RIGHT_PROD_ID = 8199;

    private Joycon left, right;

    private int frequency = 50;

    private ShutdownHook shutdownHook = new ShutdownHook();

    private JoyconManager(){
        super("Joycon-Manager");
    }

    public void setInputFrequency(int frequency){
        this.frequency = frequency;
    }

    public int getInputFrequency(){ return this.frequency; }

    public ShutdownHook getShutdownHook(){ return this.shutdownHook; }

    /*public void start(){
        super.start();
        try {
            this.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/

    public synchronized void run(){
        LOGGER.info("Initializing JoyCon Manager.");
        Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        List<HidDeviceInfo> connectedDevices = PureJavaHidApi.enumerateDevices();
        findJoycons(connectedDevices, Platform.getOSType());
    }

    private void findJoycons(List<HidDeviceInfo> devices, int os){
        if(os == Platform.WINDOWS){
            findJoyconsWindows(devices);
        }else{
            findJoyconsUnix(devices);
        }
    }

    private void findJoyconsUnix(List<HidDeviceInfo> devices){
        for(HidDeviceInfo device : devices){
            if(device.getProductString() != null && device.getPath() != null && device.getSerialNumberString() != null){
                if(device.getProductString().startsWith("Joy-Con")){
                    LOGGER.debug("Found JoyCon {}", device.getProductString());
                    try {
                        HidDevice hd = PureJavaHidApi.openDevice(device);
                        Joycon jc = new Joycon(hd);
                        jc.join();
                        if(device.getProductString().endsWith("(L)")){
                            jc.setSide(2);
                            left = jc;
                            LOGGER.debug("\tFound left JoyCon.");
                        }else if(device.getProductString().endsWith("(R)")){
                            jc.setSide(0);
                            right = jc;
                            LOGGER.debug("\tFound right JoyCon.");
                        }else{
                            LOGGER.error("Could not recognize JoyCon!");
                        }
                        jc.addHIDInputReportHandler(new JoyconFullInputReportHandler());
                        jc.addHIDSubcommandInputHandler(new JoyconSPIMemoryInputHandler());
                        jc.addHIDSubcommandInputHandler(new JoyconBatteryLifeInfoHandler());
//                        jc.start();
                        BatteryInformation batteryInformation = jc.getBatteryInfo();
                        LOGGER.info("Added Joy-Con: {} with battery status {}, voltage {}, and percentage {}", device.getProductString(), batteryInformation.getBatteryStatus(), batteryInformation.getBatteryVoltage(), batteryInformation.getBatteryPercentage());
                    } catch (java.io.IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void findJoyconsWindows(List<HidDeviceInfo> devices){
        for(HidDeviceInfo device : devices){
            if(device.getProductString() != null && device.getPath() != null && device.getSerialNumberString() != null){
                if(device.getProductString().startsWith("Wireless Gamepad") && device.getManufacturerString().equals("Nintendo")){
                    LOGGER.debug("Found JoyCon");
                    try {
                        HidDevice hd = PureJavaHidApi.openDevice(device);
                        Joycon jc = new Joycon(hd);
                        jc.join();
                        if(device.getProductId() == LEFT_PROD_ID){
                            jc.setSide(2);
                            left = jc;
                            LOGGER.debug("\tFound left JoyCon.");
                        }else if(device.getProductId() == RIGHT_PROD_ID){
                            jc.setSide(0);
                            right = jc;
                            LOGGER.debug("\tFound right JoyCon.");
                        }else{
                            LOGGER.error("Could not recognize JoyCon!");
                        }
                        jc.addHIDInputReportHandler(new JoyconFullInputReportHandler());
                        jc.addHIDSubcommandInputHandler(new JoyconSPIMemoryInputHandler());
                        jc.addHIDSubcommandInputHandler(new JoyconBatteryLifeInfoHandler());
//                        jc.start();
                        BatteryInformation batteryInformation = jc.getBatteryInfo();
                        LOGGER.info("Added Joy-Con: {} with battery status {}, voltage {}, and percentage {}", device.getProductString(), batteryInformation.getBatteryStatus(), batteryInformation.getBatteryVoltage(), batteryInformation.getBatteryPercentage());
                    } catch (java.io.IOException | InterruptedException e) {
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
