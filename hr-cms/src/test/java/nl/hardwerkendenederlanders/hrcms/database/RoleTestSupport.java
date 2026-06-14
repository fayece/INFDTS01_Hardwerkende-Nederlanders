package nl.hardwerkendenederlanders.hrcms.database;

import java.sql.PreparedStatement;
import nl.hardwerkendenederlanders.hrcms.models.Role;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Roles are flyway-managed reference data; cms_app has no INSERT/UPDATE/DELETE on public.roles.
 * Tests that need their own role fixtures must escalate to cms_superuser for the insert.
 */
public class RoleTestSupport {

    public static void insertRole(JdbcTemplate jdbcTemplate, Role role) {
        jdbcTemplate.execute((ConnectionCallback<Void>) connection -> {
            try (PreparedStatement setRole = connection.prepareStatement("SET ROLE cms_superuser")) {
                setRole.execute();
            }

            try (PreparedStatement insert =
                    connection.prepareStatement("INSERT INTO roles (id, role_name) VALUES (?, ?)")) {
                insert.setObject(1, role.getId());
                insert.setString(2, role.getRoleName());
                insert.execute();
            }

            try (PreparedStatement resetRole = connection.prepareStatement("RESET ROLE")) {
                resetRole.execute();
            }

            return null;
        });
    }
}
