package couch.joycouch;

import couch.joycouch.analog.AnalogStickPos;
import couch.joycouch.buttons.JoyconButtons;
import couch.joycouch.handlers.JoyconAnalogInputHandler;
import couch.joycouch.handlers.JoyconButtonInputHandler;
import couch.joycouch.joycon.*;

public class JoyconTest {
    public static void main(String[] args){
        SingleJoycon jc1 = JoyconManager.INSTANCE.pairJoycon();
        SingleJoycon jc2 = JoyconManager.INSTANCE.pairJoycon();
        CombinedJoycon cjc = JoyconManager.INSTANCE.combineJoycons(jc1, jc2);
        cjc.setButtonInputHandler(new JoyconButtonInputHandlerImpl());
        cjc.setAnalogInputHandler(new JoyconAnalogInputHandlerImpl());
    }

    private static class JoyconButtonInputHandlerImpl implements JoyconButtonInputHandler {

        @Override
        public void handleButtonInput(SingleJoycon jc, JoyconButtons button) {
            if(button != null) {
                if (button.getName().equals("R") || button.getName().equals("L")) {
                    jc.rumbleJoycon(160, 320, 0.6f);
                }
            }
        }
    }

    private static class JoyconAnalogInputHandlerImpl implements JoyconAnalogInputHandler{

        @Override
        public void handleAnalogInput(SingleJoycon source, AnalogStickPos analogStickPos) {
            System.out.println("Analog Input Data: ");
            System.out.println("\tAnalog X: " + analogStickPos.getX());
            System.out.println("\tAnalog Y: " + analogStickPos.getY());
            System.out.println("\tAnalog Direction: " + analogStickPos.name());
            System.out.println("\tAnalog Side: " + source.getSide());
        }
    }
}
