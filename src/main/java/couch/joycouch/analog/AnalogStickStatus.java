package couch.joycouch.analog;

public class AnalogStickStatus {

    private int leftHorizontal = 0, leftVertical = 0;
    private int rightHorizontal = 0, rightVertical = 0;

    public AnalogStickStatus(
            int left1, int left2, int left3,
            int right1, int right2, int right3
    ){
        decodeLeft(left1, left2, left3);
        centerLeft();
        decodeRight(right1, right2, right3);
        centerRight();
    }

    public AnalogStick getLeftAnalogStick(){
        return new AnalogStick(this.leftHorizontal, this.leftVertical);
    }

    public AnalogStick getRightAnalogStick(){
        return new AnalogStick(this.rightHorizontal, this.rightVertical);
    }

    private void decodeLeft(int left1, int left2, int left3){
        leftHorizontal = decodeHorizontal(left1, left2);
        leftVertical = decodeVertical(left2, left3);
    }

    private void centerLeft(){
        center(leftHorizontal, leftVertical);
    }

    private void centerRight(){
        center(rightHorizontal, rightVertical);
    }

    private void center(int horizontal, int vertical){

    }

    private void decodeRight(int right1, int right2, int right3){
        rightHorizontal = decodeHorizontal(right1, right2);
        rightVertical = decodeVertical(right2, right3);
    }

    private int decodeHorizontal(int di1, int di2){
        int i = di2 & 0xF0;
        int j = i >> 8;
        return di1 & j;
    }

    private int decodeVertical(int di2, int di3){
        int i = di2 >> 4;
        int j = di3 << 4;
        return i | j;
    }
}
