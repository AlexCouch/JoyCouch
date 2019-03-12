package couch.joycouch.joycon;

import couch.joycouch.analog.AnalogStickPos;
import couch.joycouch.buttons.JoyconButtons;

public class JoyconInputReport {
    private Joycon joycon;
    private JoyconButtons button = null;
    private AnalogStickPos analogStickPos = null;

    public JoyconInputReport(Joycon joycon){
        this.joycon = joycon;
    }

    public void setButtonStatus(JoyconButtons button){ this.button = button; }
    public void setAnalogStickPos(AnalogStickPos analogStickPos){ this.analogStickPos = analogStickPos; }

    public Joycon getJoycon(){ return this.joycon; }
    public JoyconButtons getActiveButton(){ return this.button; }
    public AnalogStickPos getAnalogStickPos(){ return this.analogStickPos; }
}
