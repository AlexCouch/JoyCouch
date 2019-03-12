package couch.joycouch;

import couch.joycouch.joycon.Joycon;
import purejavahidapi.*;

import java.util.List;

/**
 * A simple Joycon Manager. This simply just manages all the joy-cons connected. This is where you would do general
 * operations on your joy-cons such as pairing and unpairing, or even sending unsafe data, aka your own custom packets
 * of data. WIP
 */
public class JoyconManager {
    public static final JoyconManager INSTANCE = new JoyconManager();

    private Joycon left, right;

    private JoyconManager(){}

    public void init(){
        List<HidDeviceInfo> connectedDevices = PureJavaHidApi.enumerateDevices();
        for(HidDeviceInfo device : connectedDevices){
            if(device.getProductString() != null && device.getPath() != null && device.getSerialNumberString() != null){
                if(device.getPath().startsWith("Bluetooth") && device.getProductString().startsWith("Joy-Con")){
                    try {
                        HidDevice hd = PureJavaHidApi.openDevice(device);
                        Joycon jc = new Joycon(hd);
                        if(device.getProductString().endsWith("(L)")){
                            jc.setSide(2);
                            left = jc;
                        }else if(device.getProductString().endsWith("(R)")){
                            jc.setSide(0);
                            right = jc;
                        }else{
                            throw new IllegalStateException("Could not recognize joycon!");
                        }
                        System.out.println(
                                "Added Joy-Con: " + device.getProductString() +
                                " with serial: " + device.getSerialNumberString() +
                                " and player number: " + jc.getPlayerNumber()
                        );
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Joycon getLeft(){
        if(this.left == null) throw new RuntimeException("Left joycon not available!");
        return this.left;
    }
    public Joycon getRight(){
        if(this.right == null) throw new RuntimeException("Right joycon not available!");
        return this.right;
    }
}
