package couch.joycouch.io.input;

import couch.joycouch.JoyconManager;
import couch.joycouch.io.input.delegate.HandlerData;
import couch.joycouch.io.input.delegate.JoyconInputHandlerDelegate;

import java.util.*;

public class JoyconInputHandlerThread extends Thread{
    private Map<JoyconInputHandlerDelegate, HandlerData> hidInputHandlers = new HashMap<>();
    private boolean updating = false;

    public JoyconInputHandlerThread(){
        super("Input-Handler-Thread");
    }

    public void addHIDInputHandler(JoyconInputHandlerDelegate handler, HandlerData data){
        updating = true;
        if(handler == null || data == null) throw new NullPointerException(String.format("%s cannot be null", handler == null ? "Handler delegate" : "Handler data"));
        JoyconManager.LOGGER.debug("Adding handler delegate with report id {} and subcommand id {}", String.format("0x%04x", data.getReportID()), String.format("0x%04x", data.getReportData()[14]));
        this.hidInputHandlers.put(handler, data);
        updating = false;
    }

    @Override
    public void run(){
        while(true) {
            if(updating) continue;
            Iterator<JoyconInputHandlerDelegate> iterator = hidInputHandlers.keySet().iterator();
            while(iterator.hasNext()) {
                JoyconInputHandlerDelegate next = iterator.next();
                HandlerData data = hidInputHandlers.get(next);
                next.handleDelegatedInput(data.getSource(), data.getReportID(), data.getReportData(), data.getReportLength());
                hidInputHandlers.remove(next);
            }
        }
    }
}
