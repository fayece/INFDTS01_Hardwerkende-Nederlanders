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

    @Override
    public void insert(Permission permission) {
        String sql = """
                INSERT INTO permissions (id, resource, action_name)
                VALUES (:id, :resource, :actionName);
                """;
        jdbc.update(sql, paramsFromPermission(permission));
    }

    @Override
    public void update(Permission permission) {
        String sql = """
                UPDATE permissions
                SET resource = :resource, action_name = :actionName
                WHERE id = :id;
                """;
        jdbc.update(sql, paramsFromPermission(permission));
    }

    @Override
    public Optional<Permission> findById(UUID id) {
        String sql = """
            SELECT *
            FROM permissions
            WHERE id = :id;
            """;

        List<Permission> results = jdbc.query(sql, Map.of("id", id), rowMapper());
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
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

    public Set<String> findUserPermissions(UUID userId) {
        String sql = """
            SELECT DISTINCT p.permission_key
            FROM permissions p
            JOIN role_permissions rp ON p.id = rp.permission_id
            JOIN users u ON rp.role_id = u.role_id
            WHERE u.id = :userId;
            """;

        return new HashSet<>(jdbc.queryForList(sql, Map.of("userId", userId), String.class));
    }

    @Override
    public void delete(UUID id) {
        String sql = """
        DELETE FROM permissions
        WHERE id = :id;
        """;
        jdbc.update(sql, Map.of("id", id));
    }

    @Override
    public List<Permission> findAllPaged(int page, int limit) {
        if (limit <= 0) throw new IllegalArgumentException("Limit must be greater than 0.");
        if (page <= 0) throw new IllegalArgumentException("Page must be greater than 0.");
        int offset = (page - 1) * limit;

        String sql = """
                SELECT *
                FROM permissions
                LIMIT :limit
                OFFSET :offset;
                """;

        return jdbc.query(sql, Map.of("limit", limit, "offset", offset), rowMapper());
    }
}
