package com.example.listingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@org.springframework.context.annotation.Import(com.locally.security.ServiceSecurityConfiguration.class)
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class ListingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ListingServiceApplication.class, args);
        System.out.println("--- STARTING LISTING SERVICE: VERSION 2 (HARDCODED URL) ---"); // <--- Add this
    }

}
