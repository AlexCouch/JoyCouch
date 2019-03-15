package couch.joycouch.spi;

import couch.joycouch.joycon.Joycon;
import couch.joycouch.io.input.hid.JoyconHIDSubcommandInputHandler;

import java.util.Arrays;

public class JoyconSPIMemoryInputHandler implements JoyconHIDSubcommandInputHandler{

    @Override
    public byte getSubcommandID() {
        return 0x10;
    }

    @Override
    public void handleSubcommandInput(Joycon joycon, byte[] subcommandData) {
        synchronized (joycon.getInstance()) {
            joycon.addMemoryCache(new SPIMemory(
                    new byte[]{
                            subcommandData[1],
                            subcommandData[0]
                    },
                    Arrays.copyOfRange(
                            subcommandData,
                            2,
                            subcommandData.length
                    )
            ));
            if(joycon.getMemoryCaches().size() == 2){
                joycon.notify();
            }
        }
    }
}
