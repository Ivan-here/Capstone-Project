package com.example.verificationservice.Service;

import com.locally.security.Access;

import com.example.verificationservice.DTO.ReviewRequestDTO;
import com.example.verificationservice.DTO.SubmitRequestDTO;
import com.example.verificationservice.Model.Verification;
import com.example.verificationservice.Repository.VerificationRepository;
import com.example.verificationservice.Client.ProfileClient; // Import your new client
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {

    private final VerificationRepository repository;
    private final ProfileClient profileClient; // <--- Inject the client
    private final CloudinaryService cloudinaryService;

    public Verification submitRequest(SubmitRequestDTO dto, MultipartFile document) {
        Access.owner(dto.userId());

        // 1. Upload the verification document
        com.locally.security.AttemptLimiter.check("verification:" + dto.userId(), 5, 3600);
        String uploadedUrl = null;

        // 2. Build the verification record with the new URL
        Verification verification = Verification.builder()
                .userId(dto.userId())
                .type(dto.type())
                .documentUrl(uploadedUrl) // Saved from Cloudinary
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        List<Verification> existing = repository.findAllByUserId(dto.userId());
        boolean hasPending = existing.stream()
                .anyMatch(v -> "PENDING".equalsIgnoreCase(v.getStatus()));

        if (hasPending) {
            throw new RuntimeException("User already has a pending verification request");
        }

        verification.setDocumentUrl(cloudinaryService.uploadImage(document));
        log.info("Saving new verification request with document for user {}", dto.userId());
        return repository.save(verification);
    }

    public Verification reviewRequest(String id, ReviewRequestDTO dto) {
        Access.admin();
        Verification verification = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        String status = dto.status() == null ? "" : dto.status().trim().toUpperCase();
        String adminNotes = dto.adminNotes() == null ? "" : dto.adminNotes().trim();

        if (!java.util.Set.of("APPROVED", "REJECTED").contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification status is required");
        }

        if ("REJECTED".equals(status) && adminNotes.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rejection reason is required");
        }

        String oldStatus = verification.getStatus();
        boolean approvingNow =
                !"APPROVED".equalsIgnoreCase(oldStatus)
                        && "APPROVED".equalsIgnoreCase(status);

        if (approvingNow) {
            log.info("Verification approved. Notifying Profile Service for user {}", verification.getUserId());
            profileClient.verifyProfile(verification.getUserId());
        }

        if ("REJECTED".equals(status)) profileClient.setVerified(verification.getUserId(), false);
        verification.setStatus(status);
        verification.setAdminNotes(adminNotes.isBlank() ? null : adminNotes);
        verification.setUpdatedAt(LocalDateTime.now());

        return repository.save(verification);
    }

    public List<Verification> getPendingRequests() {
        Access.admin();
        return repository.findByStatus("PENDING");
    }

    public Verification getVerificationByUserId(String userId) {
        Access.owner(userId);
        List<Verification> verifications = repository.findAllByUserId(userId);

        if (verifications.isEmpty()) {
            return null;
        }

        return verifications.get(verifications.size() - 1);
    }

    public List<Verification> getAllRequests() {
        Access.admin();
        return repository.findAll();
    }

    public Verification getRequestById(String id) {
        Access.admin();
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }

    public List<Verification> getRequestsByStatus(String status) {
        Access.admin();
        return repository.findByStatus(status);
    }

    public List<Verification> getRequestsByUserId(String userId) {
        Access.admin();
        return repository.findAllByUserId(userId);
    }

    public Verification updateRequest(String id, SubmitRequestDTO dto, MultipartFile document) {
        Access.admin();
        Verification verification = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        verification.setUserId(dto.userId());
        verification.setType(dto.type());
        verification.setUpdatedAt(LocalDateTime.now());

        if (document != null && !document.isEmpty()) {
            String uploadedUrl = cloudinaryService.uploadImage(document);
            verification.setDocumentUrl(uploadedUrl);
        }

        return repository.save(verification);
    }

    public void deleteRequest(String id) {
        Access.admin();
        Verification verification = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        repository.delete(verification);
        log.info("Deleted verification request {}", id);
    }
}
