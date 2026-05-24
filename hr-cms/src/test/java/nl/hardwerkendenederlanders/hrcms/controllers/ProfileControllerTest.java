package nl.hardwerkendenederlanders.hrcms.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.exceptions.ConflictException;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.dtos.ProfileDTO;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ProfileService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

class ProfileControllerTest {

    private final ProfileService profileService = mock(ProfileService.class);
    private final UserSessionService userSessionService = mock(UserSessionService.class);

    private final ProfileController profileController = new ProfileController(profileService, userSessionService);

    @Test
    void getProfilePageOfCurrentUser_loggedIn_returnsProfilePage() {
        UUID userId = UUID.randomUUID();
        Profile profile = new Profile();
        profile.setId(userId.toString());
        profile.setUsername("swift_frog42");

        HttpSession session = mock(HttpSession.class);
        Model model = new ConcurrentModel();

        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.of(userId));
        when(profileService.getProfileById(userId)).thenReturn(profile);

        String result = profileController.getProfilePageOfCurrentUser(model, session);

        assertEquals("pages/profile-page", result);
        assertEquals(profile, model.getAttribute("profile"));
        assertEquals(true, model.getAttribute("editable"));
    }

    @Test
    void getProfilePageOfCurrentUser_notLoggedIn_returnsLoginPage() {
        HttpSession session = mock(HttpSession.class);
        Model model = new ConcurrentModel();

        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.empty());

        String result = profileController.getProfilePageOfCurrentUser(model, session);

        assertEquals("pages/login", result);
    }

    @Test
    void getProfilePageOfOtherUser_returnsProfilePage() {
        Profile profile = new Profile();
        profile.setUsername("swift_frog42");

        Model model = new ConcurrentModel();

        when(profileService.getProfileByUsername("swift_frog42")).thenReturn(profile);

        String result = profileController.getProfilePageOfOtherUser("swift_frog42", model);

        assertEquals("pages/profile-page", result);
        assertEquals(profile, model.getAttribute("profile"));
        assertEquals(false, model.getAttribute("editable"));
    }

    @Test
    void editProfile_loggedIn_returnsEditPage() {
        UUID userId = UUID.randomUUID();
        Profile profile = new Profile();
        profile.setId(userId.toString());

        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUsername("swift_frog42");

        HttpSession session = mock(HttpSession.class);
        Model model = new ConcurrentModel();

        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.of(userId));
        when(profileService.getProfileById(userId)).thenReturn(profile);
        when(profileService.toDTO(profile)).thenReturn(profileDTO);

        String result = profileController.editProfile(model, session);

        assertEquals("pages/edit-profile", result);
        assertEquals(profileDTO, model.getAttribute("profileDTO"));
    }

    @Test
    void editProfile_notLoggedIn_returnsLoginPage() {
        HttpSession session = mock(HttpSession.class);
        Model model = new ConcurrentModel();

        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.empty());

        String result = profileController.editProfile(model, session);

        assertEquals("pages/login", result);
    }

    @Test
    void updateProfile_success_redirectsToProfile() throws Exception {
        UUID userId = UUID.randomUUID();
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUsername("swift_frog42");

        HttpSession session = mock(HttpSession.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.of(userId));
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = profileController.updateProfile(profileDTO, null, bindingResult, session);

        assertEquals("redirect:/my-profile", result);
        verify(profileService).setProfile(eq(userId), eq(profileDTO), any());
    }

    @Test
    void updateProfile_notLoggedIn_returnsLoginPage() throws Exception {
        ProfileDTO profileDTO = new ProfileDTO();
        HttpSession session = mock(HttpSession.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.empty());

        String result = profileController.updateProfile(profileDTO, null, bindingResult, session);

        assertEquals("pages/login", result);
    }

    @Test
    void updateProfile_usernameTaken_returnsEditPage() throws Exception {
        UUID userId = UUID.randomUUID();
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setUsername("taken_username");

        HttpSession session = mock(HttpSession.class);
        BindingResult bindingResult = mock(BindingResult.class);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.of(userId));
        doThrow(new ConflictException("Username is already taken"))
                .when(profileService)
                .setProfile(any(), any(), any());

        String result = profileController.updateProfile(profileDTO, null, bindingResult, session);

        assertEquals("pages/edit-profile", result);
    }
}
