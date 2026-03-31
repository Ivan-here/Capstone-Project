package com.example.followservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponseDTO {
    private String followId;
    private FlatUserDTO user;
    private boolean isMutual;
    private LocalDateTime connectedAt;
}