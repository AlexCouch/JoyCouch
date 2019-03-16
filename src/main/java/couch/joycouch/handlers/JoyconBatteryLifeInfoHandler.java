package couch.joycouch.handlers;

import couch.joycouch.io.input.hid.JoyconHIDSubcommandInputHandler;
import couch.joycouch.joycon.Joycon;

public class JoyconBatteryLifeInfoHandler implements JoyconHIDSubcommandInputHandler {
    @Override
    public byte getSubcommandID() {
        return 0x50;
    }

    @Override
    public void handleSubcommandInput(Joycon joycon, byte[] subcommandInput) {
        joycon.setBatteryLife(decodeBattery(subcommandInput[1], subcommandInput[0]));
    }

    private int decodeBattery(byte b1, byte b2){
        int data = (b1 << 8) | (b2 & 0xF);

        if(data >= 0x0528 && data <= 0x059F) return 2;
        else if(data >= 0x05A0 && data <= 0x05DF) return 4;
        else if(data >= 0x05E0 && data <= 0x0617) return 6;
        else if(data >= 0x0618 && data <= 0x0690) return 8;
        else return -1;
    }
}
