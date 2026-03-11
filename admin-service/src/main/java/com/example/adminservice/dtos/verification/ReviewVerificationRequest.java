package com.example.adminservice.dtos.verification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewVerificationRequest {
    private String status;
    private String adminNotes;
}