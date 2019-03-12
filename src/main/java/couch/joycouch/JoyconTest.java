package couch.joycouch;

import couch.joycouch.buttons.JoyconButtons;
import couch.joycouch.handlers.JoyconInputReportHandler;
import couch.joycouch.joycon.JoyconInputReport;

public class JoyconTest {
    public static void main(String[] args){
        JoyconManager.INSTANCE.init();
        JoyconManager.INSTANCE.setInputFrequency(50);
        if(JoyconManager.INSTANCE.getLeft() != null){
            JoyconManager.INSTANCE.getLeft().addInputReportHandler(new LeftJoyconButtonHandler());
        }
        if(JoyconManager.INSTANCE.getRight() != null){
            JoyconManager.INSTANCE.getRight().addInputReportHandler(new RightJoyconButtonHandler());
        }
    }

    private static class RightJoyconButtonHandler implements JoyconInputReportHandler{

        @Override
        public void handleInputReport(JoyconInputReport report) {
            if(report.getJoycon().getSide() != 0) return;
            JoyconButtons button = report.getActiveButton();
            if(button != null){
                if(button.getName().equals("ZR")){
                    report.getJoycon().rumbleJoycon();
                }
            }
        }
    }

    private static class LeftJoyconButtonHandler implements JoyconInputReportHandler{

        @Override
        public void handleInputReport(JoyconInputReport report) {
            if(report.getJoycon().getSide() != 2) return;
            JoyconButtons button = report.getActiveButton();
            if(button != null){
                if(button.getName().equals("ZL")){
                    report.getJoycon().rumbleJoycon();
                }
            }
        }
    }
}
