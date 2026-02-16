package com.example.profileservice.profiles.service;

import com.example.profileservice.profiles.dto.BusinessProfileRequest;
import com.example.profileservice.profiles.dto.PersonalProfileRequest;
import com.example.profileservice.profiles.dto.ProfileResponse;
import com.example.profileservice.profiles.model.BusinessProfile;
import com.example.profileservice.profiles.model.BusinessType;
import com.example.profileservice.profiles.model.PersonalProfile;
import com.example.profileservice.profiles.repo.BusinessProfileRepository;
import com.example.profileservice.profiles.repo.PersonalProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final PersonalProfileRepository personalRepo;
    private final BusinessProfileRepository businessRepo;
    private final RoleUpgradeClient roleUpgradeClient;


    @Override
    public void verifyUser(String userId) {
        // 1. Find the business profile
        BusinessProfile profile = businessRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Business profile not found for user: " + userId));

        // 2. Mark as Verified in MongoDB
        profile.setVerified(true);
        businessRepo.save(profile);
        log.info("Updated BusinessProfile for user {}: isVerified=true", userId);

        // 3. Map the BusinessType to the Identity Service ROLE String
        // We use .name() to ensure it matches your "Role.java" enum exactly (e.g., "FARMER")
        String roleToAdd = switch (profile.getBusinessType()) {
            case FARMER -> "FARMER";         // Matches Role.FARMER
            case RESTAURANT -> "RESTAURANT"; // Matches Role.RESTAURANT
            case NGO -> "NGO";               // Matches Role.NGO
            // Shopper is default, so we usually don't upgrade TO it, only FROM it
            default -> throw new IllegalStateException("Unexpected business type: " + profile.getBusinessType());
        };

        // 4. Call Identity Service
        try {
            roleUpgradeClient.addRoleToUser(userId, roleToAdd);
            log.info("Successfully requested role '{}' for user {}", roleToAdd, userId);
        } catch (Exception e) {
            log.error("Failed to add role '{}' to user {}. Verify Identity Service is running.", roleToAdd, userId, e);
        }
    }


    @Override
    public ProfileResponse getMe(String userId) {
        PersonalProfile personal = personalRepo.findByUserId(userId).orElse(null);
        BusinessProfile business = businessRepo.findByUserId(userId).orElse(null);
        return new ProfileResponse(userId, personal, business);
    }

    @Override
    public PersonalProfile upsertPersonal(String userId, PersonalProfileRequest req) {
        PersonalProfile p = personalRepo.findByUserId(userId).orElseGet(PersonalProfile::new);

        boolean isNew = (p.getId() == null);
        if (isNew) {
            p.setUserId(userId);
            p.setCreatedAt(Instant.now());
            p.setAddresses(List.of());
        }

        p.setFullName((req.firstName() + " " + req.lastName()).trim());
        p.setPhone(req.contactNumber());
        p.setUpdatedAt(Instant.now());

        return personalRepo.save(p);
    }

    @Override
    public BusinessProfile upsertBusiness(String userId, BusinessProfileRequest req) {

        BusinessProfile b = businessRepo.findByUserId(userId).orElseGet(BusinessProfile::new);
        boolean isNew = (b.getId() == null);

        if (isNew) {
            b.setUserId(userId);
            b.setCreatedAt(Instant.now());
            b.setVerified(false);
        }

        b.setBusinessType(req.businessType());
        b.setBusinessName(req.name());
        b.setAddress(req.address());

        // store the description in one of your existing optional fields
        b.setPickupInstructions(req.description()); // or eligibilityNotes

        // Optional: persist email (requires adding field to BusinessProfile model)
        b.setEmail(req.email());

        b.setUpdatedAt(Instant.now());

        BusinessProfile saved = businessRepo.save(b);

        String roleToAdd = switch (req.businessType()) {
            case FARMER -> "FARMER";
            case RESTAURANT -> "RESTAURANT";
            case NGO -> "NGO";
        };

        roleUpgradeClient.addRoleToUser(userId, roleToAdd);

        return saved;
    }

    @Override
    public void deleteBusiness(String userId) {
        BusinessProfile b = businessRepo.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Business profile not found"));

        businessRepo.delete(b);
        log.info("Business profile deleted for userId={}", userId);

        // Later: call identity-service to remove role(s)
    }
}