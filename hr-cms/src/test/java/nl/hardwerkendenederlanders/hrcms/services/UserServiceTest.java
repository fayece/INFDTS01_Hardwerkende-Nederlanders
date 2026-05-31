package nl.hardwerkendenederlanders.hrcms.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.database.mongodb.ProfileRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ConflictException;
import nl.hardwerkendenederlanders.hrcms.exceptions.NotFoundException;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UsernameGeneratorService;
import org.junit.jupiter.api.Test;

public class UserServiceTest {
    private final UserRepository userRepository = mock(UserRepository.class);
    private final ProfileRepository profileRepository = mock(ProfileRepository.class);
    private final UsernameGeneratorService usernameGeneratorService = mock(UsernameGeneratorService.class);
    private final UserService userService =
            new UserServiceImpl(userRepository, profileRepository, usernameGeneratorService);

    @Test
    void getUsers_withSearchName_returnsSearchResults() {
        List<User> users = List.of(mock(User.class));

        when(userRepository.findByNamePaginated("Kim", 0, 13)).thenReturn(users);

        List<User> result = userService.getUsers(0, "Kim", null);

        assertEquals(users, result);
        verify(userRepository).findByNamePaginated("Kim", 0, 13);
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
        verify(userRepository, never()).findByNamePaginated(anyString(), anyInt(), anyInt());
        verify(userRepository, never()).findAllPaginated(anyInt(), anyInt());
    }

    @Test
    void getUsers_withoutSearchOrSort_returnsAllUsers() {
        List<User> users = List.of(mock(User.class), mock(User.class));

        when(userRepository.findAllPaginated(0, 13)).thenReturn(users);

        List<User> result = userService.getUsers(0, null, null);

        assertEquals(users, result);
        verify(userRepository).findAllPaginated(0, 13);
        verify(userRepository, never()).findByNamePaginated(anyString(), anyInt(), anyInt());
        verify(userRepository, never()).findUserOnActivityPaginated(anyBoolean(), anyInt(), anyInt());
    }

    @Test
    void getMaxPages_withSearchName_returnsCalculatedPages() {
        when(userRepository.countByNamePaginated("Kim")).thenReturn(14);

        int result = userService.getMaxPages("Kim", null);

        assertEquals(2, result);
        verify(userRepository).countByNamePaginated("Kim");
        verify(userRepository, never()).countByActive(anyBoolean());
        verify(userRepository, never()).countAll();
    }

    @Test
    void getMaxPages_withSortActive_returnsCalculatedPages() {
        when(userRepository.countByActive(true)).thenReturn(26);

        int result = userService.getMaxPages(null, true);

        assertEquals(2, result);
        verify(userRepository).countByActive(true);
        verify(userRepository, never()).countByNamePaginated(anyString());
        verify(userRepository, never()).countAll();
    }

    @Test
    void getMaxPages_withoutSearchOrSort_returnsCalculatedPages() {
        when(userRepository.countAll()).thenReturn(27);

        int result = userService.getMaxPages(null, null);

        assertEquals(3, result);
        verify(userRepository).countAll();
        verify(userRepository, never()).countByNamePaginated(anyString());
        verify(userRepository, never()).countByActive(anyBoolean());
    }

    @Test
    void insertUser_success() {
        UUID roleId = UUID.randomUUID();
        User user = User.builder()
                .firstName("Kim")
                .prefix(null)
                .lastName("Possible")
                .passwordHash("secret")
                .roleId(roleId)
                .build();

        when(usernameGeneratorService.generateUniqueUsername()).thenReturn("swift_frog42");

        String username = userService.insertUser(user);

        verify(userRepository).insert(any(User.class));
        verify(profileRepository).save(any(Profile.class));
        assertEquals("swift_frog42", username);
    }

    @Test
    void findById_successReturnsUser() {
        User user = User.builder()
                .firstName("Kim")
                .lastName("Possible")
                .passwordHash("hashedPassword")
                .build();

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
    void findAllUsers_successReturnsList() {
        User user = User.builder()
                .firstName("Kim")
                .lastName("Possible")
                .passwordHash("hashedPassword")
                .build();

        User user2 = User.builder()
                .firstName("Kimkim")
                .lastName("Possiblepossible")
                .passwordHash("hashedPassword")
                .build();

        List<User> users = Arrays.asList(user, user2);

        when(userRepository.findAllUsers()).thenReturn(users);

        List<User> result = userService.findAllUsers();

        assertEquals(2, result.size());
        verify(userRepository).findAllUsers();
    }

    @Test
    void updateUser_successUpdatesUser() {
        User user = User.builder()
                .firstName("Kim")
                .lastName("Possible")
                .passwordHash("hashedPassword")
                .build();

        userService.updateUser(user);
        verify(userRepository).update(user);
    }

    @Test
    void deleteById_successDoesNotThrow() {
        UUID id = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        assertDoesNotThrow(() -> userService.deleteById(id, currentUserId));

        verify(userRepository).deleteById(id);
        verify(profileRepository).deleteById(id.toString());
    }

    @Test
    void deleteById_failsThrows() {
        UUID id = UUID.randomUUID();

        assertThrows(ConflictException.class, () -> userService.deleteById(id, id));
    }

    @Test
    void findUsersPaginated_successReturnsUsers() {
        User user = User.builder()
                .firstName("Kim")
                .lastName("Possible")
                .passwordHash("hashedPassword")
                .build();

        User user2 = User.builder()
                .firstName("Kimkim")
                .lastName("Possiblepossible")
                .passwordHash("hashedPassword")
                .build();

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
        User user = User.builder()
                .firstName("Kim")
                .lastName("Possible")
                .passwordHash("hashedPassword")
                .build();

        User user2 = User.builder()
                .firstName("kim")
                .lastName("Possiblepossible")
                .passwordHash("hashedPassword")
                .build();

        List<User> users = Arrays.asList(user, user2);

        when(userRepository.findByNamePaginated("kim", 0, 10)).thenReturn(users);

        List<User> result = userService.searchByNamePaginated("kim", 0, 10);

        assertEquals(2, result.size());
        assertEquals("Kim", result.getFirst().getFirstName());

        verify(userRepository).findByNamePaginated("kim", 0, 10);
    }

    @Test
    void findUserOnActivityPaginated_successReturnsActiveUsers() {
        User user = User.builder()
                .firstName("Kim")
                .lastName("Possible")
                .passwordHash("hashedPassword")
                .build();

        User user2 = User.builder()
                .firstName("Kimkim")
                .lastName("Possiblepossible")
                .passwordHash("hashedPassword")
                .build();

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
        UserServiceImpl service = new UserServiceImpl(userRepository, profileRepository, usernameGeneratorService);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> service.validateUserAttributes(null));

        assertEquals("user is null", exception.getMessage());
    }

    @Test
    void validateUserAttributes_invalidFirstName_throws() {
        User user = User.builder()
                .firstName("K")
                .lastName("Possible")
                .passwordHash("hashedPassword")
                .build();

        UserServiceImpl service = new UserServiceImpl(userRepository, profileRepository, usernameGeneratorService);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> service.validateUserAttributes(user));

        assertEquals("first name should be at least 2 characters long", exception.getMessage());
    }

    @Test
    void insertUser_invalidFirstName_throws() {
        User user = User.builder()
                .firstName("K")
                .lastName("Possible")
                .passwordHash("secret")
                .roleId(UUID.randomUUID())
                .build();

        assertThrows(IllegalArgumentException.class, () -> userService.insertUser(user));

        verify(userRepository, never()).insert(any());
    }
}
