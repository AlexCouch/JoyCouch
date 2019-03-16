package couch.joycouch.joycon.properties.battery;

public enum BatteryStatus {
    FULL(3900, 4200),
    MEDIUM(3760, 3899),
    LOW(3600, 3759),
    CRITICAL(3300, 3599)
    ;

    private int min, max;

    BatteryStatus(int min, int max){
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public static BatteryStatus getBatteryStatusFrom(int voltage){
        for(BatteryStatus status : BatteryStatus.values()){
            if(voltage >= status.getMin() && voltage <= status.getMax()){
                return status;
            }
        }
        return null;
    }
}
