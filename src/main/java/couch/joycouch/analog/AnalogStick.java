package couch.joycouch.analog;

import couch.joycouch.joycon.Joycon;

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
        if(this.inVal != 8) {
            if (this.getJoycon().isCombined()) {
                for (int i = 1; i < 3; i++) {
                    if (this.getSide() == 0) this.inVal++;
                    else if (this.getSide() == 1) this.inVal--;
                    if (inVal >= 8) inVal = 0;
                    if (inVal < 0) inVal = 7;
                }
            }
        }
        for(AnalogStickPos pos : AnalogStickPos.values()){
            if(pos.getInputValue() == this.inVal){
                return pos;
            }
        }
        return null;
    }
}
