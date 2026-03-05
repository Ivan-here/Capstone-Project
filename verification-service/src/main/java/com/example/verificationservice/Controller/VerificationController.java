package com.example.verificationservice.Controller;

import com.example.verificationservice.DTO.ReviewRequestDTO;
import com.example.verificationservice.DTO.SubmitRequestDTO;
import com.example.verificationservice.Model.Verification;
import com.example.verificationservice.Service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService service;

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Verification submit(
            @RequestPart("request") @Valid SubmitRequestDTO dto,
            @RequestPart("document") MultipartFile document) { // Single file for verification
        return service.submitRequest(dto, document);
    }

    @GetMapping("/admin/queue")
    @ResponseStatus(HttpStatus.OK)
    public List<Verification> getQueue() {
        return service.getPendingRequests();
    }

    @PatchMapping("/admin/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Verification review(@PathVariable String id, @RequestBody ReviewRequestDTO dto) {
        return service.reviewRequest(id,dto);
    }

    // ADD THIS ENDPOINT
    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Verification getUserVerification(@PathVariable String userId) {
        return service.getVerificationByUserId(userId);
    }
}