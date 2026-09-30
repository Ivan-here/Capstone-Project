package com.example.stripe_paymentservice.controller;

import com.stripe.exception.StripeException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> handleFeignException(FeignException ex) {
        log.error("Downstream service call failed", ex);
        String message = "Payment service is temporarily unavailable.";
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("message", message));
    }

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<Map<String, String>> handleStripeException(StripeException ex) {
        log.error("Stripe call failed", ex);
        String message = "Payment provider request failed.";
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("message", message));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        log.error("Payment service request failed", ex);
        if (ex instanceof org.springframework.web.server.ResponseStatusException status) {
            return ResponseEntity.status(status.getStatusCode()).body(Map.of("message", "Request denied."));
        }
        String message = "Invalid payment request.";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", message));
    }
}
