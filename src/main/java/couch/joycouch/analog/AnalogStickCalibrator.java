package couch.joycouch.analog;

import couch.joycouch.joycon.properties.spi.SPIMemory;

import java.util.Arrays;

public class AnalogStickCalibrator {

    //The stick calibration data read from SPI flash memory
    private byte[] stick_precal;
    private int[] stick_cal = new int[6];

    private int side, center_x, center_y, x_min, x_max, y_min, y_max, dead_zone;

    public AnalogStickCalibrator(int side, SPIMemory calMemory, SPIMemory deadZoneMemory){
        this.stick_precal = Arrays.copyOfRange(calMemory.getReadMemoryData(), 3, 3 + calMemory.getReadMemoryData()[2]);
        this.dead_zone = getDeadZoneFrom(Arrays.copyOfRange(deadZoneMemory.getReadMemoryData(), 4, 4 + deadZoneMemory.getReadMemoryData()[2]));
        this.side = side;
        calibrate();
        sortCalibratedData();
    }

    public float[] getStickPos(byte data1, byte data2, byte data3){
        float[] pos = new float[2];
        int[] buf = new int[]{ this.getHorizontal(data1, data2), this.getVertical(data2, data3) };
        for(int i = 0; i < 2; i++){
            float diff = buf[i] - this.stick_cal[i];
            if(Math.abs(diff) < this.dead_zone) pos[i] = 0;
            else if(diff > 0){
                pos[i] = diff / stick_cal[2 + i];
            }else{
                pos[i] = diff / stick_cal[4 + i];
            }
        }
        return pos;
    }

    private int getHorizontal(byte data1, byte data2){
        int b = ((data2 & 0xF) << 8);
        int b1 = ((data1 & 0xFF) | b);
        return b1;
    }

    private int getVertical(byte data1, byte data2){
        int i1 = ((data2 & 0xFF) << 4);
        int i2 = ((data1 & 0xFF) >> 4);
        return (i1 | i2);
    }

    public int getDeadZone(){ return this.dead_zone; }
    public int getCenterX(){ return this.center_x; }
    public int getCenterY(){ return this.center_y; }
    public int getXMin(){ return this.x_min; }
    public int getYMin(){ return this.y_min; }
    public int getXMax(){ return this.x_max; }
    public int getYMax(){ return this.y_max; }

    private int getDeadZoneFrom(byte[] data){
        int b = (data[3] << 8) & 0xF00;
        int b1 = b | (data[2]) & 0xFF;
        return b1;
    }

    private void calibrate(){
        stick_cal[0] = (stick_precal[1] << 8) & 0xF00 | stick_precal[0] & 0xFF;
        stick_cal[1] = ((stick_precal[2] << 4)) | (stick_precal[1] >> 4) & 0xF;
        stick_cal[2] = (stick_precal[4] << 8) & 0xF00 | stick_precal[3] & 0xFF;
        stick_cal[3] = ((stick_precal[5] << 4)) | (stick_precal[4] >> 4) & 0xF;
        stick_cal[4] = (stick_precal[7] << 8) & 0xF00 | stick_precal[6] & 0xFF;
        stick_cal[5] = ((stick_precal[8] << 4)) | (stick_precal[7] >> 4) & 0xF;
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
        this.x_min = (center_x - this.stick_cal[xmni]) & 0xFF;
        this.y_min = (center_y - this.stick_cal[ymni]) & 0xFF;
        this.x_max = (center_x + this.stick_cal[xmxi]) & 0xFFF;
        this.y_max = (center_y + this.stick_cal[ymxi]) & 0xFFF;
    }
}
