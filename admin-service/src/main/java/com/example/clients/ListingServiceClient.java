package com.example.clients;

import com.example.listingservice.dto.UpdateListingDTO;
import com.example.listingservice.model.Listing;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "listingClient",
        url = "${clients.listingBaseUrl}"
)
public interface ListingServiceClient {

    @GetMapping("/api/listings/admin")
    List<Listing> getAllAdminListings();

    @GetMapping("/api/listings")
    List<Listing> getAllListings();

    @GetMapping("/api/listings/{id}")
    Listing getListingById(@PathVariable("id") String id);

    @PatchMapping("/api/listings/{id}/close")
    Listing closeListing(@PathVariable("id") String id);

    @PatchMapping("/api/listings/{id}/quantity")
    Listing updateStock(
            @PathVariable("id") String id,
            @RequestBody UpdateListingDTO dto
    );

    @DeleteMapping("/api/listings/{id}")
    void deleteListingById(
            @PathVariable("id") String id
    );
}
