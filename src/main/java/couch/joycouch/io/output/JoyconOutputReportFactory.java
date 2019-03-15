package couch.joycouch.io.output;

import couch.joycouch.joycon.Joycon;

public class JoyconOutputReportFactory {
    public static final JoyconOutputReportFactory INSTANCE = new JoyconOutputReportFactory();

    private byte[] report = new byte[50];

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
        joycon.getDevice().setOutputReport(this.report[0], this.report, this.report.length);
    }
}
