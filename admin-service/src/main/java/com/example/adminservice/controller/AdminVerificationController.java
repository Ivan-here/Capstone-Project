package com.example.adminservice.controller;

import com.example.adminservice.dtos.verification.ReviewVerificationRequest;
import com.example.adminservice.service.AdminVerificationService;
import com.example.verificationservice.Model.Verification;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/verifications")
@RequiredArgsConstructor
public class AdminVerificationController {

    private final AdminVerificationService adminVerificationService;

    @GetMapping("/queue")
    public List<Verification> getQueue() {
        return adminVerificationService.getQueue();
    }

    @GetMapping
    public List<Verification> getAllRequests() {
        return adminVerificationService.getAllRequests();
    }

    @GetMapping("/{id}")
    public Verification getRequestById(@PathVariable String id) {
        return adminVerificationService.getRequestById(id);
    }

    @GetMapping("/status/{status}")
    public List<Verification> getRequestsByStatus(@PathVariable String status) {
        return adminVerificationService.getRequestsByStatus(status);
    }

    @GetMapping("/user/{userId}")
    public List<Verification> getRequestsByUser(@PathVariable String userId) {
        return adminVerificationService.getRequestsByUser(userId);
    }

    @PatchMapping("/{id}/review")
    public Verification review(
            @PathVariable String id,
            @RequestBody ReviewVerificationRequest request
    ) {
        return adminVerificationService.review(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteRequest(@PathVariable String id) {
        adminVerificationService.deleteRequest(id);
    }
}