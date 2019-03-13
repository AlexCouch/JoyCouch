package couch.joycouch.analog;

public class AnalogStick {
    private int horizontal, vertical;

    public AnalogStick(int horizontal, int vertical){
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public int getHorizontal(){
        return this.horizontal;
    }

    public int getVertical(){
        return this.vertical;
    }
}
