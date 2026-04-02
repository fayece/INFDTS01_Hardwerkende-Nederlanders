package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcTemplate jdbc;
    private static final String TABLE = "users";
    public JdbcUserRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<User> rowMapper = (rs, rowNum) -> new User(
            UUID.fromString(rs.getString("id")),
            rs.getString("first_name"),
            rs.getString("prefix"),
            rs.getString("last_name"),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getString("role_id") != null ? UUID.fromString(rs.getString("role_id")) : null,
            rs.getString("organization_id") != null ? UUID.fromString(rs.getString("organization_id")) : null,
            rs.getBoolean("active"),
            rs.getObject("created_at", OffsetDateTime.class)
    );

    private MapSqlParameterSource paramsFromUser(User user) {
        return new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("firstName", user.getFirstName())
                .addValue("prefix", user.getPrefix())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("passwordHash", user.getPasswordHash())
                .addValue("roleId", user.getRoleId())
                .addValue("organizationId", user.getOrganizationId())
                .addValue("active", user.isActive())
                .addValue("createdAt", user.getCreatedAt());
    }

    @Override
    public void insert(User user) {
        String sql = """
                INSERT INTO %s (
                id, first_name, prefix, last_name, email, password_hash, role_id, organization_id, active, created_at)
                VALUES (
                :id, :firstName, :prefix, :lastName, :email, :passwordHash, :roleId, :organizationId, :active, :createdAt)
                """.formatted(TABLE);
        jdbc.update(sql, paramsFromUser(user));
    }

    @Override
    public void update(User user) {

    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void deleteById(UUID id) {

    }
}