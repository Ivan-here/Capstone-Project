package com.example.adminservice.controller;

import com.example.adminservice.dtos.support.CreateStaffRequestRequest;
import com.example.adminservice.dtos.support.StaffRequestResponse;
import com.example.adminservice.service.StaffRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/support")
@RequiredArgsConstructor
public class SupportController {

    private final StaffRequestService staffRequestService;

    @PostMapping("/staff-requests")
    public StaffRequestResponse createStaffRequest(
            Authentication authentication,
            @Valid @RequestBody CreateStaffRequestRequest request
    ) {
        return staffRequestService.create(authentication.getName(), request);
    }
}
