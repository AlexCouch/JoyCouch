package couch.joycouch.joycon;

import couch.joycouch.analog.AnalogStickStatus;
import couch.joycouch.buttons.ButtonStatus;

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
     * A more comprehensive report on the button statuses. Include information for both left and right JoyCons.
     * <br><br>
     * This allows you to get the buttons of any JoyCon that sends input. It is safe because the right will<br>
     * only send right JoyCon button statuses. So a right JoyCon input handler cannot receive nor handle button statuses<br>
     * from the left JoyCon. This avoids the unneccessary use of bytes and ints to get Buttons, and this also allows you<br>
     * to have shared input reports for handling multi-button inputs. However, multi-button inputs is still in the works<br>
     * since the JoyCon adds bitwise-and's the two active buttons together. The {@link ButtonStatus} will need to support<br>
     * detecting this in the future (as the time of this writing).
     *
     * @see ButtonStatus
     */
    private ButtonStatus buttonStatus = null;
    /**
     * A more comprehensive report on the analog stick statuses. Includes information for both left and right sticks.
     * <br><br>
     * This allows you to have access to the status of both analog sticks and their horizontal and vertical axes.<br>
     * Having these axes allows you to accurately translate analog pos to in-game maths, smoothly. This is good for smooth<br>
     * camera movement.
     */
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
