package couch.joycouch.joycon;

import couch.joycouch.analog.AnalogStickPos;
import couch.joycouch.buttons.JoyconButtons;

/**
 * This JoyconInputReport represents an abstraction of the HID input report.<br>
 * <br>
 * This input report is always constructed from the JoyCon full standard input report.<br>
 * The report contains information pertaining to the bluetooth latency, connection info, battery life,<br>
 * button status for left, right, and shared (all in their own separate bytes), analog stick status for left and right<br>
 * (three different bytes, still WIP), and gyroscope and accelerometer information.<br>
 * <br>
 * At the time of this writing, this report does not contain gyro/accel <br>
 * information, nor does it contain properly calculated/calibrated analog stick information.<br>
 * <br>
 * This input report is passed into a {@link couch.joycouch.handlers.JoyconInputReportHandler JoyconInputReportHandler},
 * which is implemented by users of this API <br>
 * for handling input.
 *
 * @author Alex Couch
 * @version 0.0.1
 * @see couch.joycouch.handlers.JoyconInputReportHandler
 */
public class JoyconInputReport {
    /**
     * The Joycon sending the input report
     */
    private Joycon joycon;
    /**
     * The button being pressed, if any
     */
    private JoyconButtons button = null;
    /**
     * The analog stick position, if any
     */
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
