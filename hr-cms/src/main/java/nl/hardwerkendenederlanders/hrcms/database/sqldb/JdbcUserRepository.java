package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.time.OffsetDateTime;
import java.util.*;
import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcTemplate jdbc;
    private static final String TABLE = "users";

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
            rs.getObject("created_at", OffsetDateTime.class));

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
    public Optional<User> findById(UUID id) {
        String sqlQuery = """
                SELECT * FROM %s
                WHERE id = :id
                """.formatted(TABLE);

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);
        List<User> users = jdbc.query(sqlQuery, params, rowMapper);
        return users.stream().findFirst();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sqlQuery = """
                SELECT * FROM %s
                WHERE email = :email
                """.formatted(TABLE);

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("email", email);

        List<User> users = jdbc.query(sqlQuery, params, rowMapper);
        return users.stream().findFirst();
    }

    @Override
    public List<User> findByNameOrEmailPaginated(String name, int page, int amount) {
        String sqlQuery = """
                SELECT *
                FROM %s
                WHERE first_name ILIKE :name
                OR last_name ILIKE :name
                OR email ILIKE :name
                OR (first_name || ' ' || last_name) ILIKE :name
                ORDER BY last_name
                LIMIT :limit
                OFFSET :offset
                """.formatted(TABLE);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", "%" + name + "%")
                .addValue("limit", amount)
                .addValue("offset", page * amount);

        return jdbc.query(sqlQuery, params, rowMapper);
    }

    @Override
    public Integer countByNameOrEmailPaginated(String name) {
        String sqlQuery = """
                SELECT COUNT(*)
                FROM %s
                WHERE first_name ILIKE :name
                OR last_name ILIKE :name
                OR email ILIKE :name
                OR (first_name || ' ' || last_name) ILIKE :name
                """.formatted(TABLE);

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("name", "%" + name + "%");

        return jdbc.queryForObject(sqlQuery, params, Integer.class);
    }

    @Override
    public List<User> findUserOnActivityPaginated(boolean isActive, int page, int amount) {
        String sqlQuery = """
                SELECT *
                FROM %s
                WHERE active = :isActive
                ORDER BY last_name
                LIMIT :limit
                OFFSET :offset
                """.formatted(TABLE);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("isActive", isActive)
                .addValue("limit", amount)
                .addValue("offset", amount * page);

        return jdbc.query(sqlQuery, params, rowMapper);
    }

    @Override
    public List<User> findAllUsers() {
        String sqlQuery = "SELECT * FROM %s".formatted(TABLE);
        return jdbc.query(sqlQuery, rowMapper);
    }

    @Override
    public List<User> findAllPaginated(Integer page, Integer amount) {
        String sqlQuery = """
                SELECT *
                FROM %s
                ORDER BY last_name
                LIMIT :limit
                OFFSET :offset
                """.formatted(TABLE);

        MapSqlParameterSource params =
                new MapSqlParameterSource().addValue("limit", amount).addValue("offset", page * amount);

        return jdbc.query(sqlQuery, params, rowMapper);
    }

    @Override
    public void updateActivityById(UUID id, boolean setActive) {
        String sqlQuery = """
                UPDATE %s
                SET active = :setActive
                WHERE id = :id
                """.formatted(TABLE);

        MapSqlParameterSource params =
                new MapSqlParameterSource().addValue("id", id).addValue("setActive", setActive);

        jdbc.update(sqlQuery, params);
    }

    @Override
    public void update(User user) {
        String sqlQuery = """
                UPDATE %s
                SET first_name = :firstName,
                prefix = :prefix,
                last_name = :lastName,
                email = :emailAddress,
                role_id = :roleId,
                organization_id = :organizationId,
                active = :active
                WHERE id = :id
                """.formatted(TABLE);
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("prefix", user.getPrefix())
                .addValue("lastName", user.getLastName())
                .addValue("emailAddress", user.getEmail())
                .addValue("roleId", user.getRoleId())
                .addValue("organizationId", user.getOrganizationId())
                .addValue("active", user.isActive())
                .addValue("id", user.getId());

        jdbc.update(sqlQuery, params);
    }

    @Override
    public void deleteById(UUID id) {
        String sqlQuery = """
                DELETE FROM %s
                WHERE id = :id
                """.formatted(TABLE);

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);

        jdbc.update(sqlQuery, params);
    }

    @Override
    public Integer countByActive(boolean active) {
        String sqlQuery = """
                SELECT COUNT(*)
                FROM %s
                WHERE active = :active
                """.formatted(TABLE);

        MapSqlParameterSource params = new MapSqlParameterSource("active", active);

        return jdbc.queryForObject(sqlQuery, params, Integer.class);
    }

    @Override
    public Integer countAll() {
        String sqlQuery = """
                SELECT COUNT(*)
                FROM %s
                """.formatted(TABLE);

        return jdbc.queryForObject(sqlQuery, new MapSqlParameterSource(), Integer.class);
    }
}
