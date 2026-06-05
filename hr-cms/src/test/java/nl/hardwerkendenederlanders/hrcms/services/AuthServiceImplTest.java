package nl.hardwerkendenederlanders.hrcms.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.database.mongodb.ProfileRepository;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

class AuthServiceImplTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ProfileRepository profileRepository = mock(ProfileRepository.class);
    private final AuthServiceImpl authService = new AuthServiceImpl(userRepository, profileRepository);

    @Test
    void login_success_returnsUser() {
        String password = "secret";
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        UUID userId = UUID.randomUUID();

        Profile profile =
                Profile.builder().id(userId.toString()).username("johndoe123").build();

        User user = new User(userId, "John", null, "Doe", hash, null, null, true, OffsetDateTime.now());

        when(profileRepository.findByUsername("johndoe123")).thenReturn(Optional.of(profile));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = authService.login("johndoe123", password);

        assertEquals(user, result);
    }

    @Test
    void login_unknownUsername_throwsException() {
        when(profileRepository.findByUsername("johndoe123")).thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> authService.login("johndoe123", "secret"));

        assertEquals("Invalid credentials", exception.getMessage());
    }

    @Test
    void login_wrongPassword_throwsException() {
        String hash = BCrypt.hashpw("correctPassword", BCrypt.gensalt());

        UUID userId = UUID.randomUUID();

        Profile profile =
                Profile.builder().id(userId.toString()).username("johndoe123").build();

        User user = new User(userId, "John", null, "Doe", hash, null, null, true, OffsetDateTime.now());

        when(profileRepository.findByUsername("johndoe123")).thenReturn(Optional.of(profile));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        RuntimeException exception =
                assertThrows(RuntimeException.class, () -> authService.login("johndoe123", "wrongPassword"));

        assertEquals("Invalid credentials", exception.getMessage());
    }
}
