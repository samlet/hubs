package com.bluecc.prefabs.fixtures;

import com.bluecc.prefabs.kafka.Receiver;
import com.google.gson.Gson;

public class ServiceBase {
    static Gson gson = new Gson();
    protected <T> void sendImpl(ServiceCaller.CallContext ctx, Receiver.ReceiveCallback callback,
                            ServiceCaller.ServiceEnvelope<T> msg) {
        byte[] callid= ctx.getSender().send(gson.toJson(msg));
        ctx.getReceiver().register(callid, rec -> {
            callback.proc(rec);
            int count=ctx.getTaskCount().decrementAndGet();
            if(count==0){
                ctx.getEndProc().run();
            }
        });
        ctx.getTaskCount().getAndIncrement();
    }
}
