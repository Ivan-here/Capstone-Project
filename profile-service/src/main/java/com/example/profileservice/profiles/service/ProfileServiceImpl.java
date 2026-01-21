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

        p.setFullName(req.fullName());
        p.setPhone(req.phone());
        p.setAddresses(req.addresses() == null ? List.of() : req.addresses());
        p.setUpdatedAt(Instant.now());

        PersonalProfile saved = personalRepo.save(p);
        log.info("Personal profile {} for userId={}", isNew ? "created" : "updated", userId);
        return saved;
    }

    @Override
    public BusinessProfile upsertBusiness(String userId, BusinessProfileRequest req) {
        validateBusinessByType(req);

        BusinessProfile b = businessRepo.findByUserId(userId).orElseGet(BusinessProfile::new);
        boolean isNew = (b.getId() == null);

        if (isNew) {
            b.setUserId(userId);
            b.setCreatedAt(Instant.now());
        }

        b.setBusinessType(req.businessType());
        b.setBusinessName(req.businessName());
        b.setAddress(req.address());
        b.setHours(req.hours());
        b.setPickupInstructions(req.pickupInstructions());
        b.setServiceArea(req.serviceArea());
        b.setEligibilityNotes(req.eligibilityNotes());
        b.setUpdatedAt(Instant.now());

        BusinessProfile saved = businessRepo.save(b);

        String roleToAdd = switch (req.businessType()) {
            case FARMER -> "FARMER";
            case RESTAURANT -> "RESTAURANT";
            case NGO -> "NGO";
        };

        roleUpgradeClient.addRoleToUser(userId, roleToAdd);

        log.info("Business profile {} for userId={}, type={}", isNew ? "created" : "updated", userId, req.businessType());
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

    private void validateBusinessByType(BusinessProfileRequest req) {
        BusinessType type = req.businessType();
        if (type == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "businessType is required");
        }

        if (type == BusinessType.RESTAURANT) {
            if (req.hours() == null || req.hours().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "hours is required for RESTAURANT");
            }
        }

        if (type == BusinessType.NGO) {
            if (req.serviceArea() == null || req.serviceArea().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "serviceArea is required for NGO");
            }
        }
    }
}