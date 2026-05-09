package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcRoleRepository;
import nl.hardwerkendenederlanders.hrcms.models.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
public class JdbcRoleRepositoryTest {

    @Autowired
    private JdbcRoleRepository jdbcRoleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "roles");
    }

    @Test
    void insertRole_withValidRole_shouldPersistAndRetrieve() {
        Role role = Role.of("Editor").build();

        jdbcRoleRepository.insert(role);

        Role retrieved = jdbcRoleRepository.findById(role.getId()).orElse(null);

        assertNotNull(retrieved);
        assertEquals(role.getId(), retrieved.getId());
        assertEquals(role.getRoleName(), retrieved.getRoleName());
        assertEquals(role.getRoleName().toUpperCase().replace(" ", "_"), retrieved.getInternalName());
    }

    @Test
    void updateRole_withModifiedFields_shouldReflectChanges() {
        Role role = Role.of("Editor").build();

        jdbcRoleRepository.insert(role);

        role.setRoleName("Senior Editor");
        jdbcRoleRepository.update(role);

        Role retrieved = jdbcRoleRepository.findById(role.getId()).orElse(null);

        assertNotNull(retrieved);
        assertEquals(role.getId(), retrieved.getId());
        assertEquals("Senior Editor", retrieved.getRoleName());
    }

    @Test
    void findRoleById_withExistingId_shouldReturnRole() {
        Role role = Role.of("Reviewer").build();

        jdbcRoleRepository.insert(role);

        Role retrieved = jdbcRoleRepository.findById(role.getId()).orElse(null);

        assertNotNull(retrieved);
        assertEquals(role.getId(), retrieved.getId());
        assertEquals(role.getRoleName(), retrieved.getRoleName());
        assertEquals(role.getRoleName().toUpperCase().replace(" ", "_"), retrieved.getInternalName());
    }

    @Test
    void findRoleById_withNonExistingId_shouldReturnEmptyOptional() {
        Optional<Role> result = jdbcRoleRepository.findById(UUID.randomUUID());
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteRole_withExistingRole_shouldRemoveFromDatabase() {
        Role role = Role.of("Moderator").build();

        jdbcRoleRepository.insert(role);
        jdbcRoleRepository.delete(role.getId());

        Optional<Role> result = jdbcRoleRepository.findById(role.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllRolesPaged_withValidPaginationData_shouldReturnCorrectCount() {
        for (int i = 0; i < 15; i++) {
            Role role = Role.of("Role " + i).build();
            jdbcRoleRepository.insert(role);
        }

        var page1 = jdbcRoleRepository.findAllPaged(1, 10);
        var page2 = jdbcRoleRepository.findAllPaged(2, 10);

        assertEquals(10, page1.size());
        assertEquals(5, page2.size());
    }

    static Stream<Arguments> invalidPaginationData() {
        return Stream.of(Arguments.of(0, 0), Arguments.of(1, 0), Arguments.of(1, -1), Arguments.of(0, -1));
    }

    @ParameterizedTest
    @MethodSource("invalidPaginationData")
    void findAllRolesPaged_withInvalidLimit_shouldThrowException(int offset, int limit) {
        assertThrows(IllegalArgumentException.class, () -> jdbcRoleRepository.findAllPaged(offset, limit));
    }
}
