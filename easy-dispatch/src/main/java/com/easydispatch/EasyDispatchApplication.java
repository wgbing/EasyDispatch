package com.easydispatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class EasyDispatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyDispatchApplication.class, args);
    }
}
