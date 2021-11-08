package com.bluecc.bluesrv.tests.resolver;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.binding.BinderAwareChannelResolver;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@SpringBootApplication
@Controller
public class SourceWithDynamicDestination {
    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir") );
        SpringApplication.run(SourceWithDynamicDestination.class);
    }

    @Autowired
    private BinderAwareChannelResolver resolver;

    @RequestMapping(value="/{target}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void send(@RequestBody String body, @PathVariable("target") String target){
        resolver.resolveDestination(target).send(new GenericMessage<String>(body));
    }
}

/*
# 目的地“客户”和“订单”是在代理中（在 Rabbit 的交换中或在 Kafka 的主题中）创建的，名称为“客户”和“订单”，并将数据发布到适当的目的地。
$ curl -H "Content-Type: application/json" -X POST -d "customer-1" ttp://localhost:9001/customers
$ just list customers
kafka-console-consumer --bootstrap-server localhost:9092 --topic customers --from-beginning
customer-1

$ curl -H "Content-Type: application/json" -X POST -d "order-1" http://localhost:8080/orders
 */