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
import java.util.ArrayList;
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

        BusinessProfile profile = businessRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Business profile not found for user: " + userId));

        profile.setVerified(true);
        businessRepo.save(profile);
        log.info("Updated BusinessProfile for user {}: isVerified=true", userId);

        String roleToAdd = switch (profile.getBusinessType()) {
            case FARMER -> "FARMER";
            case RESTAURANT -> "RESTAURANT";
            case NGO -> "NGO";
            default -> throw new IllegalStateException("Unexpected business type: " + profile.getBusinessType());
        };

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
        var p = personalRepo.findByUserId(userId).orElseGet(PersonalProfile::new);
        boolean isNew = (p.getId() == null);

        if (isNew) {
            p.setUserId(userId);
            p.setCreatedAt(Instant.now());
        }

        // required registration fields
        p.setFirstName(req.firstName());
        p.setLastName(req.lastName());

        if (p.getUsername() == null || p.getUsername().isBlank()) {
            if (req.username() == null || req.username().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "username is required");
            }
            p.setUsername(req.username().trim());
        }

        p.setEmail(req.email());

        // optional fields
        if (req.role() != null) p.setRole("SHOPPER");
        if (req.role() != null) p.setDisplayName(req.displayName());
        if (req.location() != null) p.setLocation(req.location());
        if (req.about() != null) p.setAbout(req.about());
        if (req.phone() != null) p.setPhone(req.phone());

        if (req.addresses() != null) p.setAddresses(req.addresses());
        if (req.preferences() != null) p.setPreferences(req.preferences());
        if (req.stats() != null) p.setStats(req.stats());
        if (req.followingPeople() != null) p.setFollowingPeople(req.followingPeople());
        if (req.ratings() != null) p.setRatings(req.ratings());

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

        // required fields
        b.setBusinessType(req.businessType());
        b.setBusinessName(req.businessName());
        b.setAddress(req.address());
        b.setEmail(req.email());

        // optional
        if (req.description() != null) b.setDescription(req.description());
        if (req.hours() != null) b.setHours(req.hours());
        if (req.serviceArea() != null) b.setServiceArea(req.serviceArea());
        if (req.eligibilityNotes() != null) b.setEligibilityNotes(req.eligibilityNotes());

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
    }
}