package com.example.notificationsservice;
import com.example.notificationsservice.client.IdentityClient;
import com.example.notificationsservice.controller.NotificationController;
import com.example.notificationsservice.model.Notification;
import com.example.notificationsservice.repository.NotificationRepository;
import org.junit.jupiter.api.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class NotificationSecurityTest {
    NotificationRepository repo = mock(NotificationRepository.class);
    NotificationController controller = new NotificationController(repo, mock(IdentityClient.class));
    @BeforeEach void setup() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("alice", null, List.of(new SimpleGrantedAuthority("ROLE_SHOPPER"))));
        Notification note = new Notification(); note.setId("n"); note.setUserId("bob");
        when(repo.findById("n")).thenReturn(Optional.of(note));
    }
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }
    @Test void cannotReadChangeOrDeleteAnotherUsersNotification() {
        assertThrows(ResponseStatusException.class, () -> controller.list("bob", null));
        assertThrows(ResponseStatusException.class, () -> controller.getById("n"));
        assertThrows(ResponseStatusException.class, () -> controller.markRead("n", true));
        assertThrows(ResponseStatusException.class, () -> controller.markAllRead("bob"));
        assertThrows(ResponseStatusException.class, () -> controller.delete("n"));
        verify(repo, never()).save(any()); verify(repo, never()).delete(any());
    }
    @Test void cannotForgeSystemNotification() {
        assertThrows(ResponseStatusException.class, () -> controller.create(new Notification()));
        assertThrows(ResponseStatusException.class, () -> controller.update("n", new Notification()));
    }
}
