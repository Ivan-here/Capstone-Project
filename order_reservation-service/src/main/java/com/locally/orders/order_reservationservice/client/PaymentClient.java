package com.locally.orders.order_reservationservice.client;

import com.locally.orders.order_reservationservice.dtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final RestTemplate restTemplate;

    @Value("${payment.service.base-url:http://localhost:8086}")
    private String paymentBaseUrl;

    public CreatePaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request) {
        return restTemplate.postForObject(
                paymentBaseUrl + "/api/payments/intent",
                request,
                CreatePaymentIntentResponse.class
        );
    }

    public ReleaseFundsResponse releaseFunds(ReleaseFundsRequest request) {
        return restTemplate.postForObject(
                paymentBaseUrl + "/api/payments/release",
                request,
                ReleaseFundsResponse.class
        );
    }

    public RefundPaymentResponse refundPayment(RefundPaymentRequest request) {
        return restTemplate.postForObject(
                paymentBaseUrl + "/api/payments/refund",
                request,
                RefundPaymentResponse.class
        );
    }
}