package couch.joycouch.io.input;

import couch.joycouch.analog.AnalogStickStatus;
import couch.joycouch.buttons.ButtonStatus;
import couch.joycouch.joycon.Joycon;

public class JoyconInputReport {

    private Joycon joycon;

    private ButtonStatus buttonStatus = null;
    private AnalogStickStatus analogStickStatus = null;

    public JoyconInputReport(Joycon joycon){
        this.joycon = joycon;
    }

    public void setButtonStatus(ButtonStatus buttonStatus){ this.buttonStatus = buttonStatus; }
    public void setAnalogStickStatus(AnalogStickStatus analogStickStatus){ this.analogStickStatus = analogStickStatus; }

    public Joycon getJoycon(){ return this.joycon; }
    public ButtonStatus getButtonStatus(){ return this.buttonStatus; }
    public AnalogStickStatus getAnalogStickStatus(){ return this.analogStickStatus; }
}
