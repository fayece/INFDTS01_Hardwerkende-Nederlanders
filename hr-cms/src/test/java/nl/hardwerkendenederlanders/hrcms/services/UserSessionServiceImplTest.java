package nl.hardwerkendenederlanders.hrcms.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpSession;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.junit.jupiter.api.Test;

class UserSessionServiceImplTest {

    private final UserSessionServiceImpl service = new UserSessionServiceImpl();

    @Test
    void login_setsUserIdInSession() {
        HttpSession session = mock(HttpSession.class);
        UUID id = UUID.randomUUID();

        User user = new User(id, "Kim", null, "Kardashian", "hash", null, null, true, OffsetDateTime.now());

        service.login(session, user);

        verify(session).setAttribute("userId", id);
    }

    @Test
    void getLoggedInUser_returnsUserId() {
        HttpSession session = mock(HttpSession.class);
        UUID id = UUID.randomUUID();

        when(session.getAttribute("userId")).thenReturn(id);

        Optional<UUID> result = service.getLoggedInUser(session);
        assertTrue(result.isPresent());
        assertEquals(id, result.get());
    }

    @Test
    void getLoggedInUser_whenNotLoggedIn_returnsEmptyOptional() {
        HttpSession session = mock(HttpSession.class);

        when(session.getAttribute("userId")).thenReturn(null);

        Optional<UUID> result = service.getLoggedInUser(session);
        assertTrue(result.isEmpty());
    }

    @Test
    void isLoggedIn_returnsTrue_whenUserExists() {
        HttpSession session = mock(HttpSession.class);

        when(session.getAttribute("userId")).thenReturn(UUID.randomUUID());

        assertTrue(service.isLoggedIn(session));
    }

    @Test
    void isLoggedIn_returnsFalse_whenUserMissing() {
        HttpSession session = mock(HttpSession.class);

        when(session.getAttribute("userId")).thenReturn(null);

        assertFalse(service.isLoggedIn(session));
    }

    @Test
    void logout_invalidatesSession() {
        HttpSession session = mock(HttpSession.class);

        service.logout(session);

        verify(session).invalidate();
    }
}
