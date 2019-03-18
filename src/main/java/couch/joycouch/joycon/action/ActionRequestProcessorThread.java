package couch.joycouch.joycon.action;

import couch.joycouch.joycon.Joycon;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ActionRequestProcessorThread extends Thread{
    private final Joycon jc;

    private final Queue<Joycon.ActionRequest> actionRequests = new ConcurrentLinkedQueue<>();

    public ActionRequestProcessorThread(Joycon jc){
        super("Action-Request-Processor-" + jc.getName());
        this.jc = jc;
    }

    public void requestAction(Joycon.ActionRequest actionRequest){
        this.actionRequests.add(actionRequest);
    }

    public void shutdown(){
        this.actionRequests.clear();
    }

    @Override
    public synchronized void run() {
        synchronized (this.actionRequests) {
            while (this.jc.isRunning()) {
                Joycon.ActionRequest request = this.actionRequests.poll();
                if (request != null) {
                    request.requestAction(this.jc);
                }
            }
        }
    }
}
