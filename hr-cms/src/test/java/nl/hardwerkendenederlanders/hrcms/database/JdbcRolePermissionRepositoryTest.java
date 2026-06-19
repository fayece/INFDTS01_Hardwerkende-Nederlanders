package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcRolePermissionRepository;
import nl.hardwerkendenederlanders.hrcms.models.Permission;
import nl.hardwerkendenederlanders.hrcms.models.Role;
import nl.hardwerkendenederlanders.hrcms.models.RolePermission;
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
public class JdbcRolePermissionRepositoryTest {

    @Autowired
    private JdbcRolePermissionRepository jdbcRolePermissionRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Role role;
    private Permission permission;

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "role_permissions");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "roles");

        role = Role.of("Editor").build();
        RoleTestSupport.insertRole(jdbcTemplate, role);

        String sql = "SELECT id, resource, action_name, permission_key FROM permissions LIMIT 1";
        permission = jdbcTemplate.queryForObject(
                sql,
                (rs, _) -> Permission.builder()
                        .id(UUID.fromString(rs.getString("id")))
                        .resource(rs.getString("resource"))
                        .actionName(rs.getString("action_name"))
                        .permissionKey(rs.getString("permission_key"))
                        .build());
    }

    @Test
    void findRolePermissionById_withExistingId_shouldReturnRolePermission() {
        RolePermission rolePermission = new RolePermission(role.getId(), permission.getId());

        RolePermissionTestSupport.insertRolePermission(jdbcTemplate, rolePermission);

        RolePermission retrieved =
                jdbcRolePermissionRepository.findById(rolePermission.getId()).orElse(null);

        assertNotNull(retrieved);
        assertEquals(rolePermission.getId(), retrieved.getId());
        assertEquals(rolePermission.getRoleId(), retrieved.getRoleId());
        assertEquals(rolePermission.getPermissionId(), retrieved.getPermissionId());
    }

    @Test
    void findRolePermissionById_withNonExistingId_shouldReturnEmptyOptional() {
        Optional<RolePermission> result = jdbcRolePermissionRepository.findById(UUID.randomUUID());
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllRolePermissionsPaged_withValidPaginationData_shouldReturnCorrectCount() {
        for (int i = 0; i < 15; i++) {
            Role newRole = Role.of("Role " + i).build();
            RoleTestSupport.insertRole(jdbcTemplate, newRole);
            RolePermissionTestSupport.insertRolePermission(
                    jdbcTemplate, new RolePermission(newRole.getId(), permission.getId()));
        }

        var page1 = jdbcRolePermissionRepository.findAllPaged(1, 10);
        var page2 = jdbcRolePermissionRepository.findAllPaged(2, 10);

        assertEquals(10, page1.size());
        assertEquals(5, page2.size());
    }

    static Stream<Arguments> invalidPaginationData() {
        return Stream.of(Arguments.of(0, 0), Arguments.of(1, 0), Arguments.of(1, -1), Arguments.of(0, -1));
    }

    @ParameterizedTest
    @MethodSource("invalidPaginationData")
    void findAllRolePermissionsPaged_withInvalidLimit_shouldThrowException(int offset, int limit) {
        assertThrows(IllegalArgumentException.class, () -> jdbcRolePermissionRepository.findAllPaged(offset, limit));
    }
}
