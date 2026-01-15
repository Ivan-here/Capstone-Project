package com.example.verificationservice.Controller;

import com.example.verificationservice.DTO.ReviewRequestDTO;
import com.example.verificationservice.DTO.SubmitRequestDTO;
import com.example.verificationservice.Model.Verification;
import com.example.verificationservice.Service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService service;

    @PostMapping("/submit")
    @ResponseStatus(HttpStatus.CREATED)
    public Verification submit(@Valid @RequestBody SubmitRequestDTO dto) {
        return service.submitRequest(dto);
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
}