package com.example.marketplaceservice;

import org.springframework.boot.SpringApplication;

public class TestMarketplaceServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(MarketplaceServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
