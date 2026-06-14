package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.RolePermissionRepository;
import nl.hardwerkendenederlanders.hrcms.models.RolePermission;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcRolePermissionRepository implements RolePermissionRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public JdbcRolePermissionRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private RowMapper<RolePermission> rowMapper() {
        return (rs, _) -> new RolePermission(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("role_id")),
                UUID.fromString(rs.getString("permission_id")));
    }

    @Override
    public Optional<RolePermission> findById(UUID id) {
        String sql = """
            SELECT *
            FROM role_permissions
            WHERE id = :id;
            """;

        List<RolePermission> results = jdbc.query(sql, Map.of("id", id), rowMapper());
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    @Override
    public List<RolePermission> findAllPaged(int page, int limit) {
        if (limit <= 0) throw new IllegalArgumentException("Limit must be greater than 0.");
        if (page <= 0) throw new IllegalArgumentException("Page must be greater than 0.");
        int offset = (page - 1) * limit;

        String sql = """
            SELECT *
            FROM role_permissions
            LIMIT :limit
            OFFSET :offset;
            """;

        return jdbc.query(sql, Map.of("limit", limit, "offset", offset), rowMapper());
    }
}
