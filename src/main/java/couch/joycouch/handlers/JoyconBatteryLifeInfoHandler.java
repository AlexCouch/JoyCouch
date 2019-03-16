package couch.joycouch.handlers;

import couch.joycouch.io.input.hid.JoyconHIDSubcommandInputHandler;
import couch.joycouch.joycon.Joycon;
import couch.joycouch.joycon.properties.battery.BatteryInformation;
import couch.joycouch.joycon.properties.battery.BatteryStatus;

public class JoyconBatteryLifeInfoHandler implements JoyconHIDSubcommandInputHandler {
    @Override
    public byte getSubcommandID() {
        return 0x50;
    }

    @Override
    public void handleSubcommandInput(Joycon joycon, byte[] subcommandInput) {
        int b = (subcommandInput[1] << 8) | (subcommandInput[0] & 0xF);
        int voltage = (int)(b * 2.5F);
        BatteryStatus status = BatteryStatus.getBatteryStatusFrom(voltage);
        float percentage = voltage / 4200F;
        BatteryInformation batteryInfo = new BatteryInformation(status, voltage, percentage);
        joycon.setBatteryInfo(batteryInfo);
    }
}
