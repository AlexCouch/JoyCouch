package couch.joycouch.joycon.properties.spi;

public class SPIMemory {
    private byte[] readMemoryAddress = new byte[5];
    private byte[] readMemoryData = new byte[50];

    public SPIMemory(byte[] address, byte[] memory){
        System.arraycopy(address, 0, readMemoryAddress, 0, address.length);
        System.arraycopy(memory, 0, readMemoryData, 0, memory.length);
    }

    //Compares the read memory address stored in the instance to the supplied address
    public boolean checkAddress(byte[] address){
        return address[0] == this.readMemoryAddress[1] && address[1] == readMemoryAddress[0];
    }

    public byte[] getReadMemoryData(){ return this.readMemoryData; }
}
