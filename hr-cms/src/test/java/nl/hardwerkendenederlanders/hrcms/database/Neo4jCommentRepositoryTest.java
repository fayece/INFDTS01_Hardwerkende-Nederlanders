package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.Neo4jTestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.graphdb.Neo4jUserRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.CommentRepository;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcUserRepository;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentWithAuthor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

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

    @Autowired
    private Neo4jUserRepository neo4jUserRepository;

    @Autowired
    private JdbcUserRepository jdbcUserRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        clearCommentsTable();
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "users");
        neo4jClient.query("MATCH (u:User) DETACH DELETE u").run();
        neo4jUserRepository.upsert(
                new User(AUTHOR_ID, "Test", null, "User", "hash", null, true, OffsetDateTime.now()));
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

    @Test
    void findByIdWithAuthor_withLinkedUser_returnsActualAuthorName() {
        Comment comment = Comment.builder()
                .articleId(ARTICLE_ID)
                .creatorId(AUTHOR_ID)
                .commentBody("Comment with known author")
                .build();
        commentRepository.insert(comment);

        CommentWithAuthor result =
                commentRepository.findByIdWithAuthor(comment.getId()).orElse(null);

        assertNotNull(result);
        assertEquals("Test User", result.authorName());
    }

    @Test
    void insert_whenUserNodeMissing_syncsFromSqlAndResolvesAuthorName() {
        UUID userId = UUID.randomUUID();
        jdbcUserRepository.insert(new User(userId, "No", null, "User", "hash", null, true, OffsetDateTime.now()));

        Comment comment = Comment.builder()
                .articleId(ARTICLE_ID)
                .creatorId(userId)
                .commentBody("Comment where user node was missing")
                .build();
        commentRepository.insert(comment);

        CommentWithAuthor result =
                commentRepository.findByIdWithAuthor(comment.getId()).orElse(null);

        assertNotNull(result);
        assertEquals("No User", result.authorName());
    }

    @Test
    void findByIdWithAuthor_withNoLinkedUser_returnsHidden() {
        UUID unknownAuthorId = UUID.randomUUID();
        Comment comment = Comment.builder()
                .articleId(ARTICLE_ID)
                .creatorId(unknownAuthorId)
                .commentBody("Orphaned comment")
                .build();
        commentRepository.insert(comment);

        CommentWithAuthor result =
                commentRepository.findByIdWithAuthor(comment.getId()).orElse(null);

        assertNotNull(result);
        assertEquals("Hidden", result.authorName());
    }
}
