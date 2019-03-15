package couch.joycouch.analog;

public class AnalogStickStatus {

    private int horizontal, vertical;

    public AnalogStickStatus(
            AnalogStickCalibrator calibrator,
            byte data1, byte data2, byte data3
    ){
        int[] leftPos = calibrator.getStickPos(data1, data2, data3);
        horizontal = leftPos[0];
        vertical = leftPos[1];
    }

    public AnalogStick getAnalogStick(){
        return new AnalogStick(this.horizontal, this.vertical);
    }
}
