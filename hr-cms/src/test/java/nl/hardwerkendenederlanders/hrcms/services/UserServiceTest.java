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
    void getUsers_withSearchName_returnsSearchResults() {
        List<User> users = List.of(mock(User.class));

        when(userRepository.findByNameOrEmailPaginated("Kim", 0, 13)).thenReturn(users);

        List<User> result = userService.getUsers(0, "Kim", null);

        assertEquals(users, result);
        verify(userRepository).findByNameOrEmailPaginated("Kim", 0, 13);
        verify(userRepository, never()).findUserOnActivityPaginated(anyBoolean(), anyInt(), anyInt());
        verify(userRepository, never()).findAllPaginated(anyInt(), anyInt());
    }

    @Test
    void getUsers_withSortActive_returnsFilteredResults() {
        List<User> users = List.of(mock(User.class));

        when(userRepository.findUserOnActivityPaginated(true, 0, 13)).thenReturn(users);

        List<User> result = userService.getUsers(0, null, true);

        assertEquals(users, result);
        verify(userRepository).findUserOnActivityPaginated(true, 0, 13);
        verify(userRepository, never()).findByNameOrEmailPaginated(anyString(), anyInt(), anyInt());
        verify(userRepository, never()).findAllPaginated(anyInt(), anyInt());
    }

    @Test
    void getUsers_withoutSearchOrSort_returnsAllUsers() {
        List<User> users = List.of(mock(User.class), mock(User.class));

        when(userRepository.findAllPaginated(0, 13)).thenReturn(users);

        List<User> result = userService.getUsers(0, null, null);

        assertEquals(users, result);
        verify(userRepository).findAllPaginated(0, 13);
        verify(userRepository, never()).findByNameOrEmailPaginated(anyString(), anyInt(), anyInt());
        verify(userRepository, never()).findUserOnActivityPaginated(anyBoolean(), anyInt(), anyInt());
    }

    @Test
    void getMaxPages_withSearchName_returnsCalculatedPages() {
        when(userRepository.countByNameOrEmailPaginated("Kim")).thenReturn(14);

        int result = userService.getMaxPages("Kim", null);

        assertEquals(2, result);
        verify(userRepository).countByNameOrEmailPaginated("Kim");
        verify(userRepository, never()).countByActive(anyBoolean());
        verify(userRepository, never()).countAll();
    }

    @Test
    void getMaxPages_withSortActive_returnsCalculatedPages() {
        when(userRepository.countByActive(true)).thenReturn(26);

        int result = userService.getMaxPages(null, true);

        assertEquals(2, result);
        verify(userRepository).countByActive(true);
        verify(userRepository, never()).countByNameOrEmailPaginated(anyString());
        verify(userRepository, never()).countAll();
    }

    @Test
    void getMaxPages_withoutSearchOrSort_returnsCalculatedPages() {
        when(userRepository.countAll()).thenReturn(27);

        int result = userService.getMaxPages(null, null);

        assertEquals(3, result);
        verify(userRepository).countAll();
        verify(userRepository, never()).countByNameOrEmailPaginated(anyString());
        verify(userRepository, never()).countByActive(anyBoolean());
    }

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

    @Test
    void validateUserAttributes_nullUser_throws() {
        UserServiceImpl service = new UserServiceImpl(userRepository);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> service.validateUserAttributes(null));

        assertEquals("user is null", exception.getMessage());
    }

    @Test
    void validateUserAttributes_invalidEmail_throws() {
        User user = new User(
                UUID.randomUUID(),
                "Kim",
                null,
                "Possible",
                "invalid-email",
                "hashedPassword",
                null,
                null,
                true,
                OffsetDateTime.now());

        UserServiceImpl service = new UserServiceImpl(userRepository);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> service.validateUserAttributes(user));

        assertEquals("invalid email address", exception.getMessage());
    }

    @Test
    void insertUser_withParams_success() {
        when(userRepository.findByEmail("kp@example.com")).thenReturn(Optional.empty());

        userService.insertUser("Kim", null, "Possible", "kp@example.com", "secret");

        verify(userRepository).findByEmail("kp@example.com");
        verify(userRepository).insert(any(User.class));
    }

    @Test
    void insertUser_withParams_invalidFirstName_throws() {
        assertThrows(
                IllegalArgumentException.class,
                () -> userService.insertUser("K", null, "Possible", "kp@example.com", "secret"));

        verify(userRepository, never()).findByEmail(any());
        verify(userRepository, never()).insert(any());
    }
}
