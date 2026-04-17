package nl.hardwerkendenederlanders.hrcms.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ConflictException;
import nl.hardwerkendenederlanders.hrcms.exceptions.NotFoundException;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserService;
import org.junit.jupiter.api.Test;

public class UserServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final UserService userService = new UserServiceImpl(userRepository);

    @Test
    void insertUser_success() {
        User user = new User(
                UUID.randomUUID(),
                "Kim",
                null,
                "Possible",
                "kp@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        userService.insertUser(user);
        verify(userRepository).insert(user);
    }

    @Test
    void insertUser_failEmailAddressTaken() {
        User user = new User(
                UUID.randomUUID(),
                "Kim",
                null,
                "Possible",
                "kp@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        assertThrows(ConflictException.class, () -> userService.insertUser(user));
        verify(userRepository, never()).insert(any());
    }

    @Test
    void findById_successReturnsUser() {
        User user = new User(
                UUID.randomUUID(),
                "Kim",
                null,
                "Possible",
                "kp@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User result = userService.findById(user.getId());

        assertEquals(user.getId(), result.getId());
    }

    @Test
    void findById_failThrowsException() {
        UUID id = UUID.randomUUID();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findById(id));
    }

    @Test
    void findByEmail_successReturnsUser() {
        User user = new User(
                UUID.randomUUID(),
                "Kim",
                null,
                "Possible",
                "kp@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        User result = userService.findByEmail(user.getEmail());

        assertEquals(result.getEmail(), user.getEmail());
    }

    @Test
    void findByEmail_failThrowsException() {
        String email = "kp@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findByEmail(email));
    }

    @Test
    void findAllUsers_successReturnsList() {
        User user = new User(
                UUID.randomUUID(),
                "Kim",
                null,
                "Possible",
                "kp@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        User user2 = new User(
                UUID.randomUUID(),
                "Kimkim",
                null,
                "Possiblepossible",
                "kpkp@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());
        List<User> users = Arrays.asList(user, user2);

        when(userRepository.findAllUsers()).thenReturn(users);

        List<User> result = userService.findAllUsers();

        assertEquals(2, result.size());

        verify(userRepository).findAllUsers();
    }

    @Test
    void updateUser_successUpdatesUser() {
        User user = new User(
                UUID.randomUUID(),
                "Kim",
                null,
                "Possible",
                "kp@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        userService.updateUser(user);
        verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository).update(user);
    }

    @Test
    void updateUser_failEmailTakenThrowsException() {
        User user = new User(
                UUID.randomUUID(),
                "Kim",
                null,
                "Possible",
                "kp@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        User user2 = new User(
                UUID.randomUUID(),
                "Kimkim",
                null,
                "Possiblepossible",
                "kpkp@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user2));

        assertThrows(ConflictException.class, () -> userService.updateUser(user));
    }

    @Test
    void deleteById_successDoesNotThrow() {
        UUID id = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        assertDoesNotThrow(() -> userService.deleteById(id, currentUserId));

        verify(userRepository).deleteById(id);
    }

    @Test
    void deleteById_failsThrows() {
        UUID id = UUID.randomUUID();

        assertThrows(ConflictException.class, () -> userService.deleteById(id, id));
    }

    @Test
    void findUsersPaginated_successReturnsUsers() {
        User user = new User(
                UUID.randomUUID(),
                "Kim",
                null,
                "Possible",
                "kp@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        User user2 = new User(
                UUID.randomUUID(),
                "Kimkim",
                null,
                "Possiblepossible",
                "kpkp@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        List<User> users = Arrays.asList(user, user2);

        when(userRepository.findAllPaginated(0, 10)).thenReturn(users);

        List<User> result = userService.findUsersPaginated(0, 10);

        assertEquals(2, result.size());
        assertEquals("Kim", result.get(0).getFirstName());
        assertEquals("Kimkim", result.get(1).getFirstName());

        verify(userRepository).findAllPaginated(0, 10);
    }

    @Test
    void searchByNamePaginated_successReturnsMatchingUsers() {
        User user = new User(
                UUID.randomUUID(),
                "Kim",
                null,
                "Possible",
                "kp@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        User user2 = new User(
                UUID.randomUUID(),
                "kim",
                null,
                "Possiblepossible",
                "kimkim@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        List<User> users = Arrays.asList(user, user2);

        when(userRepository.findByNameOrEmailPaginated("kim", 0, 10)).thenReturn(users);

        List<User> result = userService.searchByNamePaginated("kim", 0, 10);

        assertEquals(2, result.size());
        assertEquals("Kim", result.get(0).getFirstName());
        assertEquals("kp@example.com", result.get(0).getEmail());

        verify(userRepository).findByNameOrEmailPaginated("kim", 0, 10);
    }

    @Test
    void findUserOnActivityPaginated_successReturnsActiveUsers() {
        User user = new User(
                UUID.randomUUID(),
                "Kim",
                null,
                "Possible",
                "kp@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        User user2 = new User(
                UUID.randomUUID(),
                "Kimkim",
                null,
                "Possiblepossible",
                "kpkp@example.com",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        List<User> users = Arrays.asList(user, user2);

        when(userRepository.findUserOnActivityPaginated(true, 0, 10)).thenReturn(users);

        List<User> result = userService.findUserOnActivityPaginated(true, 0, 10);

        assertEquals(2, result.size());
        assertTrue(result.get(0).isActive());
        assertTrue(result.get(1).isActive());

        verify(userRepository).findUserOnActivityPaginated(true, 0, 10);
    }
}
