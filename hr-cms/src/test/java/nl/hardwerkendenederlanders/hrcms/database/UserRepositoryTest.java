package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import java.util.stream.Stream;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcUserRepository;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.RoleRepository;
import nl.hardwerkendenederlanders.hrcms.models.Role;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
public class UserRepositoryTest {

    @Autowired
    private JdbcUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role("Editor");
        roleRepository.insert(role);
    }

    @Test
    void insertUser_withValidUser_shouldPersistAndRetrieve() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email(UUID.randomUUID() + "@example.com")
                .passwordHash("R@ndomP4ssw0rd1!@x")
                .roleId(role.getId())
                .build();

        userRepository.insert(user);

        User retrieved = userRepository.findById(user.getId());

        assertNotNull(retrieved);
        assertEquals(user.getId(), retrieved.getId());
        assertEquals(user.getFirstName(), retrieved.getFirstName());
        assertEquals(user.getLastName(), retrieved.getLastName());
        assertEquals(user.getEmail(), retrieved.getEmail());
        assertEquals(user.getRoleId(), retrieved.getRoleId());
    }

    @Test
    void updateUser_withModifiedFields_shouldReflectChanges() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email(UUID.randomUUID() + "@example.com")
                .passwordHash("R@ndomP4ssw0rd1!@x")
                .roleId(role.getId())
                .build();

        userRepository.insert(user);

        user.setFirstName("Updated");
        user.setLastName("Name");
        user.setActive(false);
        userRepository.update(user);

        User retrieved = userRepository.findById(user.getId());

        assertNotNull(retrieved);
        assertEquals(user.getId(), retrieved.getId());
        assertEquals("Updated", retrieved.getFirstName());
        assertEquals("Name", retrieved.getLastName());
        assertFalse(retrieved.isActive());
    }

    @Test
    void findUserById_withExistingId_shouldReturnUser() {
        User user = User.builder()
                .firstName("Jane")
                .lastName("Doe")
                .email(UUID.randomUUID() + "@example.com")
                .passwordHash("R@ndomP4ssw0rd1!@x")
                .roleId(role.getId())
                .build();

        userRepository.insert(user);

        User retrieved = userRepository.findById(user.getId());

        assertNotNull(retrieved);
        assertEquals(user.getId(), retrieved.getId());
        assertEquals(user.getFirstName(), retrieved.getFirstName());
        assertEquals(user.getLastName(), retrieved.getLastName());
        assertEquals(user.getEmail(), retrieved.getEmail());
        assertEquals(user.getRoleId(), retrieved.getRoleId());
    }

    @Test
    void findUserById_withNonExistingId_shouldThrowException() {
        assertThrows(Exception.class, () -> userRepository.findById(UUID.randomUUID()));
    }

    @Test
    void deleteUser_withExistingUser_shouldRemoveFromDatabase() {
        User user = User.builder()
                .firstName("Mark")
                .lastName("Smith")
                .email(UUID.randomUUID() + "@example.com")
                .passwordHash("R@ndomP4ssw0rd1!@x")
                .roleId(role.getId())
                .build();

        userRepository.insert(user);
        userRepository.delete(user.getId());

        assertThrows(Exception.class, () -> userRepository.findById(user.getId()));
    }

    @Test
    void findAllUsersPaged_withValidPaginationData_shouldReturnCorrectCount() {
        for (int i = 0; i < 15; i++) {
            User user = User.builder()
                    .firstName("User " + i)
                    .lastName("Test")
                    .email("user" + i + UUID.randomUUID() + "@example.com")
                    .passwordHash("R@ndomP4ssw0rd1!@x")
                    .roleId(role.getId())
                    .build();
            userRepository.insert(user);
        }

        var page1 = userRepository.findAllPaged(1, 10);
        var page2 = userRepository.findAllPaged(2, 10);

        assertEquals(10, page1.size());
        assertEquals(5, page2.size());
    }

    static Stream<Arguments> invalidPaginationData() {
        return Stream.of(Arguments.of(0, 0), Arguments.of(1, 0), Arguments.of(1, -1), Arguments.of(0, -1));
    }

    @ParameterizedTest
    @MethodSource("invalidPaginationData")
    void findAllUsersPaged_withInvalidLimit_shouldThrowException(int offset, int limit) {
        assertThrows(IllegalArgumentException.class, () -> userRepository.findAllPaged(offset, limit));
    }
}
