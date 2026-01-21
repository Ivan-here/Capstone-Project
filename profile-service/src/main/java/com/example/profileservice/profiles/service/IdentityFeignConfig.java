package com.example.profileservice.profiles.service;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class IdentityFeignConfig {

    @Bean
    public RequestInterceptor internalSecretHeader(
            @Value("${internal.sharedSecret}") String internalSecret
    ) {
        return requestTemplate -> requestTemplate.header("X-Internal-Secret", internalSecret);
    }
}