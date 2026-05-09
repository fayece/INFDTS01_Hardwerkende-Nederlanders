package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.RoleRepository;
import nl.hardwerkendenederlanders.hrcms.models.Role;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcRoleRepository implements RoleRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public JdbcRoleRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private RowMapper<Role> rowMapper() {
        return (rs, _) -> Role.builder()
                .id(UUID.fromString(rs.getString("id")))
                .roleName(rs.getString("role_name"))
                .internalName(rs.getString("internal_name"))
                .build();
    }

    private MapSqlParameterSource paramsFromRole(Role role) {
        return new MapSqlParameterSource()
                .addValue("id", role.getId())
                .addValue("roleName", role.getRoleName())
                .addValue("internalName", role.getInternalName());
    }

    @Override
    public void insert(Role entity) {

        String sql = """
            INSERT INTO roles (id, role_name)
            VALUES (:id, :roleName);
            """;

        jdbc.update(sql, paramsFromRole(entity));
    }

    @Override
    public void update(Role entity) {

        String sql = """
            UPDATE roles
            SET role_name = :roleName
            WHERE id = :id;
            """;

        jdbc.update(sql, paramsFromRole(entity));
    }

    @Override
    public Optional<Role> findById(UUID id) {

        String sql = """
            SELECT *
            FROM roles
            WHERE id = :id;
            """;

        List<Role> results = jdbc.query(sql, Map.of("id", id), rowMapper());
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    @Override
    public void delete(UUID id) {

        String sql = """
            DELETE FROM roles
            WHERE id = :id;
            """;

        jdbc.update(sql, Map.of("id", id));
    }

    @Override
    public List<Role> findAllPaged(int page, int limit) {
        if (limit <= 0) throw new IllegalArgumentException("Limit must be greater than 0.");
        if (page <= 0) throw new IllegalArgumentException("Page must be greater than 0.");
        int offset = (page - 1) * limit;

        String sql = """
            SELECT *
            FROM roles
            LIMIT :limit
            OFFSET :offset;
            """;

        return jdbc.query(sql, Map.of("limit", limit, "offset", offset), rowMapper());
    }
}
