package com.example.communityservice;
import com.example.communityservice.client.*;
import com.example.communityservice.model.*;
import com.example.communityservice.repository.CommunityPostRepository;
import com.example.communityservice.service.CommunityPostService;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class CommunitySecurityTest {
    CommunityPostRepository repo = mock(CommunityPostRepository.class);
    FollowVisibilityClient visibility = mock(FollowVisibilityClient.class);
    CommunityPostService service = new CommunityPostService(repo, mock(IdentityClient.class), mock(NotificationClient.class), visibility);
    CommunityPost post;
    @BeforeEach void setup() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("alice", null, List.of(new SimpleGrantedAuthority("ROLE_SHOPPER"))));
        post = new CommunityPost(); post.setId("p"); post.setUserId("bob"); post.setVisibility(CommunityVisibility.PUBLIC);
        when(repo.findById("p")).thenReturn(Optional.of(post));
    }
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }
    @Test void cannotEditOrDeleteAnotherAuthorsPost() {
        assertThrows(ResponseStatusException.class, () -> service.update("p", null));
        assertThrows(ResponseStatusException.class, () -> service.delete("p"));
        verify(repo, never()).save(any()); verify(repo, never()).delete(any());
    }
    @Test void followingOnlyContentIsFilteredAndDirectAccessDenied() {
        post.setVisibility(CommunityVisibility.FOLLOWING_ONLY);
        when(repo.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(post));
        assertTrue(service.listAll(null, null, null, null).isEmpty());
        assertThrows(ResponseStatusException.class, () -> service.getById("p"));
        when(visibility.follows("alice", "bob")).thenReturn(true);
        assertEquals(post, service.getById("p"));
    }
    @Test void cannotDeleteAnotherUsersCommentOnAnotherAuthorsPost() {
        CommunityComment comment = new CommunityComment(); comment.setId("c"); comment.setUserId("carol");
        post.setComments(new ArrayList<>(List.of(comment)));
        assertThrows(ResponseStatusException.class, () -> service.deleteComment("p", "c"));
        assertEquals(1, post.getComments().size());
    }
}
