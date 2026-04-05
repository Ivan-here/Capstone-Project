package com.example.profileservice.settings.service;

import com.example.profileservice.profiles.model.BusinessProfile;
import com.example.profileservice.profiles.model.PersonalProfile;
import com.example.profileservice.profiles.repo.BusinessProfileRepository;
import com.example.profileservice.profiles.repo.PersonalProfileRepository;
import com.example.profileservice.profiles.service.IdentityClient;
import com.example.profileservice.settings.dto.AdminVerificationReviewRequest;
import com.example.profileservice.settings.dto.SettingsOverviewResponse;
import com.example.profileservice.settings.dto.VerificationRecordResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettingsService {

    private final PersonalProfileRepository personalRepo;
    private final BusinessProfileRepository businessRepo;
    private final VerificationGateway verificationGateway;
    private final AdminVerificationReviewGateway adminVerificationReviewGateway;
    private final IdentityClient identityClient;
    private final WebClient webClient;

    @Value("${clients.listingBaseUrl}")
    private String listingBaseUrl;

    @Value("${clients.communityBaseUrl}")
    private String communityBaseUrl;

    public SettingsOverviewResponse getOverview(String userId) {
        PersonalProfile personal = personalRepo.findByUserId(userId).orElse(null);
        BusinessProfile business = businessRepo.findByUserId(userId).orElse(null);
        VerificationRecordResponse verification = verificationGateway.getLatestByUserId(userId);

        return new SettingsOverviewResponse(
                userId,
                personal != null,
                business != null,
                business != null && business.isVerified(),
                verification != null && "PENDING".equalsIgnoreCase(verification.status())
        );
    }

    public VerificationRecordResponse getBusinessVerification(String userId) {
        ensureBusinessProfile(userId);
        VerificationRecordResponse verification = verificationGateway.getLatestByUserId(userId);
        if (verification == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No verification record found");
        }
        return verification;
    }

    public VerificationRecordResponse resubmitVerification(String userId, String type, MultipartFile document) {
        BusinessProfile business = ensureBusinessProfile(userId);

        if (document == null || document.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification document is required");
        }

        String verificationType = resolveVerificationType(type, business);
        VerificationRecordResponse created = verificationGateway.submit(userId, verificationType, document);

        adminVerificationReviewGateway.notifyReviewRequested(
                new AdminVerificationReviewRequest(
                        created.id(),
                        userId,
                        created.type(),
                        created.documentUrl(),
                        userId
                )
        );

        return created;
    }

    public void deletePersonalProfile(String userId) {
        PersonalProfile personal = personalRepo.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Personal profile not found"));
        personalRepo.delete(personal);
    }

    public void deleteBusinessProfile(String userId) {
        BusinessProfile business = businessRepo.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business profile not found"));

        deleteListingsByOwnerId(userId);

        businessRepo.delete(business);
        verificationGateway.deleteAllByUserId(userId);
    }

    public void deleteAccount(String userId) {
        deleteListingsByOwnerId(userId);
        deleteCommunityContentByUserId(userId);
        verificationGateway.deleteAllByUserId(userId);
        businessRepo.findByUserId(userId).ifPresent(businessRepo::delete);
        personalRepo.findByUserId(userId).ifPresent(personalRepo::delete);

        try {
            identityClient.deleteUser(userId);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to delete account in identity service");
        }

        log.info("Deleted full account for userId={}", userId);
    }

    private BusinessProfile ensureBusinessProfile(String userId) {
        return businessRepo.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business profile not found"));
    }

    private String resolveVerificationType(String typeOverride, BusinessProfile business) {
        if (typeOverride != null && !typeOverride.isBlank()) {
            return typeOverride.trim().toUpperCase();
        }
        if (business.getBusinessType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Business type is required for verification");
        }
        return business.getBusinessType().name();
    }

    private void deleteListingsByOwnerId(String userId) {
        try {
            webClient.delete()
                    .uri(listingBaseUrl + "/api/listings/internal/owner/" + userId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to delete user listings");
        }
    }

    private void deleteCommunityContentByUserId(String userId) {
        try {
            webClient.delete()
                    .uri(communityBaseUrl + "/api/community/posts/internal/user/" + userId)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Failed to delete user community content");
        }
    }
}
