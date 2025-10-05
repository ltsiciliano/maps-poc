package com.example.mapspoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class MapsPocApplication {
    public static void main(String[] args) {
        SpringApplication.run(MapsPocApplication.class, args);
    }
}
