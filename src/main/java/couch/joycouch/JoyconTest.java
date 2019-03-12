package couch.joycouch;

import couch.joycouch.buttons.JoyconButtons;
import couch.joycouch.handlers.JoyconInputReportHandler;
import couch.joycouch.joycon.JoyconInputReport;

public class JoyconTest {
    public static void main(String[] args){
        JoyconManager.INSTANCE.init();
        JoyconManager.INSTANCE.getRight().addInputReportHandler(new JoyconButtonInputReportHandlerImpl());
    }

    private static class JoyconButtonInputReportHandlerImpl implements JoyconInputReportHandler{

        @Override
        public void handleInputReport(JoyconInputReport report) {
            JoyconButtons button = report.getActiveButton();
            if(button != null){
                if(button.getName().equals("ZR") || button.getName().equals("ZL")){
                    report.getJoycon().rumbleJoycon();
                }
            }
        }
    }
}
