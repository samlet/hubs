package com.bluecc.prefabs.weaver.task;

import com.bluecc.hubs.common.Helper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Builder;
import lombok.Data;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

@Component
public class SimpleAsyncAdapter implements JavaDelegate {
    public static final Gson GSON = new GsonBuilder()
            // .setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES)
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .registerTypeAdapter(LocalDateTime.class, new Helper.LocalDateTimeAdapter().nullSafe())
            .setPrettyPrinting()
            .create();

    @Autowired
    private StreamBridge streamBridge;

    void send(String fn, Object data){
        String body=GSON.toJson(data);
        streamBridge.send("sagas",
                MessageBuilder.withPayload(body)
                        .setHeader("type", "application/json")
                        .setHeader("fn", fn)
                        .build());
    }

    @Data
    @Builder
    static class UserAgent{
        String name;
    }

    @Override
    public void execute(DelegateExecution ctx) throws Exception {
        System.out.println("ping with '" + ctx.getVariable("name") + "'");
        send("ping", UserAgent.builder().name("samlet").build());
    }
}

