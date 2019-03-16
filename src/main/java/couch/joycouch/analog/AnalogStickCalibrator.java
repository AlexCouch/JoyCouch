package couch.joycouch.analog;

import couch.joycouch.spi.SPIMemory;

import java.util.Arrays;

public class AnalogStickCalibrator {

    //The stick calibration data read from SPI flash memory
    private byte[] stick_precal;
    private byte[] stick_cal = new byte[6];

    private int side, center_x, center_y, x_min, x_max, y_min, y_max, dead_zone;

    public AnalogStickCalibrator(int side, SPIMemory calMemory, SPIMemory deadZoneMemory){
        this.stick_precal = Arrays.copyOfRange(calMemory.getReadMemoryData(), 2, 2 + calMemory.getReadMemoryData()[2]);
        this.dead_zone = getDeadZoneFrom(Arrays.copyOfRange(deadZoneMemory.getReadMemoryData(), 4, 4 + deadZoneMemory.getReadMemoryData()[2]));
        this.side = side;
        calibrate();
        sortCalibratedData();
    }

    public float[] getStickPos(int data1, int data2, int data3){
        float[] pos = new float[2];
        int[] buf = new int[]{ this.getHorizontal(data1, data2), this.getVertical(data2, data3) };
        for(int i = 0; i < 2; i++){
            float diff = buf[i] - this.stick_cal[2 + i];
            if(Math.abs(diff) < this.dead_zone) buf[i] = 0;
            else if(diff > 0){
                pos[i] = diff / buf[i];
            }else{
                pos[i] = diff / stick_cal[4 + i];
            }
        }
        return pos;
    }

    private int getHorizontal(int data1, int data2){
        return data1 | ((data2 & 0xF) << 8);
    }

    private int getVertical(int data1, int data2){
        return (data1 >> 4) | (data2 << 4);
    }

    public int getDeadZone(){ return this.dead_zone; }
    public int getCenterX(){ return this.center_x; }
    public int getCenterY(){ return this.center_y; }
    public int getXMin(){ return this.x_min; }
    public int getYMin(){ return this.y_min; }
    public int getXMax(){ return this.x_max; }
    public int getYMax(){ return this.y_max; }

    private int getDeadZoneFrom(byte[] data){
        return (data[3] << 8) & 0xF00 | data[2];
    }

    private void calibrate(){
        stick_cal[0] = (byte)((stick_precal[1] << 8) & 0xF00 | stick_precal[0]);
        stick_cal[1] = (byte)((stick_precal[2] << 4) | (stick_precal[1] >> 4));
        stick_cal[2] = (byte)((stick_precal[4] << 8) & 0xF00 | stick_precal[3]);
        stick_cal[3] = (byte)((stick_precal[5] << 4) | (stick_precal[4] >> 4));
        stick_cal[4] = (byte)((stick_precal[7] << 8) & 0xF00 | (stick_precal[6]));
        stick_cal[5] = (byte)((stick_precal[8] << 4) | (stick_precal[7] >> 4));
    }

    private void sortCalibratedData(){
        int xci = this.side == 0 ? 0 : (this.side == 2 ? 2 : 0);
        int yci = this.side == 0 ? 1 : (this.side == 2 ? 3 : 1);
        int xmni = this.side == 0 ? 2 : (this.side == 2 ? 4 : 2);
        int ymni = this.side == 0 ? 3 : (this.side == 2 ? 5 : 3);
        int xmxi = this.side == 0 ? 4 : (this.side == 2 ? 0 : 4);
        int ymxi = this.side == 0 ? 5 : (this.side == 2 ? 1 : 5);

        this.center_x = this.stick_cal[xci];
        this.center_y = this.stick_cal[yci];
        this.x_min = center_x - this.stick_cal[xmni];
        this.y_min = center_y - this.stick_cal[ymni];
        this.x_max = center_x + this.stick_cal[xmxi];
        this.y_max = center_y + this.stick_cal[ymxi];
    }
}
