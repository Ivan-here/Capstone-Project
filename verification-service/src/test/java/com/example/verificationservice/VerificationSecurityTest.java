package com.example.verificationservice;
import com.example.verificationservice.Client.ProfileClient;
import com.example.verificationservice.Repository.VerificationRepository;
import com.example.verificationservice.Service.*;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class VerificationSecurityTest {
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }
    @Test void userCannotReadOthersDocumentsOrApproveTheirOwnBusiness() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("alice", null, List.of(new SimpleGrantedAuthority("ROLE_SHOPPER"))));
        var repo = mock(VerificationRepository.class);
        var service = new VerificationService(repo, mock(ProfileClient.class), mock(CloudinaryService.class));
        assertThrows(ResponseStatusException.class, () -> service.getVerificationByUserId("bob"));
        assertThrows(ResponseStatusException.class, () -> service.reviewRequest("own-request", null));
        assertThrows(ResponseStatusException.class, () -> service.getAllRequests());
        verifyNoInteractions(repo);
    }
}
