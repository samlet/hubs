package com.bluecc.endpoint;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Method;
import java.util.Set;

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class);

        App app=new App();
        // for (Method m : app.getDateDeprecatedMethods()) {
        //     System.out.println(m.getName());
        // }
        for (Method m : app.getFunctions()) {
            System.out.format("- %s:%s -> %s\n",
                    m.getDeclaringClass().getName(),
                    m.getName(),
                    m.getReturnType().getName());
        }
    }

    public Set<Method> getDateDeprecatedMethods() {
        Reflections reflections = new Reflections(
                "java.util.Date",
                new MethodAnnotationsScanner());
        return reflections.getMethodsAnnotatedWith(Deprecated.class);
    }

    public Set<Method> getFunctions() {
        Reflections reflections = new Reflections(
                "com.bluecc.endpoint",
                new MethodAnnotationsScanner());
        return reflections.getMethodsAnnotatedWith(Bean.class);
    }
}
