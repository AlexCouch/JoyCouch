package couch.joycouch;

import couch.joycouch.joycon.*;
import purejavahidapi.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple Joycon Manager. This simply just manages all the joy-cons connected. This is where you would do general
 * operations on your joy-cons such as pairing and unpairing, or even sending unsafe data, aka your own custom packets
 * of data. WIP
 */
public class JoyconManager {
    public static final JoyconManager INSTANCE = new JoyconManager();

    private List<Joycon> pairedJoycons = new ArrayList<>();

    private JoyconManager(){}

    public List<Joycon> getPairedJoycons(){ return pairedJoycons; }

    public Joycon pairJoycon(){
        List<HidDeviceInfo> connectedDevices = PureJavaHidApi.enumerateDevices();
        for(HidDeviceInfo device : connectedDevices){
            if(device.getProductString() != null && device.getPath() != null && device.getSerialNumberString() != null){
                if(device.getPath().startsWith("Bluetooth") && device.getProductString().startsWith("Joy-Con")){
                    try {
                        HidDevice hd = PureJavaHidApi.openDevice(device);
                        int i;
                        if(device.getProductString().endsWith("(L)")){
                            i = 2;
                        }else if(device.getProductString().endsWith("(R)")){
                            i = 0;
                        }else{
                            i = 0;
                        }
                        Joycon jc = new Joycon(hd, i);
                        this.pairedJoycons.add(jc);
                        System.out.println(
                                "Added Joy-Con: " + device.getProductString() +
                                " with serial: " + device.getSerialNumberString() +
                                " and player number: " + jc.getPlayerNumber()
                        );
                        return jc;
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}
