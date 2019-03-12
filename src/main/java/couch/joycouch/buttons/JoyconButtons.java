package couch.joycouch.buttons;

public enum JoyconButtons {
    //Left Buttons
    UP("UP", 0x02, 1, 2),
    DOWN("DOWN", 0x01, 1, 2),
    LEFT("LEFT", 0x08, 1, 2),
    RIGHT("RIGHT", 0x04, 1, 2),
    SR_L("SR_L", 0x10, 1, 2),
    SL_L("SL_L", 0x20, 1, 2),

    L("L", 0x40, 2, 2),
    ZL("ZL", -128, 2, 2),

    //Right Buttons
    A("A", 0x08, 1, 0),
    B("B", 0x04, 1, 0),
    X("X", 0x02, 1, 0),
    Y("Y", 0x01, 1, 0),
    SR_R("SR_R", 0x10, 1, 0),
    SL_R("SL_R", 0x20, 1, 0),

    R("R", 0x40, 2, 0),
    RZ("ZR", -128, 2, 0),

    //Shared
    MINUS("MINUS", 0x01, 2, 1),
    CAPTURE("CAPTURE", 0x20, 2, 1),
    PLUS("PLUS", 0x02, 2, 1),
    HOME("HOME", 0x10, 2, 1)
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

    public static JoyconButtons getButton(int buttonInt, int side){
        for(JoyconButtons button : JoyconButtons.values()){
            if(button.code == buttonInt && button.side == side){
                return button;
            }
        }
        return null;
    }
}
