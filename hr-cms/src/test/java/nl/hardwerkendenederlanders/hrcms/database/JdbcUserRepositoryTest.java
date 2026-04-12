package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcUserRepository;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

@SpringBootTest
class JdbcUserRepositoryTest {

    @Autowired
    JdbcUserRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
    }

    @Test
    void insert_shouldReturnTrue() {
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

        assertTrue(repository.insert(user));
    }

    @Test
    void findById_shouldReturnOneUserWithSameId() {
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

        repository.insert(user);

        Optional<User> userFromDb = repository.findById(user.getId());

        assertEquals(user.getId(), userFromDb.get().getId());
    }

    @Test
    void findByEmail_shouldReturnOneUserWithSameEmail() {
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

        repository.insert(user);

        Optional<User> userFromDb = repository.findByEmail(user.getEmail());

        assertEquals(user.getEmail(), userFromDb.get().getEmail());
    }

    @Test
    void findByNameOrEmailPaginated_shouldReturnUser() {
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

        repository.insert(user);

        List<User> byName = repository.findByNameOrEmailPaginated("kim", 0, 1);
        assertEquals(user.getFirstName(), (byName.get(0)).getFirstName());

        List<User> byEmail = repository.findByNameOrEmailPaginated("kp@", 0, 1);
        assertEquals(user.getEmail(), (byEmail.get(0)).getEmail());
    }

    @Test
    void findUserOnActivityPaginated_shouldReturnUsersOnActivity() {
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
                "Kim2",
                null,
                "Possible2",
                "kp2@example.com",
                "hashedPassword",
                null,
                null,
                false,
                OffsetDateTime.now());

        repository.insert(user);
        repository.insert(user2);

        List<User> activeUsers = repository.findUserOnActivityPaginated(true, 0, 1);
        List<User> inActiveUsers = repository.findUserOnActivityPaginated(false, 0, 1);

        assertEquals(user.getId(), (activeUsers.get(0)).getId());
        assertEquals(user2.getId(), (inActiveUsers.get(0)).getId());
    }

    @Test
    void findAllUsers_shouldReturnAListOfAllUsers() {
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
                "Kim2",
                null,
                "Possible2",
                "kp2@example.com",
                "hashedPassword",
                null,
                null,
                false,
                OffsetDateTime.now());

        repository.insert(user);
        repository.insert(user2);

        List<User> allUsers = repository.findAllUsers();
        assertEquals(2, allUsers.size());
    }

    @Test
    void updateActivityById_shouldSetActive() {
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

        repository.insert(user);

        repository.updateActivityById(user.getId(), false);
        Optional<User> userFromDb = repository.findById(user.getId());
        assertFalse(userFromDb.get().isActive());
    }

    @Test
    void update_shouldChangeAllUserFields() {
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

        repository.insert(user);

        User updatedUser = new User(
                user.getId(),
                "Kom",
                null,
                "Pissable",
                "kp@answer.com",
                "hashedPassword",
                null,
                null,
                false,
                OffsetDateTime.now());

        repository.update(updatedUser);

        Optional<User> fromDb = repository.findById(user.getId());
        assertEquals(updatedUser.getFirstName(), fromDb.get().getFirstName());
        assertEquals(updatedUser.getLastName(), fromDb.get().getLastName());
        assertEquals(updatedUser.getEmail(), fromDb.get().getEmail());
        assertFalse(fromDb.get().isActive());
    }

    @Test
    void deleteById_shouldReturnTrue() {
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

        repository.insert(user);

        assertTrue(repository.deleteById(user.getId()));
    }
}
