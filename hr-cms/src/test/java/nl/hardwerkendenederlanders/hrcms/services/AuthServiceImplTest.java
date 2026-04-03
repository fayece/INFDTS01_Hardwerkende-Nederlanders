package nl.hardwerkendenederlanders.hrcms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

class AuthServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final AuthServiceImpl authService = new AuthServiceImpl(userRepository);

    @Test
    void login_success_returnsUser() {
        String password = "secret";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        User user = new User(
                UUID.randomUUID(), "John", null, "Doe", "test@test.com", hash, null, null, true, OffsetDateTime.now());

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        User result = authService.login("test@test.com", password);

        assertEquals(user, result);
    }

    @Test
    void login_unknownEmail_throwsException() {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> authService.login("test@test.com", "secret"));

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void login_wrongPassword_throwsException() {
        String hash = BCrypt.hashpw("correctPassword", BCrypt.gensalt());

        User user = new User(
                UUID.randomUUID(), "John", null, "Doe", "test@test.com", hash, null, null, true, OffsetDateTime.now());

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> authService.login("test@test.com", "wrongPassword"));

        assertEquals("Invalid credentials", exception.getMessage());
    }
}
