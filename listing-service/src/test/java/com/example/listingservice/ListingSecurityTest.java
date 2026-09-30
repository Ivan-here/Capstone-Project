package com.example.listingservice;
import com.example.listingservice.client.*;
import com.example.listingservice.model.Listing;
import com.example.listingservice.repository.ListingRepository;
import com.example.listingservice.service.*;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class ListingSecurityTest {
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }
    @Test void otherUsersCannotDeleteCloseOrEditListing() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("mallory", null, List.of(new SimpleGrantedAuthority("ROLE_FARMER"))));
        var repo = mock(ListingRepository.class);
        when(repo.findById("l")).thenReturn(Optional.of(Listing.builder().id("l").ownerId("alice").build()));
        var service = new ListingService(repo, mock(ProfileClient.class), mock(PaymentClient.class), mock(CloudinaryService.class), mock(FollowClient.class), mock(NotificationClient.class));
        assertThrows(ResponseStatusException.class, () -> service.deleteListingById("l"));
        assertThrows(ResponseStatusException.class, () -> service.closeListing("l"));
        assertThrows(ResponseStatusException.class, () -> service.updateFullListing("l", null, null));
        assertThrows(ResponseStatusException.class, () -> service.updateStock("l", null));
        assertThrows(ResponseStatusException.class, () -> service.getAllListings());
        verify(repo, never()).save(any()); verify(repo, never()).deleteById(any());
    }
}
