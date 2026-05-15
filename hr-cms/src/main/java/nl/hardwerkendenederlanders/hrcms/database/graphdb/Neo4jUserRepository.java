package nl.hardwerkendenederlanders.hrcms.database.graphdb;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@AllArgsConstructor
public class Neo4jUserRepository {

    private final Neo4jClient neo4jClient;

    @PostConstruct
    public void ensureIndexes() {
        neo4jClient
                .query("CREATE INDEX user_id IF NOT EXISTS FOR (u:User) ON (u.id)")
                .run();
    }

    @Transactional
    public void upsert(User user) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", user.getId().toString());
        params.put("firstName", user.getFirstName());
        params.put("prefix", user.getPrefix());
        params.put("lastName", user.getLastName());

        neo4jClient
                .query(
                        // language=Cypher
                        """
                MERGE (u:User {id: $id})
                SET u.firstName = $firstName, u.prefix = $prefix, u.lastName = $lastName
                """)
                .bindAll(params)
                .run();
    }

    public boolean existsById(UUID id) {
        return neo4jClient
                .query("MATCH (u:User {id: $id}) RETURN count(u) > 0 AS exists")
                .bindAll(Map.of("id", id.toString()))
                .fetchAs(Boolean.class)
                .mappedBy((_, r) -> r.get("exists").asBoolean())
                .one()
                .orElse(false);
    }

    @Transactional
    public void deleteById(UUID id) {
        neo4jClient
                .query(
                        // language=Cypher
                        """
                MATCH (u:User {id: $id})
                DETACH DELETE u
                """)
                .bindAll(Map.of("id", id.toString()))
                .run();
    }
}
