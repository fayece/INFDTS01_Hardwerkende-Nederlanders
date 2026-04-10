package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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
    public void update(User user) {}

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

    public List<User> findByName(String name){
        String sqlQuery = """
                SELECT *
                FROM %s
                WHERE first_name LIKE :name
                """.formatted(TABLE);

        MapSqlParameterSource params = new MapSqlParameterSource("name","%" + name + "%");

        return jdbc.query(sqlQuery, params, rowMapper);
    }

    public List<User> findUserOnActivity(boolean isActive){
        String sqlQuery = """
                SELECT *
                FROM %s
                WHERE active = :isActive
                """.formatted(TABLE);

        MapSqlParameterSource params = new MapSqlParameterSource("active", isActive);

        return jdbc.query(sqlQuery, params, rowMapper);
    }



    public List<User> findAllUsers(){
        String sqlQuery = "SELECT * FROM %s".formatted(TABLE);
        return jdbc.query(sqlQuery, rowMapper);
    }

    public boolean updateActivityById(UUID id, boolean setActive){
        String sqlQuery = """
                UPDATE %s
                SET active = :setActive
                WHERE id = :id
                """.formatted(TABLE);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("setActive", setActive);

        return jdbc.update(sqlQuery, params) >= 1;
    }

    public boolean updateUser(User user){
        String sqlQuery = """
                UPDATE %s
                SET first_name = :firstName,
                prefix = :prefix,
                last_name = :lastName,
                email_address = ":emailAddress,
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

        return jdbc.update(sqlQuery, params) >= 1;
    }

    @Override
    public boolean deleteById(UUID id){
        String sqlQuery = """
                DELETE FROM %s
                WHERE id = :id
                """.formatted(TABLE);

        MapSqlParameterSource params = new MapSqlParameterSource().addValue("id", id);

        return jdbc.update(sqlQuery, params) >= 1;
    }
}
