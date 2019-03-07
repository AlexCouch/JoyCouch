package couch.joycontest;

import purejavahidapi.*;

public class JoyconTest {
    private static byte[] default_buf = new byte[]{ 0x0, 0x1, 0x40, 0x40, 0x0, 0x1, 0x40, 0x40 };
    private static HidDevice joycon = null;

    public static void main(String[] args){
        JoyconManager.INSTANCE.pairJoycons();
        Joycon jc = JoyconManager.INSTANCE.getPairedJoycons().keySet().iterator().next();
        jc.setJoyconInputHandler(code -> {
            System.out.println("Button name: " + code.getName());
            System.out.println("Button Code: " + code.getCode());
            System.out.println("Button Report Index: " + code.getReportIndex());
            System.out.println("Button Side: " + (code.getSide() == 0 ? "left" : "right"));

            if(code.getName().equals("R") || code.getName().equals("L")){
                jc.rumbleJoycon();
            }
        });

    }
}
