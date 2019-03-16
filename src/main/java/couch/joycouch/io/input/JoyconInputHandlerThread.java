package couch.joycouch.io.input;

import couch.joycouch.io.input.delegate.HandlerData;
import couch.joycouch.io.input.delegate.JoyconInputHandlerDelegate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class JoyconInputHandlerThread extends Thread{
    private ConcurrentMap<JoyconInputHandlerDelegate, HandlerData> hidInputHandlers = new ConcurrentHashMap<>();
    private volatile boolean updating = false;

    public JoyconInputHandlerThread(){
        super("Input-Handler-Thread");
    }

    public void addHIDInputHandler(JoyconInputHandlerDelegate handler, HandlerData data){
        updating = true;
        if(handler == null || data == null) throw new NullPointerException(String.format("%s cannot be null", handler == null ? "Handler delegate" : "Handler data"));
//        JoyconManager.LOGGER.debug("Adding handler delegate with report id {} and subcommand id {}", String.format("0x%04x", data.getReportID()), String.format("0x%04x", data.getReportData()[14]));
        this.hidInputHandlers.put(handler, data);
        updating = false;
    }

    @Override
    public synchronized void run(){
        while(true) {
            if(updating) continue;
            Iterator<Map.Entry<JoyconInputHandlerDelegate, HandlerData>> iterator = hidInputHandlers.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry<JoyconInputHandlerDelegate, HandlerData> next = iterator.next();
                JoyconInputHandlerDelegate delegate = next.getKey();
                HandlerData data = next.getValue();
                iterator.remove();
                delegate.handleDelegatedInput(data.getSource(), data.getReportID(), data.getReportData(), data.getReportLength());
            }
        }
    }
}
