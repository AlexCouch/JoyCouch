package couch.joycouch.io.input;

import couch.joycouch.io.input.delegate.HandlerData;
import couch.joycouch.io.input.delegate.JoyconInputHandlerDelegate;
import couch.joycouch.joycon.Joycon;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JoyconInputHandlerThread extends Thread{
    private Queue<HandlerData> inputData = new ConcurrentLinkedQueue<>();
    private JoyconInputHandlerDelegate delegate;
    private volatile boolean updating = false;

    public JoyconInputHandlerThread(Joycon joycon){
        super("Input-Handler-Thread");
        this.delegate = new JoyconInputHandlerDelegate(joycon);
    }

    public void addHIDInputHandler(HandlerData data){
        updating = true;
//        JoyconManager.LOGGER.debug("Adding handler delegate with report id {} and subcommand id {}", String.format("0x%04x", data.getReportID()), String.format("0x%04x", data.getReportData()[14]));
        this.inputData.add(data);
        updating = false;
    }

    @Override
    public synchronized void run(){
        while(true) {
            if(updating) continue;
            HandlerData data = this.inputData.poll();
            if(data == null) continue;
            this.delegate.handleDelegatedInput(data.getSource(), data.getReportID(), data.getReportData(), data.getReportLength());
        }
    }
}
