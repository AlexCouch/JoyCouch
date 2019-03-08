package couch.joycouch;

import couch.joycouch.joycon.*;
import purejavahidapi.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple SingleJoycon Manager. This simply just manages all the joy-cons connected. This is where you would do general
 * operations on your joy-cons such as pairing and unpairing, or even sending unsafe data, aka your own custom packets
 * of data. WIP
 */
public class JoyconManager {
    public static final JoyconManager INSTANCE = new JoyconManager();

    private List<Joycon> pairedJoycons = new ArrayList<>();

    private JoyconManager(){}

    public List<Joycon> getPairedJoycons(){ return pairedJoycons; }

    public CombinedJoycon combineJoycons(SingleJoycon jc1, SingleJoycon jc2){
        CombinedJoycon combinedJoycon = new CombinedJoycon(jc1, jc2);
        if(pairedJoycons.contains(jc1) && pairedJoycons.contains(jc2)){
            if(jc1.getSide() == jc2.getSide()) throw new IllegalArgumentException("Both joycons are of the same side!");
            this.pairedJoycons.remove(jc1);
            this.pairedJoycons.remove(jc2);
            this.pairedJoycons.add(combinedJoycon);
        }
        return combinedJoycon;
    }

    public SingleJoycon pairJoycon(){
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
                            throw new IllegalStateException("Unknown SingleJoycon!");
                        }
                        SingleJoycon jc = new SingleJoycon(hd, side, this.pairedJoycons.size());
                        if(this.pairedJoycons.contains(jc)) continue;
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
