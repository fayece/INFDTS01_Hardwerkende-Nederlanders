package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.Neo4jTestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.graphdb.Neo4jUserRepository;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.neo4j.core.Neo4jClient;

@SpringBootTest
@Import({TestcontainersConfiguration.class, Neo4jTestcontainersConfiguration.class})
class Neo4jUserRepositoryTest {

    @Autowired
    private Neo4jUserRepository neo4jUserRepository;

    @Autowired
    private Neo4jClient neo4jClient;

    @BeforeEach
    void setUp() {
        neo4jClient.query("MATCH (u:User) DETACH DELETE u").run();
    }

    private User testUser() {
        return User.builder()
                .id(UUID.randomUUID())
                .firstName("Test")
                .lastName("User")
                .passwordHash("hash")
                .roleId(null)
                .active(true)
                .createdAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void upsert_shouldCreateUserNode() {
        User user = testUser();

        neo4jUserRepository.upsert(user);

        long count = neo4jClient
                .query("MATCH (u:User {id: $id}) RETURN count(u) AS count")
                .bindAll(Map.of("id", user.getId().toString()))
                .fetchAs(Long.class)
                .mappedBy((_, r) -> r.get("count").asLong())
                .one()
                .orElse(0L);
        assertEquals(1L, count);
    }

    @Test
    void upsert_calledTwice_shouldUpdateAndNotDuplicate() {
        User user = testUser();
        neo4jUserRepository.upsert(user);

        user.setFirstName("TestNew");
        neo4jUserRepository.upsert(user);

        String firstName = neo4jClient
                .query("MATCH (u:User {id: $id}) RETURN u.firstName AS firstName")
                .bindAll(Map.of("id", user.getId().toString()))
                .fetchAs(String.class)
                .mappedBy((_, r) -> r.get("firstName").asString())
                .one()
                .orElse(null);
        assertEquals("TestNew", firstName);

        long count = neo4jClient
                .query("MATCH (u:User {id: $id}) RETURN count(u) AS count")
                .bindAll(Map.of("id", user.getId().toString()))
                .fetchAs(Long.class)
                .mappedBy((_, r) -> r.get("count").asLong())
                .one()
                .orElse(0L);
        assertEquals(1L, count);
    }

    @Test
    void deleteById_shouldRemoveUserNode() {
        User user = testUser();
        neo4jUserRepository.upsert(user);

        neo4jUserRepository.deleteById(user.getId());

        long count = neo4jClient
                .query("MATCH (u:User {id: $id}) RETURN count(u) AS count")
                .bindAll(Map.of("id", user.getId().toString()))
                .fetchAs(Long.class)
                .mappedBy((_, r) -> r.get("count").asLong())
                .one()
                .orElse(0L);
        assertEquals(0L, count);
    }
}
