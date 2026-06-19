package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.sql.DataSource;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@SkipDbSessionContext
class RowLevelSecurityTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void usersPii_userRole_seesOnlyOwnRow() throws SQLException {
        UUID firstUser = UUID.randomUUID();
        UUID secondUser = UUID.randomUUID();

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                seedPiiUser(connection, firstUser, "Alice");
                seedPiiUser(connection, secondUser, "Bob");

                try (Statement statement = connection.createStatement()) {
                    statement.execute("SET ROLE cms_role_user");
                    statement.execute("SELECT set_config('app.current_user_id', '" + firstUser + "', false)");

                    ResultSet rs = statement.executeQuery("SELECT user_id FROM pii.users_pii");
                    Set<UUID> visible = new HashSet<>();
                    while (rs.next()) visible.add(UUID.fromString(rs.getString("user_id")));
                    assertEquals(Set.of(firstUser), visible);
                }
            } finally {
                connection.rollback();
            }
        }
    }

    @Test
    void usersPii_administratorRole_seesAllRows() throws SQLException {
        UUID firstUser = UUID.randomUUID();
        UUID secondUser = UUID.randomUUID();

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try {
                seedPiiUser(connection, firstUser, "Alice");
                seedPiiUser(connection, secondUser, "Bob");

                try (Statement statement = connection.createStatement()) {
                    statement.execute("SET ROLE cms_role_administrator");

                    ResultSet rs = statement.executeQuery("SELECT user_id FROM pii.users_pii WHERE user_id IN ('"
                            + firstUser + "', '" + secondUser + "')");
                    Set<UUID> visible = new HashSet<>();
                    while (rs.next()) visible.add(UUID.fromString(rs.getString("user_id")));
                    assertEquals(Set.of(firstUser, secondUser), visible);
                }
            } finally {
                connection.rollback();
            }
        }
    }

    private void seedPiiUser(Connection connection, UUID userId, String firstName) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("SET ROLE cms_role_administrator");
            statement.execute("INSERT INTO users (id) VALUES ('" + userId + "')");
            statement.execute("INSERT INTO pii.users_pii (user_id, first_name, last_name) VALUES ('" + userId + "', '"
                    + firstName + "', 'Test')");
        }
    }
}
