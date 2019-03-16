package couch.joycouch.analog;

public class AnalogStick {
    private float horizontal, vertical;

    public AnalogStick(float horizontal, float vertical){
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public float getHorizontal(){
        return this.horizontal;
    }

    public float getVertical(){
        return this.vertical;
    }
}
