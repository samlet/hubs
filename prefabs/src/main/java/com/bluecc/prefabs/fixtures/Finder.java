package com.bluecc.prefabs.fixtures;

import com.bluecc.prefabs.fixtures.ServiceCaller.*;
import com.bluecc.prefabs.kafka.Receiver;
import com.google.common.collect.Maps;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Singular;

import java.util.Map;

import static com.bluecc.prefabs.fixtures.ServiceCaller.prepare;

public class Finder {

    @Data
    @Builder
    public static class PerformFindListParams{
        String entityName;
        int viewIndex;
        int viewSize;
        @Singular
        Map<String, Object> inputFields= Maps.newHashMap();
    }

    @Data
    @Builder
    @EqualsAndHashCode(callSuper = true)
    static class PerformFindList extends ServiceBase{
        PerformFindListParams params;

        public String send(CallContext ctx, Receiver.ReceiveCallback callback) {
            ServiceEnvelope<PerformFindListParams> msg = ServiceEnvelope.<PerformFindListParams>builder()
                    .serviceName("performFindList")
                    .serviceInParams(params)
                    .build();

            return sendImpl(ctx, callback, msg);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        CallContext ctx = prepare(()-> System.exit(0));

        String orderId="WSCO10112";

        PerformFindList.builder()
                .params(PerformFindListParams.builder()
                        .entityName("OrderHeader")
                        .viewIndex(0)
                        .viewSize(5)
                        .inputField("orderId", orderId)
                        .build())
                .build()
                .send(ctx, record -> {
                    System.out.println(".. response: "+record.value());
                });

        // OrderAndContactMech
        PerformFindList.builder()
                .params(PerformFindListParams.builder()
                        .entityName("OrderAndContactMech")
                        .viewIndex(0)
                        .viewSize(5)
                        .inputField("orderId", orderId)
                        .build())
                .build()
                .send(ctx, record -> {
                    System.out.println(".. response: "+record.value());
                });

        System.out.println(".. wait response");
    }
}
