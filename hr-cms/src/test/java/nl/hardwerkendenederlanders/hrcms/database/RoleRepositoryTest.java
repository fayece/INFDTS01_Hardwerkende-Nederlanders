package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.RoleRepository;
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
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "roles");
    }

    @Test
    void insertRole_withValidRole_shouldPersistAndRetrieve() {
        Role role = new Role("Editor");

        roleRepository.insert(role);

        Role retrieved = roleRepository.findById(role.getId()).orElse(null);

        assertNotNull(retrieved);
        assertEquals(role.getId(), retrieved.getId());
        assertEquals(role.getRoleName(), retrieved.getRoleName());
        assertEquals(role.getInternalName(), retrieved.getInternalName());
    }

    @Test
    void updateRole_withModifiedFields_shouldReflectChanges() {
        Role role = new Role("Editor");

        roleRepository.insert(role);

        role.setRoleName("Senior Editor");
        roleRepository.update(role);

        Role retrieved = roleRepository.findById(role.getId()).orElse(null);

        assertNotNull(retrieved);
        assertEquals(role.getId(), retrieved.getId());
        assertEquals("Senior Editor", retrieved.getRoleName());
    }

    @Test
    void findRoleById_withExistingId_shouldReturnRole() {
        Role role = new Role("Reviewer");

        roleRepository.insert(role);

        Role retrieved = roleRepository.findById(role.getId()).orElse(null);

        assertNotNull(retrieved);
        assertEquals(role.getId(), retrieved.getId());
        assertEquals(role.getRoleName(), retrieved.getRoleName());
        assertEquals(role.getInternalName(), retrieved.getInternalName());
    }

    @Test
    void findRoleById_withNonExistingId_shouldReturnEmptyOptional() {
        Optional<Role> result = roleRepository.findById(UUID.randomUUID());
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteRole_withExistingRole_shouldRemoveFromDatabase() {
        Role role = new Role("Moderator");

        roleRepository.insert(role);
        roleRepository.delete(role.getId());

        Optional<Role> result = roleRepository.findById(role.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllRolesPaged_withValidPaginationData_shouldReturnCorrectCount() {
        for (int i = 0; i < 15; i++) {
            Role role = new Role("Role " + i);
            roleRepository.insert(role);
        }

        var page1 = roleRepository.findAllPaged(1, 10);
        var page2 = roleRepository.findAllPaged(2, 10);

        assertEquals(10, page1.size());
        assertEquals(5, page2.size());
    }

    static Stream<Arguments> invalidPaginationData() {
        return Stream.of(Arguments.of(0, 0), Arguments.of(1, 0), Arguments.of(1, -1), Arguments.of(0, -1));
    }

    @ParameterizedTest
    @MethodSource("invalidPaginationData")
    void findAllRolesPaged_withInvalidLimit_shouldThrowException(int offset, int limit) {
        assertThrows(IllegalArgumentException.class, () -> roleRepository.findAllPaged(offset, limit));
    }
}
