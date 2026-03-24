package com.example.verificationservice.Service;

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

        // 1. Upload the verification document
        String uploadedUrl = cloudinaryService.uploadImage(document);

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

        log.info("Saving new verification request with document for user {}", dto.userId());
        return repository.save(verification);
    }

    public Verification reviewRequest(String id, ReviewRequestDTO dto) {
        Verification verification = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found"));

        String oldStatus = verification.getStatus();
        boolean approvingNow =
                !"APPROVED".equalsIgnoreCase(oldStatus)
                        && "APPROVED".equalsIgnoreCase(dto.status());

        if (approvingNow) {
            log.info("Verification approved. Notifying Profile Service for user {}", verification.getUserId());
            profileClient.verifyProfile(verification.getUserId());
        }

        verification.setStatus(dto.status());
        verification.setAdminNotes(dto.adminNotes());
        verification.setUpdatedAt(LocalDateTime.now());

        return repository.save(verification);
    }

    public List<Verification> getPendingRequests() {
        return repository.findByStatus("PENDING");
    }

    public Verification getVerificationByUserId(String userId) {
        List<Verification> verifications = repository.findAllByUserId(userId);

        if (verifications.isEmpty()) {
            return null;
        }

        return verifications.get(verifications.size() - 1);
    }

    public List<Verification> getAllRequests() {
        return repository.findAll();
    }

    public Verification getRequestById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
    }

    public List<Verification> getRequestsByStatus(String status) {
        return repository.findByStatus(status);
    }

    public List<Verification> getRequestsByUserId(String userId) {
        return repository.findAllByUserId(userId);
    }

    public Verification updateRequest(String id, SubmitRequestDTO dto, MultipartFile document) {
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
        Verification verification = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        repository.delete(verification);
        log.info("Deleted verification request {}", id);
    }
}