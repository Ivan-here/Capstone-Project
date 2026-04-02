package com.example.followservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlatUserDTO {
    private String id;
    private String displayName;
    private String username;
    private String role;
    private String avatarUrl;
}
