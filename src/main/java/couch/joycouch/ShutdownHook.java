package couch.joycouch;

import couch.joycouch.io.input.hid.JoyconHIDSubcommandInputHandler;
import couch.joycouch.joycon.Joycon;

public class ShutdownHook extends Thread implements JoyconHIDSubcommandInputHandler {

    private boolean leftShudown = false, rightShutdown = false;

    @Override
    public synchronized void run() {
        if(JoyconManager.INSTANCE.getLeft() != null){
            JoyconManager.INSTANCE.getLeft().reset();
        }
        if(JoyconManager.INSTANCE.getRight() != null){
            JoyconManager.INSTANCE.getRight().reset();
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
