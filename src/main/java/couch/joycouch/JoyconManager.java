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

    /**
     * The left and right JoyCons; either of these can be null, just know that accessing these while null will result
     * in a failure. There is no exception handling at the moment, so you must do that yourself.
     */
    private Joycon left, right;

    /**
     * This field is used to decide how long to wait until the next input report is accepted.
     *
     * @see couch.joycouch.joycon.JoyconHIDInputHandler#onInputReport(HidDevice, byte, byte[], int) JoyconHIDInputHandler#onInputReport
     */
    private int frequency = 50;

    private JoyconManager(){}

    public void setInputFrequency(int frequency){
        this.frequency = frequency;
    }

    public int getInputFrequency(){ return this.frequency; }

    /**
     * <p>
     * This initializes the JoyCon to be connected. It scans for both a left and right JoyCon to be connected.
     * <br><br>
     * This only supports one left and one right; however it is possible to have only one of them at a time.
     * </p>
     */
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
        return this.left;
    }
    public Joycon getRight(){
        return this.right;
    }
}
