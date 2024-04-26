package com.systemdesign.leakybucket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class LeakyBucketApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeakyBucketApplication.class, args);
    }

}
