package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.Neo4jTestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.graphdb.Neo4jUserRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.jdbc.JdbcTestUtils;

@SpringBootTest
@Import({TestcontainersConfiguration.class, Neo4jTestcontainersConfiguration.class})
class DualWriteUserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @MockitoSpyBean
    private Neo4jUserRepository neo4jUserRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private Neo4jClient neo4jClient;

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
        neo4jClient.query("MATCH (u:User) DETACH DELETE u").run();
    }

    @Test
    void insert_shouldWriteToSqlAndNeo4j() {
        User user = testUser();

        userRepository.insert(user);

        assertTrue(userRepository.findById(user.getId()).isPresent());
        assertEquals(1L, countNeo4jUser(user.getId()));
    }

    @Test
    void update_shouldSyncNameToNeo4j() {
        User user = testUser();
        userRepository.insert(user);
        user.setFirstName("Updated");

        userRepository.update(user);

        String firstName = neo4jClient
                .query("MATCH (u:User {id: $id}) RETURN u.firstName AS firstName")
                .bindAll(Map.of("id", user.getId().toString()))
                .fetchAs(String.class)
                .mappedBy((_, r) -> r.get("firstName").asString())
                .one()
                .orElse(null);
        assertEquals("Updated", firstName);
    }

    @Test
    void deleteById_shouldRemoveFromSqlAndNeo4j() {
        User user = testUser();
        userRepository.insert(user);

        userRepository.deleteById(user.getId());

        assertTrue(userRepository.findById(user.getId()).isEmpty());
        assertEquals(0L, countNeo4jUser(user.getId()));
    }

    @Test
    void insert_whenNeo4jFails_sqlRowStillCommits() {
        doThrow(new RuntimeException("Neo4j unavailable"))
                .when(neo4jUserRepository)
                .upsert(any());
        User user = testUser();

        assertDoesNotThrow(() -> userRepository.insert(user));

        assertTrue(userRepository.findById(user.getId()).isPresent());
        assertEquals(0L, countNeo4jUser(user.getId()));
    }

    private long countNeo4jUser(UUID id) {
        return neo4jClient
                .query("MATCH (u:User {id: $id}) RETURN count(u) AS count")
                .bindAll(Map.of("id", id.toString()))
                .fetchAs(Long.class)
                .mappedBy((_, r) -> r.get("count").asLong())
                .one()
                .orElse(0L);
    }

    private User testUser() {
        return new User(UUID.randomUUID(), "Test", null, "User", "hash", null, true, OffsetDateTime.now());
    }
}
