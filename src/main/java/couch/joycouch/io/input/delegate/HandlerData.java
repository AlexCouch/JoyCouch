package couch.joycouch.io.input.delegate;

import purejavahidapi.HidDevice;

public class HandlerData {
    private final HidDevice source;
    private final byte reportID;
    private final byte[] reportData;
    private final int reportLength;

    public HandlerData(HidDevice source, byte reportID, byte[] reportData, int reportLength){
        this.source = source;
        this.reportID = reportID;
        this.reportData = reportData;
        this.reportLength = reportLength;
    }

    public HidDevice getSource(){ return this.source; }
    public byte getReportID(){ return this.reportID; }
    public byte[] getReportData(){ return this.reportData; }
    public int getReportLength(){ return this.reportLength; }
}
