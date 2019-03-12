package couch.joycouch;

import couch.joycouch.buttons.JoyconButtons;
import couch.joycouch.joycon.Joycon;

public class JoyconTest {
    public static void main(String[] args){
        Joycon joycon = JoyconManager.INSTANCE.pairJoycon();
        joycon.setInputReportHandler(report -> {
            JoyconButtons button = report.getActiveButton();
            if(button != null){
                if(button.getName().equals("ZR") || button.getName().equals("ZL")){
                    report.getJoycon().rumbleJoycon();
                }
            }
        });
    }
}
