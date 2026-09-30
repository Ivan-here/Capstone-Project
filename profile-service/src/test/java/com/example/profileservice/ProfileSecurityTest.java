package com.example.profileservice;
import com.example.profileservice.client.NotificationClient;
import com.example.profileservice.profiles.model.*;
import com.example.profileservice.profiles.repo.*;
import com.example.profileservice.profiles.service.*;
import org.junit.jupiter.api.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
class ProfileSecurityTest {
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }
    @Test void publicProfilesRedactPrivateContactDetailsAndAddresses() {
        var personalRepo = mock(PersonalProfileRepository.class);
        var businessRepo = mock(BusinessProfileRepository.class);
        var personal = new PersonalProfile(); personal.setUserId("alice"); personal.setEmail("private@example.test");
        personal.setPhone("private"); personal.setAddresses(List.of("home")); personal.setPreferences(List.of("private"));
        var business = new BusinessProfile(); business.setEmail("public@example.test"); business.setEmailVisibility(ContactVisibility.PUBLIC);
        business.setPhone("private"); business.setPhoneVisibility(ContactVisibility.PRIVATE);
        when(personalRepo.findByUserId("alice")).thenReturn(Optional.of(personal));
        when(businessRepo.findByUserId("alice")).thenReturn(Optional.of(business));
        var service = new ProfileServiceImpl(personalRepo, businessRepo, mock(RoleUpgradeClient.class), mock(NotificationClient.class));
        var result = service.getProfileByUserId("alice");
        assertNull(result.personalProfile().getEmail()); assertNull(result.personalProfile().getPhone());
        assertTrue(result.personalProfile().getAddresses().isEmpty());
        assertEquals("public@example.test", result.businessProfile().getEmail()); assertNull(result.businessProfile().getPhone());
        verify(personalRepo, never()).save(any()); verify(businessRepo, never()).save(any());
    }
}
