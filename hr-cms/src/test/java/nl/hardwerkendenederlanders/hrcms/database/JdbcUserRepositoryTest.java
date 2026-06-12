package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcRoleRepository;
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
    private JdbcRoleRepository roleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
    }

    @Test
    void insert_shouldInsertUser() {
        User user = new User(
                UUID.randomUUID(), "Kim", null, "Possible", "hashedPassword", null, true, OffsetDateTime.now());

        repository.insert(user);
        Optional<User> fromDb = repository.findById(user.getId());
        assertEquals(user.getId(), fromDb.get().getId());
    }

    @Test
    void findById_shouldReturnOneUserWithSameId() {
        User user = new User(
                UUID.randomUUID(), "Kim", null, "Possible", "hashedPassword", null, true, OffsetDateTime.now());

        repository.insert(user);

        Optional<User> userFromDb = repository.findById(user.getId());

        assertEquals(user.getId(), userFromDb.get().getId());
    }

    @Test
    void findByNamePaginated_shouldReturnUser() {
        User user = new User(
                UUID.randomUUID(), "Kim", null, "Possible", "hashedPassword", null, true, OffsetDateTime.now());

        repository.insert(user);

        List<User> byName = repository.findByNamePaginated("kim", 0, 1);
        assertEquals(user.getFirstName(), (byName.get(0)).getFirstName());
    }

    @Test
    void findUserOnActivityPaginated_shouldReturnUsersOnActivity() {
        User user = new User(
                UUID.randomUUID(), "Kim", null, "Possible", "hashedPassword", null, true, OffsetDateTime.now());
        User user2 = new User(
                UUID.randomUUID(), "Kim2", null, "Possible2", "hashedPassword", null, false, OffsetDateTime.now());

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
                UUID.randomUUID(), "Kim", null, "Possible", "hashedPassword", null, true, OffsetDateTime.now());
        User user2 = new User(
                UUID.randomUUID(), "Kim2", null, "Possible2", "hashedPassword", null, false, OffsetDateTime.now());

        repository.insert(user);
        repository.insert(user2);

        List<User> allUsers = repository.findAllUsers();
        assertEquals(2, allUsers.size());
    }

    @Test
    void updateActivityById_shouldSetActive() {
        User user = new User(
                UUID.randomUUID(), "Kim", null, "Possible", "hashedPassword", null, true, OffsetDateTime.now());

        repository.insert(user);

        repository.updateActivityById(user.getId(), false);
        Optional<User> userFromDb = repository.findById(user.getId());
        assertFalse(userFromDb.get().isActive());
    }

    @Test
    void update_shouldChangeAllUserFields() {
        User user = new User(
                UUID.randomUUID(), "Kim", null, "Possible", "hashedPassword", null, true, OffsetDateTime.now());

        repository.insert(user);

        User updatedUser =
                new User(user.getId(), "Kom", null, "Pissable", "hashedPassword", null, false, OffsetDateTime.now());

        repository.update(updatedUser);

        Optional<User> fromDb = repository.findById(user.getId());
        assertEquals(updatedUser.getFirstName(), fromDb.get().getFirstName());
        assertEquals(updatedUser.getLastName(), fromDb.get().getLastName());
        assertFalse(fromDb.get().isActive());
    }

    @Test
    void updatePasswordSelf_shouldChangePasswordHash() {
        User user = new User(
                UUID.randomUUID(), "Kim", null, "Possible", "hashedPassword", null, true, OffsetDateTime.now());

        repository.insert(user);

        User userWithNewPassword =
                new User(user.getId(), "Kim", null, "Possible", "newHashedPassword", null, true, OffsetDateTime.now());

        repository.updatePasswordSelf(userWithNewPassword);

        Optional<User> fromDb = repository.findById(user.getId());
        assertTrue(fromDb.isPresent());
        assertEquals("newHashedPassword", fromDb.get().getPasswordHash());
    }

    @Test
    void updatePasswordSelf_unknownUser_doesNothing() {
        User unknownUser = new User(
                UUID.randomUUID(), "Kim", null, "Possible", "newHashedPassword", null, true, OffsetDateTime.now());

        assertDoesNotThrow(() -> repository.updatePasswordSelf(unknownUser));
        assertEquals(Optional.empty(), repository.findById(unknownUser.getId()));
    }

    @Test
    void findRoleIdById_shouldReturnRoleId() {
        UUID roleId = roleRepository.findAll().getFirst().getId();
        User user = new User(UUID.randomUUID(), "Kim", null, "Possible", "hashedPassword", roleId, true, OffsetDateTime.now());

        repository.insert(user);

        Optional<UUID> foundRoleId = repository.findRoleIdById(user.getId());

        assertEquals(Optional.of(roleId), foundRoleId);
    }

    @Test
    void findRoleIdById_unknownUser_returnsEmptyOptional() {
        Optional<UUID> foundRoleId = repository.findRoleIdById(UUID.randomUUID());

        assertEquals(Optional.empty(), foundRoleId);
    }

    @Test
    void deleteById_shouldReturnOptionalEmpty() {
        User user = new User(
                UUID.randomUUID(), "Kim", null, "Possible", "hashedPassword", null, true, OffsetDateTime.now());

        repository.insert(user);
        repository.deleteById(user.getId());

        assertEquals(Optional.empty(), repository.findById(user.getId()));
    }
}
