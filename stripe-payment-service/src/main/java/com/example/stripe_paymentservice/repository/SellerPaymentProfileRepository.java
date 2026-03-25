package com.example.stripe_paymentservice.repository;

import com.example.stripe_paymentservice.model.SellerPaymentProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SellerPaymentProfileRepository extends MongoRepository<SellerPaymentProfile, String> {
    Optional<SellerPaymentProfile> findByBusinessProfileId(String businessProfileId);
    Optional<SellerPaymentProfile> findByUserId(String userId);
    Optional<SellerPaymentProfile> findByStripeConnectedAccountId(String stripeConnectedAccountId);
}