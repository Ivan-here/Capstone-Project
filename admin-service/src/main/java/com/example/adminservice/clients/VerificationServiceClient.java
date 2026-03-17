package com.example.adminservice.clients;

import com.example.adminservice.dtos.verification.ReviewRequestDTO;
import com.example.adminservice.dtos.verification.Verification;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "verificationClient",
        url = "${clients.verificationBaseUrl}"
)
public interface VerificationServiceClient {

    @GetMapping("/api/verification/admin/queue")
    List<Verification> getQueue();

    @PatchMapping("/api/verification/admin/{id}")
    Verification review(
            @PathVariable("id") String id,
            @RequestBody ReviewRequestDTO dto
    );

    @GetMapping("/api/verification/user/{userId}")
    Verification getUserVerification(@PathVariable("userId") String userId);

    @GetMapping("/api/verification/admin")
    List<Verification> getAllRequests();

    @GetMapping("/api/verification/admin/{id}")
    Verification getRequestById(@PathVariable("id") String id);

    @GetMapping("/api/verification/admin/status/{status}")
    List<Verification> getRequestsByStatus(@PathVariable("status") String status);

    @GetMapping("/api/verification/admin/user/{userId}")
    List<Verification> getRequestsByUser(@PathVariable("userId") String userId);

    @DeleteMapping("/api/verification/admin/{id}")
    void deleteRequest(@PathVariable("id") String id);
}