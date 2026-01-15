package com.example.verificationservice.Service;

import com.example.verificationservice.DTO.ReviewRequestDTO;
import com.example.verificationservice.DTO.SubmitRequestDTO;
import com.example.verificationservice.Model.Verification;
import com.example.verificationservice.Repository.VerificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor // Lombok creates the constructor for the Repository automatically
@Slf4j // Allows us to log what's happening
public class VerificationService {

    private final VerificationRepository repository;

    public Verification submitRequest(SubmitRequestDTO dto) {
        // Using the @Builder from your Model!
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

        log.info("Verification {} has been updated to {}", id, dto.status());
        return repository.save(verification);
    }

    public List<Verification> getPendingRequests() {
        return repository.findByStatus("PENDING");
    }
}