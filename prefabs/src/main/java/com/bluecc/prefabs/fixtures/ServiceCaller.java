package com.bluecc.prefabs.fixtures;

import com.bluecc.prefabs.kafka.Receiver;
import com.bluecc.prefabs.kafka.Sender;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Arrays;
import java.util.Map;

public class ServiceCaller {
    @Data
    @Builder
    public static class InstanceConf {
        String inTopic;
        String outTopic;
    }

    @Data
    @Builder
    public static class ServiceEnvelope<T> {
        String serviceName;
        T serviceInParams;
    }

    @Data
    @Builder
    public static class TestScvParams {
        String message;
        double defaultValue;
    }

    @Data
    public static class TestScvResult {
        String resp;
    }

    @Data
    @Builder
    static class TestScv {
        TestScvParams params;

        public void send(CallContext ctx, Receiver.ReceiveCallback callback) {
            ServiceEnvelope<TestScvParams> msg = ServiceEnvelope.<TestScvParams>builder()
                    .serviceName("testScv")
                    .serviceInParams(params)
                    .build();

            byte[] callid=ctx.getSender().send(gson.toJson(msg));
            ctx.getReceiver().register(callid, callback);
        }
    }

    @Data
    @AllArgsConstructor
    public static class CallContext{
        InstanceConf conf;
        Sender sender;
        Receiver receiver;
    }

    static Gson gson = new Gson();

    public static CallContext prepare() throws InterruptedException {
        InstanceConf conf = InstanceConf.builder()
                .inTopic("bluecc-in")
                .outTopic("bluecc-out")
                .build();
        Receiver receiver=new Receiver(conf.outTopic);
        receiver.listen();
        Sender sender = new Sender(conf.inTopic, false);
        CallContext ctx=new CallContext(conf, sender, receiver);

        Thread.sleep(500);
        return ctx;
    }


    public static void main(String[] args) throws InterruptedException {
        CallContext ctx = prepare();

        TestScv.builder()
                .params(TestScvParams.builder()
                        .message("hello")
                        .defaultValue(17.8)
                        .build())
                .build()
                .send(ctx, record -> {
                    System.out.println(".. response: "+record.value());
                    System.exit(0);  // stop
                });

        System.out.println(".. wait response");
    }

}
