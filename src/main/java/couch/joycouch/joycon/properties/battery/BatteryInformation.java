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

    public BatteryStatus getBatteryStatus(){ return this.batteryStatus; }
    public int getBatteryVoltage(){ return this.batteryVoltage; }
    public float getBatteryPercentage(){ return this.batteryPercentage; }
}
