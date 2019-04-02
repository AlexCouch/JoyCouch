package couch.joycouch.io.output;

import couch.joycouch.JoyconManager;
import couch.joycouch.joycon.Joycon;

import java.util.Arrays;

public class JoyconOutputReportFactory {
    public static final JoyconOutputReportFactory INSTANCE = new JoyconOutputReportFactory();

    private byte[] report = new byte[50];
    private byte[] dummyReport = new byte[50];

    private JoyconOutputReportFactory(){}

    public JoyconOutputReportFactory setOutputReportID(byte id){
        report[0] = id;
        return this;
    }

    public JoyconOutputReportFactory setOutoutReportData(byte[] data){
        System.arraycopy(data, 0, report, 1, data.length);
        return this;
    }

    public JoyconOutputReportFactory setSubcommandID(byte subcommandID){
        this.report[10] = subcommandID;
        return this;
    }

    public JoyconOutputReportFactory setSubcommandArg(byte arg){
        this.report[11] = arg;
        return this;
    }

    public JoyconOutputReportFactory setSubcommandArgs(byte[] args){
        System.arraycopy(args, 0, this.report, 11, args.length);
        return this;
    }

    public void sendTo(Joycon joycon){
        JoyconManager.LOGGER.debug("Sending output report with report id {} and subcommand {} with args {{}}",
                String.format("0x%04x", this.report[0]),
                String.format("0x%04x", this.report[10]),
                Arrays.toString(Arrays.copyOfRange(this.report, 11, this.report.length - 1)));
        int resp = joycon.getDevice().setOutputReport(this.report[0], this.report, this.report.length - 2);
        JoyconManager.LOGGER.debug("Output report response: {}", resp);
        this.report = new byte[50];
        joycon.getDevice().setOutputReport((byte)0x0, this.report, this.report.length - 2);
    }
}
