package nl.hardwerkendenederlanders.hrcms.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpSession;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.AuthService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ProfileService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

class AuthControllerTest {

    private final UserSessionService userSessionService = mock(UserSessionService.class);
    private final AuthService authService = mock(AuthService.class);

    private final AuthController authController = new AuthController(userSessionService, authService);

    @Test
    void login_success_redirectsToHome() {
        HttpSession session = mock(HttpSession.class);
        User user = mock(User.class);

        when(authService.login("test@test.com", "secret")).thenReturn(user);

        String result = authController.login(session, "test@test.com", "secret");

        assertEquals("redirect:/", result);
        verify(authService).login("test@test.com", "secret");
        verify(userSessionService).login(session, user);
    }

    @Test
    void login_failure_redirectsToLoginWithError() {
        HttpSession session = mock(HttpSession.class);

        when(authService.login("test@test.com", "wrong")).thenThrow(new RuntimeException("Invalid credentials"));

        String result = authController.login(session, "test@test.com", "wrong");

        assertEquals("redirect:/login?error=true", result);
        verify(authService).login("test@test.com", "wrong");
        verify(userSessionService, never()).login(any(), any());
    }

    @Test
    void logout_redirectsToLogin() {
        HttpSession session = mock(HttpSession.class);

        String result = authController.logout(session);

        assertEquals("redirect:/login", result);
        verify(userSessionService).logout(session);
    }

    @Test
    void loginPage_withoutError_returnsLoginView() {
        Model model = new ConcurrentModel();

        String result = authController.loginPage(null, model);

        assertEquals("pages/login", result);
        assertNull(model.getAttribute("error"));
    }

    @Test
    void loginPage_withError_returnsLoginViewAndSetsError() {
        Model model = new ConcurrentModel();

        String result = authController.loginPage(true, model);

        assertEquals("pages/login", result);
        assertEquals(true, model.getAttribute("error"));
    }
}
