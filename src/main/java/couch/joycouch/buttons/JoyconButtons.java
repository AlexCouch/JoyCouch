package couch.joycouch.buttons;

public enum JoyconButtons {
    //Left Buttons
    UP("UP", 4, 1, 0),
    DOWN("DOWN", 2, 1, 0),
    LEFT("LEFT", 1, 1, 0),
    RIGHT("RIGHT", 8, 1, 0),
    SR_L("SR_L", 32, 1, 0),
    SL_L("SL_L", 16, 1, 0),

    L("L", 64, 2, 0),
    ZL("ZL", -128, 2, 0),
    MINUS("MINUS", 1, 2, 0),
    CAPTURE("CAPTURE", 32, 2, 0),

    //Right Buttons
    A("A", 1, 1, 1),
    B("B", 4, 1, 1),
    X("X", 2, 1, 1),
    Y("Y", 8, 1, 1),
    SR_R("SR_R", 32, 1, 1),
    SL_R("SL_R", 16, 1, 1),

    R("R", 64, 2, 1),
    RZ("RZ", -128, 2, 1),
    PLUS("PLUS", 2, 2, 1),
    HOME("HOME", 16, 2, 1)
    ;

    private String name;
    private int code;
    private int reportIndex;
    private int side; //0 for left, 1 for right

    JoyconButtons(String name, int code, int reportIndex, int side){
        this.name = name;
        this.code = code;
        this.reportIndex = reportIndex;
        this.side = side;
    }

    public String getName(){ return this.name; }
    public int getCode(){ return this.code; }
    public int getReportIndex(){ return this.reportIndex; }
    public int getSide(){ return this.side; }
}
