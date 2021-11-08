package com.bluecc.bluesrv.tests.headers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;

import java.util.function.Function;

@SpringBootApplication
public class DemoStreamApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoStreamApplication.class,
                "--spring.cloud.function.definition=enrich|echo",
                "--spring.cloud.stream.function.bindings.enrich|echo-in-0=input",
                "--spring.cloud.stream.bindings.input.destination=myDestination",
                "--spring.cloud.stream.bindings.input.group=myGroup");
    }

    @Bean
    public Function<Message<String>, Message<String>> enrich() {
        return message -> {
            Assert.isTrue(!message.getHeaders().containsKey("foo"), "Should NOT contain 'foo' header");
            return MessageBuilder.fromMessage(message).setHeader("foo", "bar").build();
        };
    }

    /**
     * 从http直接访问echo时, 会自动设置header的foo属性, (实际使用任何名称访问, 都会导航到echo函数, 并附带了foo标头
     * 从kafka-producer-cli上发消息到myDestination上时, 因为加了enrich作为前置filter,
     * 会自动加上foo标头.
     *
     * <pre>
     * kafka-console-producer --broker-list localhost:9092 --topic myDestination
     *      >myDestination
     *      >皮肤较黑
     *
     * Incoming message GenericMessage [payload=皮肤较黑, headers={deliveryAttempt=1,
     *  foo=bar, kafka_timestampType=CREATE_TIME, kafka_receivedTopic=myDestination,
     *  target-protocol=kafka, kafka_offset=8, kafka_consumer=org.apache.kafka.clients.consumer.KafkaConsumer@4055ab04, id=7f25d02f-ad55-9030-6925-263c3ca60851, kafka_receivedPartitionId=0, kafka_receivedTimestamp=1636269887542, contentType=application/json, kafka_groupId=myGroup, timestamp=1636269888554}]
     * </pre>
     *
     * @return function
     */
    @Bean
    public Function<Message<String>, Message<String>> echo() {
        return message -> {
            Assert.isTrue(message.getHeaders().containsKey("foo"), "Should contain 'foo' header");
            System.out.println(".. Incoming message " + message);
            return message;
        };
    }
}
