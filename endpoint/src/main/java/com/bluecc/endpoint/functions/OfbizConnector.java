package com.bluecc.endpoint.functions;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Slf4j
@Controller
public class OfbizConnector {
    @Autowired
    private StreamBridge streamBridge;

    @Data
    @Builder
    public static class Info {
        String data;
    }

    @Bean
    Function<String, Info> info() {
        return e -> Info.builder()
                .data("1.0")
                .build();
    }

    /*
    $ curl -H "Content-Type: text/plain" -X POST -d "hello from the other side" http://localhost:9002
    $ kafka-console-consumer --bootstrap-server localhost:9092 --topic sagas --from-beginning
     */
    @RequestMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void delegateToSupplier(@RequestHeader MultiValueMap<String, String> headers,
                                   @RequestBody String body) {
        // Header 'Content-Type' = text/yaml
        // Header 'Content-Type' = application/json
        // Header 'foo' = bar
        String contentType = headers.getFirst("Content-Type");
        if (contentType == null) {
            contentType = headers.getFirst("content-type");
        }
        if (contentType == null) {
            contentType = "text/plain";
        }
        headers.forEach((key, value) -> {
            log.info(String.format(
                    "Header '%s' = %s", key, String.join("|", value)));
        });
        System.out.println("Sending " + body);
        // streamBridge.send("sagas", body);
        streamBridge.send("bluecc-in",
                MessageBuilder.withPayload(body)
                        .setHeader("type", contentType)
                        .setHeader("fn", headers.getFirst("fn"))
                        .setHeader("dataType", headers.getFirst("Data-Type"))
                        .build());
        // MimeType.valueOf("application/json")
    }

}
