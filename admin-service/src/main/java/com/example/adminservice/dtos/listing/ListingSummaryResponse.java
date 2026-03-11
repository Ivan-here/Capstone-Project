package com.example.adminservice.dtos.listing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingSummaryResponse {
    private String id;
    private String title;
    private String description;
    private String type;
    private String status;
    private Integer quantity;
    private String ownerId;
}