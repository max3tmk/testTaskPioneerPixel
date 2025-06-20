package com.max.pioneer_pixel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class PioneerPixelApplication {

    public static void main(String[] args) {
        SpringApplication.run(PioneerPixelApplication.class, args);
    }

}
