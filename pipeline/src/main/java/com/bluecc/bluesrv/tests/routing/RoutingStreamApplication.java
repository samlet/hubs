package com.bluecc.bluesrv.tests.routing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Consumer;

/*
$ kafka-console-producer --broker-list localhost:9092 --topic functionRouter-in-0
>555
: Resolved function from provided [routing-expression]  T(java.lang.System).nanoTime() % 2 == 0 ? 'even' : 'odd'
EVEN: 555
 */
@SpringBootApplication
public class RoutingStreamApplication {

    public static void main(String[] args) {
        // 作为应用程序属性被传递（例如，spring.cloud.function.routing-expression=headers['type']
        SpringApplication.run(RoutingStreamApplication.class,
                "--spring.cloud.function.routing-expression="
                        + "T(java.lang.System).nanoTime() % 2 == 0 ? 'even' : 'odd'");
    }
    @Bean
    public Consumer<Integer> even() {
        return value -> System.out.println("EVEN: " + value);
    }

    @Bean
    public Consumer<Integer> odd() {
        return value -> System.out.println("ODD: " + value);
    }
}
