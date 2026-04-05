package com.example.adminservice.service;

import com.example.adminservice.clients.ListingServiceClient;
import com.example.adminservice.dtos.listing.Listing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminListingService {

    private final ListingServiceClient listingServiceClient;

    public List<Listing> getAllListings() {
        return listingServiceClient.getAllAdminListings();
    }

    public Listing getListingById(String id) {
        return listingServiceClient.getListingById(id);
    }

    public Listing closeListing(String id) {
        return listingServiceClient.closeListing(id);
    }

    public Listing updateListingStatus(String id, String status) {
        return listingServiceClient.updateListingStatus(id, status);
    }

    public void deleteListing(String id) {
        listingServiceClient.deleteListingById(id);
    }
}
