package couch.joycouch.io.input.delegate;

import couch.joycouch.joycon.Joycon;
import purejavahidapi.HidDevice;

import java.util.Arrays;

public class JoyconInputHandlerDelegate {
    private Joycon joycon;

    public JoyconInputHandlerDelegate(Joycon joycon){
        this.joycon = joycon;
    }

    public synchronized void handleDelegatedInput(HidDevice source, byte reportID, byte[] reportData, int reportLength){
        if (reportID == 0x21) { //Subcommand
//                JoyconManager.LOGGER.debug("Handling subcommand reply....with id {}", String.format("0x%04x", reportData[14]));
            this.joycon.getHidSubcommandInputHandlers().forEach(h -> {
                if (h.getSubcommandID() == reportData[14]) {
//                        JoyconManager.LOGGER.debug("Handling subcommand reply for: {}", String.format("0x%04x", h.getSubcommandID()));
                    h.handleSubcommandInput(this.joycon, Arrays.copyOfRange(reportData, 15, 49));
                }
            });
        } else if (reportID == 0x30) { //Standard full report
//                JoyconManager.LOGGER.debug("Handling standard full report....");
            this.joycon.getHidInputReportHandlers().forEach(h -> {
                if (h.getReportID() == reportID) {
//                        JoyconManager.LOGGER.debug("Handling subcommand reply for: {}", String.format("0x%04x", h.getReportID()));
                    h.handleInput(this.joycon, reportData);
                }
            });
        }
    }
}
