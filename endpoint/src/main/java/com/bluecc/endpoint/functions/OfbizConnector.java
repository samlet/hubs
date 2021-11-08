package com.bluecc.endpoint.functions;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.function.Function;

@Configuration
@Slf4j
@Controller
public class OfbizConnector {
    @Autowired
    private StreamBridge streamBridge;

    @Data
    @Builder
    public static class Info{
        String data;
    }

    @Bean
    Function<String, Info> info(){
        return e -> Info.builder()
                .data("1.0")
                .build();
    }

    /*
    $ curl -H "Content-Type: text/plain" -X POST -d "hello from the other side" http://localhost:9002
    $ kafka-console-consumer --bootstrap-server localhost:9092 --topic toStream-out-0 --from-beginning
     */
    @RequestMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delegateToSupplier(@RequestBody String body) {
        System.out.println("Sending " + body);
        streamBridge.send("sagas", body);
    }
}
