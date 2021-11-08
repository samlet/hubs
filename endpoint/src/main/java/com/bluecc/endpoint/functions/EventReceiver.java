package com.bluecc.endpoint.functions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class EventReceiver {
    @Bean
    public Consumer<String> sagasEvent(){
        return (value) -> log.info("Event : " + value);
    }
}
