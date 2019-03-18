package couch.joycouch;

import couch.joycouch.joycon.Joycon;

public class ShutdownHook extends Thread {

    @Override
    public synchronized void run() {
        Joycon joycon;
        JoyconManager.LOGGER.debug("Running shutdown hook!");
        if(JoyconManager.INSTANCE.getLeft() != null){
            joycon = JoyconManager.INSTANCE.getLeft();
            joycon.reset();
        }
        if(JoyconManager.INSTANCE.getRight() != null){
            joycon = JoyconManager.INSTANCE.getRight();
            joycon.reset();
        }
    }
}
