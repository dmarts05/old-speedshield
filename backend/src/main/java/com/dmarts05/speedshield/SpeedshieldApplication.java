package com.dmarts05.speedshield;

import com.dmarts05.speedshield.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(JwtProperties.class)
public class SpeedshieldApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpeedshieldApplication.class, args);
    }

}
