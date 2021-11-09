package com.bluecc.endpoint.functions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Configuration
@Slf4j
public class EventReceiver {
    @Bean
    public Consumer<Message<String>> sagasEvent() {
        return (value) -> {
            log.info("header: {}", value.getHeaders());
            if(value.getHeaders().containsKey("serial")){
                byte[] serial=(byte[])value.getHeaders().get("serial");
                System.out.println("serial: "+ new String(serial, StandardCharsets.UTF_8));
            }
            log.info("payload: {}", value.getPayload());
        };
    }
}
