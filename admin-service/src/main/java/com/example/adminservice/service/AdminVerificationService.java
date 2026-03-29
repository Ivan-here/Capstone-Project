package com.example.adminservice.service;

import com.example.adminservice.dtos.verification.CreateVerificationReviewRequest;
import com.example.adminservice.clients.VerificationServiceClient;
import com.example.adminservice.dtos.verification.ReviewVerificationRequest;
import com.example.adminservice.dtos.verification.ReviewRequestDTO;
import com.example.adminservice.dtos.verification.Verification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class AdminVerificationService {

    private final VerificationServiceClient verificationServiceClient;
    private final AdminManagementService adminManagementService;

    public List<Verification> getQueue() {
        return verificationServiceClient.getQueue();
    }

    public List<Verification> getAllRequests() {
        return verificationServiceClient.getAllRequests();
    }

    public Verification getRequestById(String id) {
        return verificationServiceClient.getRequestById(id);
    }

    public List<Verification> getRequestsByStatus(String status) {
        return verificationServiceClient.getRequestsByStatus(status);
    }

    public List<Verification> getRequestsByUser(String userId) {
        return verificationServiceClient.getRequestsByUser(userId);
    }

    public Verification review(String id, ReviewVerificationRequest request) {
        String status = request.getStatus() == null ? "" : request.getStatus().trim().toUpperCase();
        String adminNotes = request.getAdminNotes() == null ? "" : request.getAdminNotes().trim();

        if (status.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification status is required");
        }

        if ("REJECTED".equals(status) && adminNotes.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rejection reason is required");
        }

        ReviewRequestDTO dto = new ReviewRequestDTO(
                status,
                adminNotes.isBlank() ? null : adminNotes
        );
        return verificationServiceClient.review(id, dto);
    }

    public void deleteRequest(String id) {
        verificationServiceClient.deleteRequest(id);
    }

    public void createReviewRequest(CreateVerificationReviewRequest request) {
        String verificationId = request.getVerificationId() == null || request.getVerificationId().isBlank()
                ? request.getUserId()
                : request.getVerificationId();

        String performedBy = request.getRequestedBy() == null || request.getRequestedBy().isBlank()
                ? "SYSTEM"
                : request.getRequestedBy();

        String description = "Verification review requested for userId="
                + request.getUserId()
                + ", type="
                + request.getType();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("userId", request.getUserId());
        metadata.put("type", request.getType());
        metadata.put("documentUrl", request.getDocumentUrl());
        metadata.put("verificationId", request.getVerificationId());

        adminManagementService.logAudit(
                "VERIFICATION_REVIEW_REQUESTED",
                "VERIFICATION",
                verificationId,
                performedBy,
                description,
                metadata
        );
    }
}
