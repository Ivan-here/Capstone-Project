package com.example.adminservice.controller;

import com.example.adminservice.dtos.verification.CreateVerificationReviewRequest;
import com.example.adminservice.service.AdminVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/admin/verifications")
@RequiredArgsConstructor
public class InternalAdminVerificationController {

    private final AdminVerificationService adminVerificationService;

    @PostMapping("/review-request")
    public void createReviewRequest(@RequestBody CreateVerificationReviewRequest request) {
        adminVerificationService.createReviewRequest(request);
    }
}
