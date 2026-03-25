package com.example.stripe_paymentservice.model;

import com.example.stripe_paymentservice.dtos.BusinessType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "seller_payment_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerPaymentProfile {

    @Id
    private String id;

    private String businessProfileId;
    private String userId;

    private BusinessType businessType;
    private String businessName;
    private String email;

    private String stripeConnectedAccountId;

    @Builder.Default
    private Boolean chargesEnabled = false;

    @Builder.Default
    private Boolean payoutsEnabled = false;

    @Builder.Default
    private Boolean onboardingComplete = false;
}