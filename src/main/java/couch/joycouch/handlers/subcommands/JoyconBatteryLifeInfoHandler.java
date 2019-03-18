package couch.joycouch.handlers.subcommands;

import couch.joycouch.io.input.hid.JoyconHIDSubcommandInputHandler;
import couch.joycouch.joycon.Joycon;
import couch.joycouch.joycon.properties.battery.BatteryInformation;

public class JoyconBatteryLifeInfoHandler implements JoyconHIDSubcommandInputHandler {
    @Override
    public byte getSubcommandID() {
        return 0x50;
    }

    @Override
    public void handleSubcommandInput(Joycon joycon, byte[] subcommandInput) {
        joycon.setBatteryInfo(BatteryInformation.getBatteryInformation(subcommandInput));
    }
}
