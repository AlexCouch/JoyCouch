package couch.joycontest;

import java.util.*;
import purejavahidapi.*;

/**
 * A simple Joycon Manager. This simply just manages all the joy-cons connected. This is where you would do general
 * operations on your joy-cons such as pairing and unpairing, or even sending unsafe data, aka your own custom packets
 * of data. WIP
 */
public class JoyconManager {
    public static final JoyconManager INSTANCE = new JoyconManager();

    private Map<Joycon, HidDevice> pairedJoycons = new HashMap<>();

    private JoyconManager(){}

    public Map<Joycon, HidDevice> getPairedJoycons(){ return pairedJoycons; }

    /**
     * Unpairs the joycon by removing it from the hash map of paired joycons and then closing the HID safely.
     * @param jc the joycon to unpair
     */
    public void unpairJoycon(Joycon jc){
        if(pairedJoycons.containsKey(jc)){
            HidDevice value = pairedJoycons.remove(jc);
            value.close();
        }
    }

    /**
     * Searches for all joycons connected to the host device (desktop, laptop, etc) and then adds them to a hashmap of
     * joycon/hid.
     */
    public void pairJoycons(){
        List<HidDeviceInfo> connectedDevices = PureJavaHidApi.enumerateDevices();
        for(HidDeviceInfo device : connectedDevices){
            if(device.getProductString() != null && device.getPath() != null && device.getSerialNumberString() != null){
                if(device.getPath().startsWith("Bluetooth") && device.getProductString().startsWith("Joy-Con")){
                    try {
                        HidDevice hd = PureJavaHidApi.openDevice(device);
                        int side;
                        if(device.getProductString().endsWith("(L)")){
                            side = 0;
                        }else if(device.getProductString().endsWith("(R)")){
                            side = 1;
                        }else{
                            throw new IllegalStateException("Joy-Con not recognized.");
                        }
                        pairedJoycons.put(new Joycon(hd, side), hd);
                        System.out.println("Added Joy-Con: " + device.getProductString() + " with serial: " + device.getSerialNumberString());
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
