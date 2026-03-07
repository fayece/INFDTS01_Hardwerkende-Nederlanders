package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Role;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class RoleRepository extends JdbcMutableRepository<Role> {

    public RoleRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        super(jdbc, resourceLoader, Role.class);
    }

    @Override
    protected RowMapper<Role> rowMapper() {
        return (rs, _) ->
                new Role(UUID.fromString(rs.getString("id")), rs.getString("role_name"), rs.getString("internal_name"));
    }
}
