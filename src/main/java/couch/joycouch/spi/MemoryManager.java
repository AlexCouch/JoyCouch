package couch.joycouch.spi;

import couch.joycouch.io.output.JoyconOutputReportFactory;
import couch.joycouch.joycon.Joycon;

public class MemoryManager {
    private final Joycon joycon;

    private SPIMemory readMemory;

    public MemoryManager(Joycon joycon){
        this.joycon = joycon;
    }

    public void storeReadMemory(SPIMemory memory){ this.readMemory = memory; }

    private SPIMemory getReadMemory(){
        SPIMemory lmem = this.readMemory; //Locally cached memory object
        readMemory = null; //Deallocate
        return lmem; //return locally cached memory object
    }

    public SPIMemory readMemory(byte subsect, byte address, byte size){
        synchronized (this.joycon) {
            try {
                setupMemoryRead(subsect, address, size);
                JoyconOutputReportFactory.INSTANCE
                        .setOutputReportID((byte) 0x1)
                        .setSubcommandID((byte) 0x10)
                        .setSubcommandArgs(new byte[]{address, subsect, 0x0, 0x0, size})
                        .sendTo(this.joycon);
                this.joycon.wait(); //Memory manager should be on the same thread; if not, then I dun goofed
                return this.getReadMemory();
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            return null;
        }
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
