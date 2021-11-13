package com.bluecc.prefabs.dummy.jaeger;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;

public final class JaegerExample {

    private final Tracer tracer;

    public JaegerExample(OpenTelemetry openTelemetry) {
        tracer = openTelemetry.getTracer("io.opentelemetry.example.JaegerExample");
    }

    private void myWonderfulUseCase() {
        // Generate a span
        Span span = this.tracer.spanBuilder("Start my wonderful use case").startSpan();
        span.addEvent("Event 0");
        // execute my use case - here we simulate a wait
        doWork();
        span.addEvent("Event 1");
        span.end();
    }

    private void doWork() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // do the right thing here
        }
    }

    public static void main(String[] args) {
        // Parsing the input
        if (args.length < 2) {
            System.out.println("Missing [hostname] [port]");
            System.exit(1);
        }
        String jaegerHostName = args[0];
        int jaegerPort = Integer.parseInt(args[1]);

        // it is important to initialize your SDK as early as possible in your application's lifecycle
        OpenTelemetry openTelemetry =
                ExampleConfiguration.initOpenTelemetry(jaegerHostName, jaegerPort);

        // Start the example
        JaegerExample example = new JaegerExample(openTelemetry);
        // generate a few sample spans
        for (int i = 0; i < 10; i++) {
            example.myWonderfulUseCase();
        }

        System.out.println("Bye");
    }
}

/*
# How to run

## Prerequisites
* Java 1.8+
* Docker 19.03
* Jaeger 1.16 - [Link][jaeger]


## 1 - Compile
```shell script
../gradlew shadowJar
```
## 2 - Run Jaeger

```shell script
docker run --rm -it --name jaeger\
  -p 16686:16686 \
  -p 14250:14250 \
  jaegertracing/all-in-one:1.16
```


## 3 - Start the Application
```shell script
java -cp build/libs/opentelemetry-examples-jaeger-0.1.0-SNAPSHOT-all.jar \
  io.opentelemetry.example.jaeger.JaegerExample localhost 14250
```
## 4 - Open the Jaeger UI

Navigate to http://localhost:16686
 */