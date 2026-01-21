package com.example.verificationservice.Service;

import com.example.verificationservice.DTO.ReviewRequestDTO;
import com.example.verificationservice.DTO.SubmitRequestDTO;
import com.example.verificationservice.Model.Verification;
import com.example.verificationservice.Repository.VerificationRepository;
import com.example.verificationservice.Client.ProfileClient; // Import your new client
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {

    private final VerificationRepository repository;
    private final ProfileClient profileClient; // <--- Inject the client

    public Verification submitRequest(SubmitRequestDTO dto) {
        Verification verification = Verification.builder()
                .userId(dto.userId())
                .type(dto.type())
                .documentUrl(dto.documentUrl())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();

        log.info("Saving new verification request for user {}", dto.userId());
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