package nl.hardwerkendenederlanders.hrcms.database;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.Neo4jTestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.neo4j.core.Neo4jClient;

@SpringBootTest
@Import({TestcontainersConfiguration.class, Neo4jTestcontainersConfiguration.class})
public class Neo4jCommentRepositoryTest extends CommentRepositoryContractTest {

    private static final UUID ARTICLE_ID = UUID.randomUUID();
    private static final UUID AUTHOR_ID = UUID.randomUUID();
    private static final UUID MEDIA_ID = UUID.randomUUID();

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private Neo4jClient neo4jClient;

    @BeforeEach
    void setUp() {
        clearCommentsTable();
    }

    @Override
    protected CommentRepository getRepository() {
        return commentRepository;
    }

    @Override
    protected UUID getValidArticleId() {
        return ARTICLE_ID;
    }

    @Override
    protected UUID getValidAuthorId() {
        return AUTHOR_ID;
    }

    @Override
    protected UUID getValidMediaId() {
        return MEDIA_ID;
    }

    @Override
    protected void clearCommentsTable() {
        neo4jClient.query("MATCH (c:Comment) DETACH DELETE c").run();
    }
}
