package couch.joycouch.joycon.init;

import couch.joycouch.JoyconManager;
import couch.joycouch.analog.AnalogStickCalibrator;
import couch.joycouch.io.output.JoyconOutputReportFactory;
import couch.joycouch.joycon.Joycon;
import couch.joycouch.joycon.properties.battery.BatteryInformation;
import couch.joycouch.joycon.properties.spi.SPIMemory;
import purejavahidapi.HidDevice;
import purejavahidapi.InputReportListener;

import java.util.*;

public class JoyconInitializer extends Thread implements InputReportListener {
    private Joycon joycon;
    private List<SPIMemory> memoryInit = new ArrayList<>();
    private SPIMemory calMem, devParamMem;

    public JoyconInitializer(Joycon joycon){
        this.joycon = joycon;
    }

    public synchronized void run(){
        initializeBatteryInfo();
        initializeCalibrator();
//        this.joycon.requestAction(Joycon::notify);
    }

    private void getMemoryForCalibrator(){
        this.memoryInit.forEach(m -> {
            if(m.checkAddress(new byte[]{(byte) 0x46, (byte) 0x60})){
                calMem = m;
            }else if(m.checkAddress(new byte[]{(byte) 0x98, (byte) 0x60})){
                devParamMem = m;
            }
        });
        createCalibrator();
    }

    private void createCalibrator(){
        if(calMem == null || devParamMem == null) getMemoryForCalibrator();
        this.joycon.setCalibrator(new AnalogStickCalibrator(
                this.joycon.getSide(),
                calMem,
                devParamMem
        ));
    }

    private void initializeCalibrator(){
        JoyconManager.LOGGER.debug("Creating analog stick calibrator...");
        this.joycon.getMemoryManager().readMemory((byte) 0x60, (byte) 0x46, (byte) 9);
        this.joycon.getMemoryManager().readMemory((byte) 0x60, (byte) 0x98, (byte) 16);
        getMemoryForCalibrator();
        JoyconManager.LOGGER.debug("\tDone!");
    }

    private void initializeBatteryInfo(){
        JoyconOutputReportFactory.INSTANCE.setOutputReportID((byte) 0x01)
                .setSubcommandID((byte) 0x50)
                .sendTo(this.joycon);
    }

    @Override
    public void onInputReport(HidDevice source, byte reportID, byte[] reportData, int reportLength) {
        byte[] subcommandData = Arrays.copyOfRange(reportData, 15, 49);
        if(reportData[14] == 0x50){
            this.joycon.setBatteryInfo(BatteryInformation.getBatteryInformation(subcommandData));
        }else if(reportData[14] == 0x10){
            JoyconManager.LOGGER.debug("Attempting to read memory from SPI flash memory @ {{}, {}}", String.format("0x%04x", subcommandData[0]), String.format("0x%04x", subcommandData[1]));
            try {
                this.memoryInit.add(new SPIMemory(
                        new byte[]{
                                subcommandData[1],
                                subcommandData[0]
                        },
                        Arrays.copyOfRange(
                                subcommandData,
                                2,
                                subcommandData.length - 1
                        )
                ));
            }catch(RuntimeException e){
                JoyconManager.LOGGER.debug(e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
