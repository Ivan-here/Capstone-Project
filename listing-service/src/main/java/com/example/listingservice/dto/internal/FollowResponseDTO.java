package com.example.listingservice.dto.internal;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FollowResponseDTO {
    private String followId;
    private FlatUserDTO user;
    private boolean isMutual;
    private LocalDateTime connectedAt;
}
