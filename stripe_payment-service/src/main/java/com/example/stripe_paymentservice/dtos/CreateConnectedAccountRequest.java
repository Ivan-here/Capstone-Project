package com.example.stripe_paymentservice.dtos;


import lombok.Data;

@Data
public class CreateConnectedAccountRequest {
    private String userId;
}