package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Permission;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class PermissionRepository extends JdbcRepository<Permission> {

    public PermissionRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        super(jdbc, resourceLoader, Permission.class);
    }

    @Override
    protected RowMapper<Permission> rowMapper() {
        return (rs, _) -> new Permission(
                UUID.fromString(rs.getString("id")),
                rs.getString("resource"),
                rs.getString("action_name"),
                rs.getString("permission_key"),
                rs.getString("internal_name"));
    }
}
