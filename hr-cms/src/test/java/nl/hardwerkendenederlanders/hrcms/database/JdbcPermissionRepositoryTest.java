package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcPermissionRepository;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcUserRepository;
import nl.hardwerkendenederlanders.hrcms.models.Permission;
import nl.hardwerkendenederlanders.hrcms.models.Role;
import nl.hardwerkendenederlanders.hrcms.models.RolePermission;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
public class JdbcPermissionRepositoryTest {

    @Autowired
    private JdbcPermissionRepository permissionRepository;

    @Autowired
    private JdbcUserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Permission permission;
    private User user;

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "role_permissions");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "roles");
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");

        String sql = "SELECT id, resource, action_name, permission_key FROM permissions LIMIT 1";
        permission = jdbcTemplate.queryForObject(
                sql,
                (rs, _) -> Permission.builder()
                        .id(rs.getObject("id", java.util.UUID.class))
                        .resource(rs.getString("resource"))
                        .actionName(rs.getString("action_name"))
                        .permissionKey(rs.getString("permission_key"))
                        .build());

        Role role = Role.of("test_role").build();
        RoleTestSupport.insertRole(jdbcTemplate, role);

        RolePermission rolePermission = new RolePermission(role.getId(), permission.getId());
        RolePermissionTestSupport.insertRolePermission(jdbcTemplate, rolePermission);

        user = User.builder()
                .firstName("Test")
                .lastName("User")
                .passwordHash("hash123-005-12X")
                .roleId(role.getId())
                .build();
        userRepository.insert(user);
    }

    @Test
    void hasPermission_returnsTrueIfUserHasPermission() {
        boolean result = permissionRepository.hasPermission(user.getId(), permission.getPermissionKey());
        assertTrue(result);
    }

    @Test
    void hasPermission_returnsFalseIfUserDoesNotHavePermission() {
        boolean result = permissionRepository.hasPermission(user.getId(), "different:permission");
        assertFalse(result);
    }
}
