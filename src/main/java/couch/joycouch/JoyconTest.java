package couch.joycouch;

import couch.joycouch.analog.AnalogStickPos;
import couch.joycouch.buttons.JoyconButtons;
import couch.joycouch.handlers.JoyconAnalogInputHandler;
import couch.joycouch.handlers.JoyconButtonInputHandler;

public class JoyconTest {
    public static void main(String[] args){
        JoyconManager.INSTANCE.pairJoycons();
        JoyconManager.INSTANCE.getPairedJoycons().forEach(j -> {
            j.setJoyconButtonInputHandler(new JoyconButtonInputHandlerImpl());
            j.setJoyconAnalogInputHandler(new JoyconAnalogInputHandlerImpl());
        });
    }

    private static class JoyconButtonInputHandlerImpl implements JoyconButtonInputHandler {

        @Override
        public void handleButtonInput(Joycon jc, JoyconButtons button) {
            if(button != null) {
                if (button.getName().equals("R") || button.getName().equals("L")) {
                    jc.rumbleJoycon(160, 320, 0.6f);
                }
            }
        }
    }

    private static class JoyconAnalogInputHandlerImpl implements JoyconAnalogInputHandler{

        @Override
        public void handleAnalogInput(Joycon source, AnalogStickPos analogStickPos) {
            System.out.println("Analog Input Data: ");
            System.out.println("\tAnalog X: " + analogStickPos.getX());
            System.out.println("\tAnalog Y: " + analogStickPos.getY());
            System.out.println("\tAnalog Direction: " + analogStickPos.name());
        }
    }
}
