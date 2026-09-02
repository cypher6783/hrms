package com.hospital.resource;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class HospitalResourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalResourceApplication.class, args);
    }
}
