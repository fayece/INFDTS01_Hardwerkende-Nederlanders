package nl.hardwerkendenederlanders.hrcms.database;

import java.sql.PreparedStatement;
import nl.hardwerkendenederlanders.hrcms.models.RolePermission;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * role_permissions is flyway-managed reference data; cms_app has no INSERT/UPDATE/DELETE on
 * public.role_permissions. Tests that need their own role_permission fixtures must escalate to
 * cms_superuser for the insert.
 */
public class RolePermissionTestSupport {

    public static void insertRolePermission(JdbcTemplate jdbcTemplate, RolePermission rolePermission) {
        jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
            try (PreparedStatement setRole = connection.prepareStatement("SET ROLE cms_superuser")) {
                setRole.execute();
            }

            try (PreparedStatement insert = connection.prepareStatement(
                    "INSERT INTO role_permissions (id, role_id, permission_id) VALUES (?, ?, ?)")) {
                insert.setObject(1, rolePermission.getId());
                insert.setObject(2, rolePermission.getRoleId());
                insert.setObject(3, rolePermission.getPermissionId());
                insert.execute();
            }

            try (PreparedStatement resetRole = connection.prepareStatement("RESET ROLE")) {
                resetRole.execute();
            }

            return null;
        });
    }
}
