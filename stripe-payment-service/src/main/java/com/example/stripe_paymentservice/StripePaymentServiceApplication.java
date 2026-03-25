package com.example.stripe_paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class StripePaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StripePaymentServiceApplication.class, args);
    }

}
