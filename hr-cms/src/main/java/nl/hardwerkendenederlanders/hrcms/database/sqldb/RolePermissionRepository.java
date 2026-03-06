package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.RolePermission;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class RolePermissionRepository extends JdbcRepository<RolePermission> {

    public RolePermissionRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        super(jdbc, resourceLoader, RolePermission.class);
    }

    @Override
    protected RowMapper<RolePermission> rowMapper() {
        return (rs, _) -> new RolePermission(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("role_id")),
                UUID.fromString(rs.getString("permission_id")));
    }
}
