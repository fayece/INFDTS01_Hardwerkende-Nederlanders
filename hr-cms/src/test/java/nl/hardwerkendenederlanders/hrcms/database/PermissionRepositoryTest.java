package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcPermissionRepository;
import nl.hardwerkendenederlanders.hrcms.models.Permission;
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
public class PermissionRepositoryTest {

    @Autowired
    private JdbcPermissionRepository permissionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "role_permissions");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "permissions");
    }

    @Test
    void insertPermission_withValidPermission_shouldPersistAndRetrieve() {
        Permission permission = Permission.of("article", "read").build();

        permissionRepository.insert(permission);

        Permission retrieved = permissionRepository.findById(permission.getId()).orElse(null);

        assertNotNull(retrieved);
        assertEquals(permission.getId(), retrieved.getId());
        assertEquals(permission.getResource(), retrieved.getResource());
        assertEquals(permission.getActionName(), retrieved.getActionName());
        assertEquals(permission.getResource() + ":" + permission.getActionName(), retrieved.getPermissionKey());
    }

    @Test
    void findPermissionById_withExistingId_shouldReturnPermission() {
        Permission permission = Permission.of("article", "write").build();

        permissionRepository.insert(permission);

        Permission retrieved = permissionRepository.findById(permission.getId()).orElse(null);

        assertNotNull(retrieved);
        assertEquals(permission.getId(), retrieved.getId());
        assertEquals(permission.getResource(), retrieved.getResource());
        assertEquals(permission.getActionName(), retrieved.getActionName());
        assertEquals(permission.getResource() + ":" + permission.getActionName(), retrieved.getPermissionKey());
    }

    @Test
    void findPermissionById_withNonExistingId_shouldReturnEmptyOptional() {
        Optional<Permission> result = permissionRepository.findById(UUID.randomUUID());
        assertTrue(result.isEmpty());
    }

    @Test
    void deletePermission_withExistingPermission_shouldRemoveFromDatabase() {
        Permission permission = Permission.of("article", "delete").build();

        permissionRepository.insert(permission);
        permissionRepository.delete(permission.getId());

        Optional<Permission> result = permissionRepository.findById(permission.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllPermissionsPaged_withValidPaginationData_shouldReturnCorrectCount() {
        String[] actions = {
            "read", "write", "delete", "publish", "archive", "restore", "comment", "like", "share", "export", "import",
            "approve", "reject", "tag", "untag"
        };

        for (int i = 0; i < 15; i++) {
            Permission permission = Permission.of("article", actions[i]).build();
            permissionRepository.insert(permission);
        }

        var page1 = permissionRepository.findAllPaged(1, 10);
        var page2 = permissionRepository.findAllPaged(2, 10);

        assertEquals(10, page1.size());
        assertEquals(5, page2.size());
    }

    static Stream<Arguments> invalidPaginationData() {
        return Stream.of(Arguments.of(0, 0), Arguments.of(1, 0), Arguments.of(1, -1), Arguments.of(0, -1));
    }

    @ParameterizedTest
    @MethodSource("invalidPaginationData")
    void findAllPermissionsPaged_withInvalidLimit_shouldThrowException(int offset, int limit) {
        assertThrows(IllegalArgumentException.class, () -> permissionRepository.findAllPaged(offset, limit));
    }
}
