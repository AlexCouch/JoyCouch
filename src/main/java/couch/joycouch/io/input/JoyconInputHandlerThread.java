package couch.joycouch.io.input;

import couch.joycouch.joycon.Joycon;
import purejavahidapi.HidDevice;

import java.util.Arrays;

public class JoyconInputHandlerThread extends Thread{
    private Joycon joycon;

    public JoyconInputHandlerThread(Joycon joycon){
        super("Input-Handler-Thread");
        this.joycon = joycon;
    }

    public void handleDelegatedInput(HidDevice source, byte reportID, byte[] reportData, int reportLength){
        if(reportID == 0x21){ //Subcommand
            this.joycon.getHidSubcommandInputHandlers().forEach(h -> {
                if(h.getSubcommandID() == reportData[14]){
                    h.handleSubcommandInput(this.joycon, Arrays.copyOfRange(reportData, 15, 49));
                }
            });
        }else if(reportID == 0x30){ //Standard full report
            this.joycon.getHidInputReportHandlers().forEach(h -> {
                if(h.getReportID() == reportID){
                    h.handleInput(this.joycon, reportData);
                }
            });
        }
    }
}
