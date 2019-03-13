package couch.joycouch.buttons;

public class ButtonStatus {
    private int
            leftButtonStatus,
            sharedButtonStatus,
            rightButtonStatus;

    public ButtonStatus(int right, int shared, int left){
        this.leftButtonStatus = left;
        this.sharedButtonStatus = shared;
        this.rightButtonStatus = right;
    }

    public JoyconButtons getActiveLeftButton(){
        JoyconButtons ret = null;
        if(leftButtonStatus != 0) {
            ret = JoyconButtons.getButton(leftButtonStatus, 2);
        }
        return ret;
    }

    public JoyconButtons getActiveSharedButton(){
        JoyconButtons ret = null;
        if(sharedButtonStatus != 0) {
            ret = JoyconButtons.getButton(sharedButtonStatus, 1);
        }
        return ret;
    }

    public JoyconButtons getActiveRightButton(){
        JoyconButtons ret = null;
        if(rightButtonStatus != 0) {
            ret = JoyconButtons.getButton(rightButtonStatus, 0);
        }
        return ret;
    }
}
