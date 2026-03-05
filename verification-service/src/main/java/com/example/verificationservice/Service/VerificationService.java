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

        log.info("Saving new verification request with document for user {}", dto.userId());
        return repository.save(verification);
    }
    public Verification reviewRequest(String id, ReviewRequestDTO dto) {
        Verification verification = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        verification.setStatus(dto.status());
        verification.setAdminNotes(dto.adminNotes());
        verification.setUpdatedAt(LocalDateTime.now());

        Verification saved = repository.save(verification);

        // THE NEW LOGIC: Notify Profile Service if Approved
        if ("APPROVED".equalsIgnoreCase(dto.status())) {
            try {
                log.info("Verification approved. Notifying Profile Service for user {}", verification.getUserId());
                profileClient.verifyProfile(verification.getUserId());
            } catch (Exception e) {
                log.error("Failed to notify Profile Service: " + e.getMessage());
                // We catch the error so the verification itself doesn't fail just because the notification failed
            }
        }

        return saved;
    }

    public List<Verification> getPendingRequests() {
        return repository.findByStatus("PENDING");
    }
}