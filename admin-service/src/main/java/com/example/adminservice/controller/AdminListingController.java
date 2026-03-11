package com.example.adminservice.controller;

import com.example.listingservice.model.Listing;
import com.example.adminservice.service.AdminListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/listings")
@RequiredArgsConstructor
public class AdminListingController {

    private final AdminListingService adminListingService;

    @GetMapping
    public List<Listing> getAllListings() {
        return adminListingService.getAllListings();
    }

    @GetMapping("/{id}")
    public Listing getListingById(@PathVariable String id) {
        return adminListingService.getListingById(id);
    }

    @PatchMapping("/{id}/close")
    public Listing closeListing(@PathVariable String id) {
        return adminListingService.closeListing(id);
    }

    @DeleteMapping("/{id}")
    public void deleteListing(@PathVariable String id) {
        adminListingService.deleteListing(id);
    }
}