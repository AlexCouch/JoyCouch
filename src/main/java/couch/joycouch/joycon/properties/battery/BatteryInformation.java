package couch.joycouch.joycon.properties.battery;

public class BatteryInformation {
    private BatteryStatus batteryStatus;
    private int batteryVoltage;
    private float batteryPercentage;
    private boolean charging = false; //Not implemented yet

    public BatteryInformation(BatteryStatus batteryStatus, int batteryVoltage, float batteryPercentage){
        this.batteryStatus = batteryStatus;
        this.batteryVoltage = batteryVoltage;
        this.batteryPercentage = batteryPercentage;
    }

    public static BatteryInformation getBatteryInformation(byte[] data){
        int b = (data[0] & 0xFF) | (data[1] << 8);
        int voltage = (int)(b * 2.5F);
        BatteryStatus status = BatteryStatus.getBatteryStatusFrom(voltage);
        float percentage = ((voltage - 3300F)*100) / (4200F - 3300F);
        return new BatteryInformation(status, voltage, percentage);
    }

    public BatteryStatus getBatteryStatus(){ return this.batteryStatus; }
    public int getBatteryVoltage(){ return this.batteryVoltage; }
    public float getBatteryPercentage(){ return this.batteryPercentage; }
}
