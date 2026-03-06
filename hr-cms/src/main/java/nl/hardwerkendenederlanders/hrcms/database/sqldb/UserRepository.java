package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.time.OffsetDateTime;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class UserRepository extends JdbcMutableRepository<User> {
    public UserRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        super(jdbc, resourceLoader, User.class);
    }

    @Override
    protected RowMapper<User> rowMapper() {
        return (rs, _) -> new User(
                UUID.fromString(rs.getString("id")),
                rs.getString("first_name"),
                rs.getString("prefix"),
                rs.getString("last_name"),
                rs.getString("email"),
                rs.getString("password_hash"),
                // Read V1__create_initial.sql for the rationale behind this nullable foreign key.
                // Should be changed to "UUID.fromString(rs.getString("role_id"))," once roles have been set up.
                rs.getString("role_id") != null ? UUID.fromString(rs.getString("role_id")) : null,
                rs.getString("organization_id") != null ? UUID.fromString(rs.getString("organization_id")) : null,
                rs.getBoolean("active"),
                rs.getObject("created_at", OffsetDateTime.class));
    }
}
