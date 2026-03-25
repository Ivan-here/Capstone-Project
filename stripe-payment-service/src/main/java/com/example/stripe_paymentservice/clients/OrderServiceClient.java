package com.example.stripe_paymentservice.clients;

import com.example.stripe_paymentservice.dtos.PaymentSucceededRequest;
import com.example.stripe_paymentservice.dtos.PaymentSucceededResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class OrderServiceClient {

    private final RestTemplate restTemplate;

    @Value("${clients.orderBaseUrl}")
    private String orderBaseUrl;

    public PaymentSucceededResponse markPaymentSucceeded(String orderId, PaymentSucceededRequest request) {
        return restTemplate.postForObject(
                orderBaseUrl + "/api/internal/orders/" + orderId + "/payment-succeeded",
                request,
                PaymentSucceededResponse.class
        );
    }
}