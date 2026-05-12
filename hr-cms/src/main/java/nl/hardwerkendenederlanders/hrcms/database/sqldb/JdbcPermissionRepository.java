package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.util.*;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.PermissionRepository;
import nl.hardwerkendenederlanders.hrcms.models.Permission;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcPermissionRepository implements PermissionRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public JdbcPermissionRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private RowMapper<Permission> rowMapper() {
        return (rs, _) -> Permission.builder()
                .id(UUID.fromString(rs.getString("id")))
                .resource(rs.getString("resource"))
                .actionName(rs.getString("action_name"))
                .permissionKey(rs.getString("permission_key"))
                .build();
    }

    private MapSqlParameterSource paramsFromPermission(Permission permission) {
        return new MapSqlParameterSource()
                .addValue("id", permission.getId())
                .addValue("resource", permission.getResource())
                .addValue("actionName", permission.getActionName());
    }

    public boolean hasPermission(UUID userId, String permissionKey) {
        String sql = """
            SELECT COUNT(*) > 0
            FROM permissions p
            JOIN role_permissions rp ON p.id = rp.permission_id
            JOIN users u ON rp.role_id = u.role_id
            WHERE u.id = :userId AND p.permission_key = :permissionKey;
            """;

        return Boolean.TRUE.equals(
                jdbc.queryForObject(sql, Map.of("userId", userId, "permissionKey", permissionKey), Boolean.class));
    }
}
