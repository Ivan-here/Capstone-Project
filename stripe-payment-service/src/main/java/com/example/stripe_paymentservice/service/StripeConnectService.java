package com.example.stripe_paymentservice.service;

import com.example.stripe_paymentservice.clients.ProfileServiceClient;
import com.example.stripe_paymentservice.dtos.BusinessProfile;
import com.example.stripe_paymentservice.dtos.BusinessType;
import com.example.stripe_paymentservice.model.SellerPaymentProfile;
import com.example.stripe_paymentservice.repository.SellerPaymentProfileRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.model.AccountLink;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountLinkCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StripeConnectService {

    private final SellerPaymentProfileRepository sellerPaymentProfileRepository;
    private final ProfileServiceClient profileServiceClient;

    @Value("${stripe.return-url}")
    private String returnUrl;

    @Value("${stripe.refresh-url}")
    private String refreshUrl;

    public SellerPaymentProfile createConnectedAccount(String userId) throws StripeException {
        BusinessProfile businessProfile = profileServiceClient.getBusinessProfileInternal(userId);

        if (businessProfile == null) {
            throw new RuntimeException("Business profile not found for userId: " + userId);
        }
        BusinessType type = businessProfile.getBusinessType();
        String businessProfileId = businessProfile.getId();

        SellerPaymentProfile existing = sellerPaymentProfileRepository
                .findByBusinessProfileId(businessProfileId)
                .orElse(null);

        if (existing != null &&
                existing.getStripeConnectedAccountId() != null &&
                !existing.getStripeConnectedAccountId().isBlank()) {
            return existing;
        }

        if (!isSupportedSellerType(type)) {
            throw new RuntimeException("Only FARMER or RESTAURANT business profiles can create Stripe connected accounts");
        }

        if (businessProfile.getEmail() == null || businessProfile.getEmail().isBlank()) {
            throw new RuntimeException("Business profile email is required");
        }

        AccountCreateParams params = AccountCreateParams.builder()
                .setType(AccountCreateParams.Type.EXPRESS)
                .setEmail(businessProfile.getEmail())
                .build();

        Account account = Account.create(params);

        SellerPaymentProfile profile = existing != null ? existing : new SellerPaymentProfile();
        profile.setUserId(businessProfile.getUserId());
        profile.setBusinessProfileId(businessProfile.getId());
        profile.setBusinessType(businessProfile.getBusinessType());
        profile.setBusinessName(businessProfile.getBusinessName());
        profile.setEmail(businessProfile.getEmail());
        profile.setStripeConnectedAccountId(account.getId());
        profile.setChargesEnabled(false);
        profile.setPayoutsEnabled(false);
        profile.setOnboardingComplete(false);

        return sellerPaymentProfileRepository.save(profile);
    }

    public String createOnboardingLink(String userId) throws StripeException {
        BusinessProfile businessProfile = profileServiceClient.getBusinessProfileInternal(userId);

        if (businessProfile == null) {
            throw new RuntimeException("Business profile not found for userId: " + userId);
        }

        SellerPaymentProfile profile = sellerPaymentProfileRepository
                .findByBusinessProfileId(businessProfile.getId())
                .orElseThrow(() -> new RuntimeException("Seller payment profile not found"));

        AccountLinkCreateParams params = AccountLinkCreateParams.builder()
                .setAccount(profile.getStripeConnectedAccountId())
                .setRefreshUrl(refreshUrl + "?userId=" + userId)
                .setReturnUrl(returnUrl + "?userId=" + userId)
                .setType(AccountLinkCreateParams.Type.ACCOUNT_ONBOARDING)
                .build();

        AccountLink accountLink = AccountLink.create(params);
        return accountLink.getUrl();
    }

    public SellerPaymentProfile refreshStatus(String userId) throws StripeException {
        BusinessProfile businessProfile = profileServiceClient.getBusinessProfileInternal(userId);

        if (businessProfile == null) {
            throw new RuntimeException("Business profile not found for userId: " + userId);
        }

        SellerPaymentProfile profile = sellerPaymentProfileRepository
                .findByBusinessProfileId(businessProfile.getId())
                .orElseThrow(() -> new RuntimeException("Seller payment profile not found"));

        Account account = Account.retrieve(profile.getStripeConnectedAccountId());

        profile.setChargesEnabled(Boolean.TRUE.equals(account.getChargesEnabled()));
        profile.setPayoutsEnabled(Boolean.TRUE.equals(account.getPayoutsEnabled()));
        profile.setOnboardingComplete(
                Boolean.TRUE.equals(account.getChargesEnabled()) &&
                        Boolean.TRUE.equals(account.getPayoutsEnabled())
        );

        return sellerPaymentProfileRepository.save(profile);
    }

    public SellerPaymentProfile getByUserId(String userId) {
        BusinessProfile businessProfile = profileServiceClient.getBusinessProfileInternal(userId);

        if (businessProfile == null) {
            throw new RuntimeException("Business profile not found for userId: " + userId);
        }

        return sellerPaymentProfileRepository.findByBusinessProfileId(businessProfile.getId())
                .orElseThrow(() -> new RuntimeException("Seller payment profile not found"));
    }
    private boolean isSupportedSellerType(BusinessType businessType) {
        return businessType != null &&
                (businessType == BusinessType.FARMER ||
                        businessType == BusinessType.RESTAURANT);
    }
}