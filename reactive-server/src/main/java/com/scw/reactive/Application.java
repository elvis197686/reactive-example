package com.scw.reactive;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan( basePackages = { "com.scw.reactive.spring" } )
public class Application {

    public static void main(final String[] args) {
        new SpringApplicationBuilder(Application.class)
            .run(args);
    }

}
