package couch.joycouch;

import couch.joycouch.analog.AnalogStick;
import couch.joycouch.analog.AnalogStickStatus;
import couch.joycouch.buttons.ButtonStatus;
import couch.joycouch.buttons.JoyconButtons;
import couch.joycouch.io.input.JoyconInputReport;
import couch.joycouch.io.input.JoyconInputReportHandler;
import couch.joycouch.joycon.Joycon;

public class JoyconTest {
    public static void main(String[] args) throws InterruptedException {
        JoyconManager.INSTANCE.setInputFrequency(50);
        JoyconManager.INSTANCE.start();
        JoyconManager.INSTANCE.join();
        if(JoyconManager.INSTANCE.getLeft() != null){
            JoyconManager.INSTANCE.getLeft().addInputReportHandler(new LeftJoyconButtonHandler());
        }
        if(JoyconManager.INSTANCE.getRight() != null){
            JoyconManager.INSTANCE.getRight().addInputReportHandler(new RightJoyconButtonHandler());
        }
//        JoyconManager.INSTANCE.start(); //Start the joycon manager thread after adding settings
    }

    private static class RightJoyconButtonHandler implements JoyconInputReportHandler{

        @Override
        public void handleInputReport(JoyconInputReport report) {
            ButtonStatus buttonStatus  = report.getButtonStatus();
            JoyconButtons button = buttonStatus.getActiveRightButton();
            if(button != null){
                if(button.getName().equals("ZR")){
                    report.getJoycon().requestAction(Joycon::rumbleJoycon);
                }
            }
            AnalogStickStatus stickStatus = report.getAnalogStickStatus();
            AnalogStick stick = stickStatus.getAnalogStick();
            JoyconManager.LOGGER.info("Right horizontal: {}\n", stick.getHorizontal());
            JoyconManager.LOGGER.info("Right vertical: {}\n\n", stick.getVertical());
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
            AnalogStickStatus stickStatus = report.getAnalogStickStatus();
            AnalogStick stick = stickStatus.getAnalogStick();
            JoyconManager.LOGGER.info("Right horizontal: {}\n", stick.getHorizontal());
            JoyconManager.LOGGER.info("Right vertical: {}\n\n", stick.getVertical());
        }
    }
}
