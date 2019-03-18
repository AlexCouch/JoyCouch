package couch.joycouch;

import couch.joycouch.io.input.hid.JoyconHIDSubcommandInputHandler;
import couch.joycouch.joycon.Joycon;

public class ShutdownHook extends Thread implements JoyconHIDSubcommandInputHandler {

    private boolean leftShudown = false, rightShutdown = false;

    @Override
    public synchronized void run() {
        Joycon joycon;
        if(JoyconManager.INSTANCE.getLeft() != null){
            joycon = JoyconManager.INSTANCE.getLeft();
            joycon.requestAction(Joycon::shutdown);
        }
        if(JoyconManager.INSTANCE.getRight() != null){
            joycon = JoyconManager.INSTANCE.getRight();
            joycon.requestAction(Joycon::shutdown);
        }
    }

    @Override
    public byte getSubcommandID() {
        return 0x48;
    }

    @Override
    public void handleSubcommandInput(Joycon joycon, byte[] subcommandInput) {
        if(joycon.getSide() == 0) this.rightShutdown = true;
        else if(joycon.getSide() == 2) this.leftShudown = true;

        if(this.leftShudown && this.rightShutdown) JoyconManager.INSTANCE.getShutdownHook().notify();
    }
}
