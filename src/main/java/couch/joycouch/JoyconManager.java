package couch.joycouch;

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
        for(HidDeviceInfo device : connectedDevices){
            if(device.getProductString() != null && device.getPath() != null && device.getSerialNumberString() != null){
                if(device.getPath().startsWith("Bluetooth") && device.getProductString().startsWith("Joy-Con")){
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

    public Joycon getLeft(){
        return this.left;
    }
    public Joycon getRight(){
        return this.right;
    }
}
