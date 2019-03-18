package couch.joycouch.joycon.properties.spi;

import couch.joycouch.io.output.JoyconOutputReportFactory;
import couch.joycouch.joycon.Joycon;

import java.util.List;

public class MemoryManager {
    private final Joycon joycon;

    private List<SPIMemory> readMemory;

    public MemoryManager(Joycon joycon){
        this.joycon = joycon;
    }

    public void storeReadMemory(SPIMemory memory){ this.readMemory.add(memory); }

    public SPIMemory getReadMemory(byte[] address){
        for(SPIMemory mem : readMemory){
            if(mem.checkAddress(address)){
                return mem;
            }
        }
        return null;
    }

    public synchronized void readMemory(byte subsect, byte address, byte size){
        setupMemoryRead(subsect, address, size);
        JoyconOutputReportFactory.INSTANCE
                .setOutputReportID((byte) 0x1)
                .setSubcommandID((byte) 0x10)
                .setSubcommandArgs(new byte[]{address, subsect, 0x0, 0x0, size})
                .sendTo(this.joycon);
    }

    private void setupMemoryRead(byte subsect, byte address, byte size){
        byte[] buf = new byte[8];
        buf[0] = 0x71;
        buf[1] = address;
        buf[2] = subsect;
        buf[5] = size;
        this.joycon.getDevice().setFeatureReport(buf[0], buf, buf.length);
    }
}
