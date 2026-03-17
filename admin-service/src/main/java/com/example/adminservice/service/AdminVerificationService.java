package com.example.adminservice.service;

import com.example.adminservice.clients.VerificationServiceClient;
import com.example.adminservice.dtos.verification.ReviewVerificationRequest;
import com.example.adminservice.dtos.verification.ReviewRequestDTO;
import com.example.adminservice.dtos.verification.Verification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminVerificationService {

    private final VerificationServiceClient verificationServiceClient;

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
        ReviewRequestDTO dto = new ReviewRequestDTO(
                request.getStatus(),
                request.getAdminNotes()
        );
        return verificationServiceClient.review(id, dto);
    }

    public void deleteRequest(String id) {
        verificationServiceClient.deleteRequest(id);
    }
}