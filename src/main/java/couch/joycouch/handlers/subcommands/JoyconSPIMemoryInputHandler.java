package couch.joycouch.handlers.subcommands;

import couch.joycouch.JoyconManager;
import couch.joycouch.joycon.Joycon;
import couch.joycouch.io.input.hid.JoyconHIDSubcommandInputHandler;
import couch.joycouch.joycon.properties.spi.SPIMemory;

import java.util.Arrays;

public class JoyconSPIMemoryInputHandler implements JoyconHIDSubcommandInputHandler{

    @Override
    public byte getSubcommandID() {
        return 0x10;
    }

    @Override
    public synchronized void handleSubcommandInput(Joycon joycon, byte[] subcommandData) {
        JoyconManager.LOGGER.debug("Attempting to read memory from SPI flash memory @ {{}, {}}", String.format("0x%04x", subcommandData[0]), String.format("0x%04x", subcommandData[1]));
        try {
            joycon.getMemoryManager().storeReadMemory(new SPIMemory(
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
            joycon.requestAction(Joycon::notify);
        }catch(RuntimeException e){
            JoyconManager.LOGGER.debug(e.getMessage());
            e.printStackTrace();
        }
    }
}
