package com.locally.orders.order_reservationservice.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class ListingClient {

    private final RestTemplate restTemplate;

    // Base URL of listing-service
    @Value("${listing.service.base-url:http://localhost:8084}")
    private String listingBaseUrl;


    public ListingDto getListing(String listingId) {
        try {
            return restTemplate.getForObject(
                    listingBaseUrl + "/api/listings/" + listingId,
                    ListingDto.class
            );
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }


    public void updateListingQuantity(String listingId, int newQuantity) {
        UpdateQuantityDto body = new UpdateQuantityDto(newQuantity);

        restTemplate.exchange(
                listingBaseUrl + "/api/listings/" + listingId + "/quantity",
                HttpMethod.PATCH,
                new HttpEntity<>(body),
                Void.class
        );
    }

    // DTO for listing response
    @Data
    public static class ListingDto {
        private String id;
        private Integer quantity;
    }

    // DTO for PATCH request body
    public record UpdateQuantityDto(Integer newQuantity) {}
}
