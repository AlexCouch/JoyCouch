package couch.joycouch;

import couch.joycouch.analog.AnalogStick;
import couch.joycouch.analog.AnalogStickStatus;
import couch.joycouch.buttons.ButtonStatus;
import couch.joycouch.buttons.JoyconButtons;
import couch.joycouch.handlers.JoyconInputReportHandler;
import couch.joycouch.joycon.JoyconInputReport;

public class JoyconTest {
    public static void main(String[] args){
        JoyconManager.INSTANCE.init();
        JoyconManager.INSTANCE.setInputFrequency(100);
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
            ButtonStatus buttonStatus  = report.getButtonStatus();
            JoyconButtons button = buttonStatus.getActiveRightButton();
            if(button != null){
                if(button.getName().equals("ZR")){
                    report.getJoycon().rumbleJoycon();
                }
            }
            AnalogStickStatus stickStatus = report.getAnalogStickStatus();
            AnalogStick stick = stickStatus.getRightAnalogStick();
            System.out.println("Right horizontal: " + stick.getHorizontal());
            System.out.println("Right vertical: " + stick.getVertical());
            System.out.println();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static class LeftJoyconButtonHandler implements JoyconInputReportHandler{

        @Override
        public void handleInputReport(JoyconInputReport report) {
            ButtonStatus buttonStatus = report.getButtonStatus();
            JoyconButtons button = buttonStatus.getActiveLeftButton();
            if(button != null){
                if(button.getName().equals("ZL")){
                    report.getJoycon().rumbleJoycon();
                }
            }
        }
    }
}
