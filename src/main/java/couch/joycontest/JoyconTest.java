package couch.joycontest;

public class JoyconTest {
    public static void main(String[] args){
        JoyconManager.INSTANCE.pairJoycons();
//        JoyconManager.INSTANCE.getPairedJoycons().forEach(j -> j.setJoyconUnsafeInputHandler(new JoyconAnalogStickInputHandler()));
    }

    private static class JoyconInputHandlerImpl implements JoyconInputHandler{

        @Override
        public void handleInput(Joycon jc, JoyconCodes button) {
            System.out.println("Button name: " + button.getName());
            System.out.println("Button Code: " + button.getCode());
            System.out.println("Button Report Index: " + button.getReportIndex());
            System.out.println("Button Side: " + (button.getSide() == 0 ? "left" : "right"));

            if(button.getName().equals("R") || button.getName().equals("L")){
                jc.rumbleJoycon(160, 320, 0.6f);
            }
        }
    }
}
