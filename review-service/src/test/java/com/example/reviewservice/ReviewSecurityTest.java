package com.example.reviewservice;
import com.example.reviewservice.client.OrderClient;
import com.example.reviewservice.model.*;
import com.example.reviewservice.repository.ReviewRepository;
import com.example.reviewservice.service.ReviewService;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class ReviewSecurityTest {
    ReviewRepository repo = mock(ReviewRepository.class);
    OrderClient orders = mock(OrderClient.class);
    ReviewService service = new ReviewService(repo, orders);
    @BeforeEach void setup() { SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("alice", null, List.of(new SimpleGrantedAuthority("ROLE_SHOPPER")))); }
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }
    @Test void cannotImpersonateReviewerOrOverwriteTheirReview() {
        Review other = Review.builder().id("r").reviewerId("bob").build();
        when(repo.findById("r")).thenReturn(Optional.of(other));
        assertThrows(ResponseStatusException.class, () -> service.createReview(other));
        assertThrows(ResponseStatusException.class, () -> service.updateReview("r", 5, "fake"));
        verify(repo, never()).save(any());
    }
    @Test void unverifiedPurchaseCannotBeReviewed() {
        Review review = Review.builder().id("injected").reviewerId("alice").orderId("o").targetId("l").targetType(TargetType.LISTING).rating(5).build();
        assertThrows(SecurityException.class, () -> service.createReview(review));
        verify(orders).verifyOrderCompletion("o", "alice", "l", "LISTING");
        verify(repo, never()).save(any());
    }
}
