package couch.joycouch.joycon;

import couch.joycouch.handlers.*;
import purejavahidapi.HidDevice;

public class CombinedJoycon implements Joycon{

    private SingleJoycon left, right;

    public CombinedJoycon(SingleJoycon left, SingleJoycon right){
        this.left = left;
        this.left.setCombined(true);
        this.right = right;
        this.right.setCombined(true);
    }

    public SingleJoycon getLeft(){ return this.left; }
    public SingleJoycon getRight(){ return this.right; }
    public void enableRumbleLeft(){ this.left.enableRumble(); }
    public void enableRumbleRight(){ this.right.enableRumble(); }
    public void rumbleLeft(float lowFreq, float highFreq, float amp){ this.left.rumbleJoycon(lowFreq, highFreq, amp); }
    public void rumbleRight(float lowFreq, float highFreq, float amp){ this.right.rumbleJoycon(lowFreq, highFreq, amp); }
    public HidDevice getDeviceLeft(){ return this.left.getDevice(); }
    public HidDevice getDeviceRight(){ return this.right.getDevice(); }
    public void setButtonInputHandler(JoyconButtonInputHandler handler){
        this.left.setJoyconButtonInputHandler(handler);
        this.right.setJoyconButtonInputHandler(handler);
    }
    public void setAnalogInputHandler(JoyconAnalogInputHandler handler){
        this.left.setJoyconAnalogInputHandler(handler);
        this.right.setJoyconAnalogInputHandler(handler);
    }
    public void setUnsafeInputHandler(JoyconUnsafeInputHandler handler){
        this.left.setJoyconUnsafeInputHandler(handler);
        this.right.setJoyconUnsafeInputHandler(handler);
    }
}
