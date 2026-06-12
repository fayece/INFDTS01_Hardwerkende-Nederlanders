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
import org.springframework.transaction.annotation.Transactional;

@Repository
@AllArgsConstructor
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private static final String BASE_SELECT = """
            SELECT u.id, p.first_name, p.prefix, p.last_name, p.role_id, p.active, p.created_at
            FROM users u
            JOIN pii.users_pii p ON u.id = p.user_id
            """;

    private static final String AUTH_SELECT = """
            SELECT u.id, p.first_name, p.prefix, p.last_name, ps.password_hash, p.role_id, p.active, p.created_at
            FROM users u
            JOIN pii.users_pii p ON u.id = p.user_id
            JOIN pii_strict.users_pii_strict ps ON u.id = ps.user_id
            """;

    private final RowMapper<User> rowMapper = (rs, rowNum) -> new User(
            UUID.fromString(rs.getString("id")),
            rs.getString("first_name"),
            rs.getString("prefix"),
            rs.getString("last_name"),
            null,
            rs.getString("role_id") != null ? UUID.fromString(rs.getString("role_id")) : null,
            rs.getBoolean("active"),
            rs.getObject("created_at", OffsetDateTime.class));

    private final RowMapper<User> authRowMapper = (rs, rowNum) -> new User(
            UUID.fromString(rs.getString("id")),
            rs.getString("first_name"),
            rs.getString("prefix"),
            rs.getString("last_name"),
            rs.getString("password_hash"),
            rs.getString("role_id") != null ? UUID.fromString(rs.getString("role_id")) : null,
            rs.getBoolean("active"),
            rs.getObject("created_at", OffsetDateTime.class));

    private MapSqlParameterSource paramsFromUser(User user) {
        return new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("firstName", user.getFirstName())
                .addValue("prefix", user.getPrefix())
                .addValue("lastName", user.getLastName())
                .addValue("passwordHash", user.getPasswordHash())
                .addValue("roleId", user.getRoleId())
                .addValue("active", user.isActive())
                .addValue("createdAt", user.getCreatedAt());
    }

    @Override
    @Transactional
    public void insert(User user) {
        jdbc.update("INSERT INTO users (id) VALUES (:id)", paramsFromUser(user));

        jdbc.update("""
                INSERT INTO pii.users_pii (user_id, first_name, prefix, last_name, role_id, created_at, active)
                VALUES (:id, :firstName, :prefix, :lastName, :roleId, :createdAt, :active)
                """, paramsFromUser(user));

        jdbc.update(
                "INSERT INTO pii_strict.users_pii_strict (user_id, password_hash) VALUES (:id, :passwordHash)",
                paramsFromUser(user));
    }

    @Override
    public Optional<User> findById(UUID id) {
        String sql = AUTH_SELECT + "WHERE u.id = :id";
        return jdbc.query(sql, new MapSqlParameterSource("id", id), authRowMapper).stream()
                .findFirst();
    }

    @Override
    public Optional<String> findPasswordHashById(UUID id) {
        String sql = "SELECT password_hash FROM pii_strict.users_pii_strict WHERE user_id = :id";
        return jdbc
                .query(sql, new MapSqlParameterSource("id", id), (rs, rowNum) -> rs.getString("password_hash"))
                .stream()
                .findFirst();
    }

    @Override
    public Optional<UUID> findRoleIdById(UUID id) {
        String sql = "SELECT role_id FROM pii.users_pii WHERE user_id = :id";
        return jdbc
                .query(sql, new MapSqlParameterSource("id", id), (rs, rowNum) -> {
                    String roleId = rs.getString("role_id");
                    return roleId != null ? UUID.fromString(roleId) : null;
                })
                .stream()
                .findFirst();
    }

    @Override
    public List<User> findByNamePaginated(String name, int page, int amount) {
        String sqlQuery = BASE_SELECT + """
                WHERE p.first_name ILIKE :name
                OR p.last_name ILIKE :name
                OR (p.first_name || ' ' || p.last_name) ILIKE :name
                ORDER BY p.last_name
                LIMIT :limit
                OFFSET :offset
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("name", "%" + name + "%")
                .addValue("limit", amount)
                .addValue("offset", page * amount);

        return jdbc.query(sqlQuery, params, rowMapper);
    }

    @Override
    public Integer countByNamePaginated(String name) {
        String sqlQuery = """
                SELECT COUNT(*)
                FROM users u
                JOIN pii.users_pii p ON u.id = p.user_id
                WHERE p.first_name ILIKE :name
                OR p.last_name ILIKE :name
                OR (p.first_name || ' ' || p.last_name) ILIKE :name
                """;

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("name", "%" + name + "%");

        return jdbc.queryForObject(sqlQuery, params, Integer.class);
    }

    @Override
    public List<User> findUserOnActivityPaginated(boolean isActive, int page, int amount) {
        String sqlQuery = BASE_SELECT + """
                WHERE p.active = :isActive
                ORDER BY p.last_name
                LIMIT :limit
                OFFSET :offset
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("isActive", isActive)
                .addValue("limit", amount)
                .addValue("offset", amount * page);

        return jdbc.query(sqlQuery, params, rowMapper);
    }

    @Override
    public List<User> findAllUsers() {
        return jdbc.query(BASE_SELECT, rowMapper);
    }

    @Override
    public List<User> findAllPaginated(Integer page, Integer amount) {
        String sqlQuery = BASE_SELECT + """
                ORDER BY p.last_name
                LIMIT :limit
                OFFSET :offset
                """;

        MapSqlParameterSource params =
                new MapSqlParameterSource().addValue("limit", amount).addValue("offset", page * amount);

        return jdbc.query(sqlQuery, params, rowMapper);
    }

    @Override
    public void updateActivityById(UUID id, boolean setActive) {

        String sqlQuery = """
                UPDATE pii.users_pii
                SET active = :setActive
                WHERE user_id = :id
                """;

        MapSqlParameterSource params =
                new MapSqlParameterSource().addValue("id", id).addValue("setActive", setActive);

        jdbc.update(sqlQuery, params);
    }

    @Override
    public void update(User user) {
        String sqlQuery = """
                UPDATE pii.users_pii
                SET first_name = :firstName,
                prefix = :prefix,
                last_name = :lastName,
                role_id = :roleId,
                active = :active
                WHERE user_id = :id
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("prefix", user.getPrefix())
                .addValue("lastName", user.getLastName())
                .addValue("roleId", user.getRoleId())
                .addValue("active", user.isActive())
                .addValue("id", user.getId());

        jdbc.update(sqlQuery, params);
    }

    @Override
    public void updatePasswordSelf(User user) {
        String sqlQuery = """
            UPDATE pii_strict.users_pii_strict
            SET password_hash = :passwordHash
            WHERE user_id = :id
            """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("passwordHash", user.getPasswordHash())
                .addValue("id", user.getId());

        jdbc.update(sqlQuery, params);
    }

    @Override
    public void deleteById(UUID id) {
        jdbc.update("DELETE FROM users WHERE id = :id", new MapSqlParameterSource("id", id));
    }

    @Override
    public Integer countByActive(boolean active) {
        String sqlQuery = """
                SELECT COUNT(*)
                FROM users u
                JOIN pii.users_pii p ON u.id = p.user_id
                WHERE p.active = :active
                """;

        return jdbc.queryForObject(sqlQuery, new MapSqlParameterSource("active", active), Integer.class);
    }

    @Override
    public Integer countAll() {
        String sqlQuery = """
                SELECT COUNT(*)
                FROM users
                """;

        return jdbc.queryForObject(sqlQuery, new MapSqlParameterSource(), Integer.class);
    }
}
