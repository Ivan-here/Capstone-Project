package com.example.stripe_paymentservice;

import com.example.stripe_paymentservice.clients.OrderServiceClient;
import com.example.stripe_paymentservice.dtos.VerifyPaymentRequest;
import com.example.stripe_paymentservice.repository.SellerPaymentProfileRepository;
import com.example.stripe_paymentservice.service.StripePaymentService;
import com.stripe.model.PaymentIntent;
import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PaymentSecurityTest {
    @Test void paymentMustMatchStatusAmountCurrencyOrderAndSeller() throws Exception {
        var service = new StripePaymentService(mock(SellerPaymentProfileRepository.class), mock(OrderServiceClient.class));
        var request = new VerifyPaymentRequest("order1", "pi_order", 1000L, "cad", "seller");
        PaymentIntent intent = new PaymentIntent();
        intent.setStatus("succeeded"); intent.setAmountReceived(1000L); intent.setCurrency("cad");
        intent.setMetadata(Map.of("orderId", "order1", "sellerUserId", "seller"));
        try(var stripe = mockStatic(PaymentIntent.class)) {
            stripe.when(() -> PaymentIntent.retrieve("pi_order")).thenReturn(intent);
            assertTrue(service.verifyPayment(request));
            intent.setStatus("requires_payment_method"); assertFalse(service.verifyPayment(request));
            intent.setStatus("succeeded"); intent.setAmountReceived(1L); assertFalse(service.verifyPayment(request));
            intent.setAmountReceived(1000L); intent.setCurrency("usd"); assertFalse(service.verifyPayment(request));
            intent.setCurrency("cad"); intent.setMetadata(Map.of("orderId", "other", "sellerUserId", "seller")); assertFalse(service.verifyPayment(request));
            intent.setMetadata(Map.of("orderId", "order1", "sellerUserId", "other")); assertFalse(service.verifyPayment(request));
        }
    }
    @Test void unsignedWebhookCannotChangeAnOrder() {
        var orders = mock(OrderServiceClient.class);
        var service = new StripePaymentService(mock(SellerPaymentProfileRepository.class), orders);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "webhookSecret", "whsec_test_only");
        assertThrows(Exception.class, () -> service.handleWebhook("{\"type\":\"payment_intent.succeeded\"}", "t=1,v1=forged"));
        verifyNoInteractions(orders);
    }
}
