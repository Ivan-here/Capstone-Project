package com.example.profileservice.profiles.model;

import lombok.Getter;

@Getter
public enum BusinessType {
    FARMER("Farmer"),
    RESTAURANT("Restaurant"),
    NGO("Non-profit");

    private final String label;

    BusinessType(String label) {
        this.label = label;
    }

}