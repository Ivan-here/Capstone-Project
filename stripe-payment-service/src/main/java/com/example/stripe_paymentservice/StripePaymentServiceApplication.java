package com.example.stripe_paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@org.springframework.context.annotation.Import(com.locally.security.ServiceSecurityConfiguration.class)
@SpringBootApplication
public class StripePaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(StripePaymentServiceApplication.class, args);
    }

}
