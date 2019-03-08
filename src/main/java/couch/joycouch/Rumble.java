package couch.joycouch;

public class Rumble {
    private float lowFreq, highFreq, amplitude;

    public Rumble(float lowFreq, float highFreq, float amplitude){
        this.lowFreq = lowFreq;
        this.highFreq = highFreq;
        this.amplitude = amplitude;
    }

    private float clamp(float x, float min, float max){
        if(x < min) return min;
        if(x > max) return max;
        return x;
    }

    public byte[] getRumbleData(){
        byte[] buf = new byte[8];

        if(amplitude == 0.0f){
            buf[0] = 0x0;
            buf[1] = 0x1;
            buf[2] = 0x40;
            buf[3] = 0x40;
        }else{
            lowFreq = clamp(lowFreq, 40.875885f, 626.286133f);
            amplitude = clamp(amplitude, 0.0f, 1.0f);
            highFreq = clamp(highFreq, 81.75177f, 1252.572266f);
            int hf = (int)((Math.round(32f * (Math.log(highFreq * 0.1f) / Math.log(2)) - 0x60))) * 4;
            byte lf = (byte)(Math.round(32f * (Math.log(lowFreq * 0.1f) / Math.log(2)) - 0x40));
            byte hf_amp;
            if(amplitude == 0.0) hf_amp = 0;
            else if (amplitude < 0.117) hf_amp = (byte)((((Math.log(amplitude * 1000) / Math.log(2)) * 32) - 0x60) / (5 - Math.pow(amplitude, 2)) - 1);
            else if (amplitude < 0.23) hf_amp = (byte)((((Math.log(amplitude * 1000) / Math.log(2)) * 32) - 0x60) - 0x5c);
            else hf_amp = (byte)(((((Math.log(amplitude * 1000) / Math.log(2)) * 32) - 0x60) * 2) - 0xf6);

            int lf_amp = (int)(Math.round(hf_amp) * 0.5f);
            byte parity = (byte)(lf_amp % 2);
            if(parity > 0){
                --lf_amp;
            }

            lf_amp = lf_amp >> 1;
            lf_amp += 0x40;
            if (parity > 0) lf_amp |= 0x8000;
            buf[0] = (byte)(hf & 0xff);
            buf[1] = (byte)((hf >> 8) & 0xff);
            buf[2] = lf;
            buf[1] += hf_amp;
            buf[2] += (byte)((lf_amp >> 8) & 0xff);
            buf[3] += (byte)(lf_amp & 0xff);
        }

        for (int i = 0; i < 4; ++i)
        {
            buf[4 + i] = buf[i];
        }

        return buf;
    }
}
