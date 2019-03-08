package couch.joycouch.analog;

import couch.joycouch.*;

public class AnalogStick {
    private Joycon jc;
    private byte inVal = -1;

    public AnalogStick(Joycon jc){
        this.jc = jc;
    }

    public Joycon getJoycon(){
        return this.jc;
    }

    public int getSide(){
        return this.jc.getSide();
    }

    public void updatePos(byte inVal){
        this.inVal = inVal;
    }

    public AnalogStickPos getPos(){
        for(AnalogStickPos pos : AnalogStickPos.values()){
            if(pos.getInputValue() == this.inVal){
                return pos;
            }
        }
        return null;
    }
}
