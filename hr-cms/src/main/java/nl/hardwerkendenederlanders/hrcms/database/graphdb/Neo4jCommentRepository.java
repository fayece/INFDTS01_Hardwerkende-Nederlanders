package nl.hardwerkendenederlanders.hrcms.database.graphdb;

import jakarta.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.BiFunction;
import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.CommentRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentWithAuthor;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.TypeSystem;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@AllArgsConstructor
public class Neo4jCommentRepository implements CommentRepository {

    private static final String HIDDEN_AUTHOR = "Hidden";
    private static final String DELETED_BODY = "This comment has been deleted";

    private final Neo4jClient neo4jClient;

    private final UserRepository userRepository;
    private final Neo4jUserRepository neo4jUserRepository;

    private record RawCommentResult(
            Comment comment, int replyCount, String firstName, String prefix, String lastName) {}

    @PostConstruct
    public void ensureIndexes() {
        neo4jClient
                .query("CREATE INDEX comment_id IF NOT EXISTS FOR (c:Comment) ON (c.id)")
                .run();
        neo4jClient
                .query("CREATE INDEX comment_article IF NOT EXISTS FOR (c:Comment) ON (c.articleId)")
                .run();
    }

    private final BiFunction<TypeSystem, Record, Comment> commentMapper =
            (_, record) -> mapCommentNode(record.get("c"));

    private final BiFunction<TypeSystem, Record, RawCommentResult> rawResultMapper =
            (_, record) -> new RawCommentResult(
                    mapCommentNode(record.get("c")),
                    record.get("replyCount").asInt(0),
                    record.get("firstName").isNull()
                            ? null
                            : record.get("firstName").asString(),
                    record.get("prefix").isNull() ? null : record.get("prefix").asString(),
                    record.get("lastName").isNull()
                            ? null
                            : record.get("lastName").asString());

    @Override
    @Transactional
    public void insert(Comment comment) {
        neo4jClient
                .query("CREATE (c:Comment $props)")
                .bind(commentToMap(comment))
                .to("props")
                .run();

        if (comment.getParentCommentId() != null) {
            neo4jClient
                    .query(
                            // language=Cypher
                            """
                MATCH (c:Comment {id: $childId}), (parent:Comment {id: $parentId})
                CREATE (c) -[:REPLIED_TO]-> (parent)
                """)
                    .bindAll(Map.of(
                            "childId", comment.getId().toString(),
                            "parentId", comment.getParentCommentId().toString()))
                    .run();
        }

        if (!neo4jUserRepository.existsById(comment.getCreatorId())) {
            userRepository.findById(comment.getCreatorId()).ifPresent(neo4jUserRepository::upsert);
        }

        if (neo4jUserRepository.existsById(comment.getCreatorId())) {
            neo4jClient
                    .query(
                            // language=Cypher
                            """
                MATCH (c:Comment {id: $commentId}), (u:User {id: $userId})
                CREATE (c) -[:AUTHORED_BY]-> (u)
                """)
                    .bindAll(Map.of(
                            "commentId", comment.getId().toString(),
                            "userId", comment.getCreatorId().toString()))
                    .run();
        }
    }

    @Override
    @Transactional
    public CommentWithAuthor insertReturning(Comment comment) {
        insert(comment);
        return findByIdWithAuthor(comment.getId()).orElseThrow();
    }

    @Override
    public Optional<CommentWithAuthor> findByIdWithAuthor(UUID id) {
        return neo4jClient
                .query(
                        // language=Cypher
                        """
                MATCH (c:Comment {id: $id})
                OPTIONAL MATCH (reply:Comment) -[:REPLIED_TO]-> (c)
                OPTIONAL MATCH (c) -[:AUTHORED_BY]-> (u:User)
                RETURN c, count(reply) AS replyCount,
                       u.firstName AS firstName, u.prefix AS prefix, u.lastName AS lastName
                """)
                .bindAll(Map.of("id", id.toString()))
                .fetchAs(RawCommentResult.class)
                .mappedBy(rawResultMapper)
                .one()
                .map(this::toCommentWithAuthor);
    }

    @Override
    public List<CommentWithAuthor> findTopLevelCommentsByArticleIdPaged(UUID articleId, int offset, int limit) {
        if (limit <= 0) throw new IllegalArgumentException("Limit must be greater than 0.");
        if (offset < 0) throw new IllegalArgumentException("Offset must be greater than or equal to 0.");

        Collection<RawCommentResult> records = neo4jClient
                .query(
                        // language=Cypher
                        """
                MATCH (c:Comment {articleId: $articleId})
                WHERE NOT (c) -[:REPLIED_TO]-> ()
                OPTIONAL MATCH (reply:Comment) -[:REPLIED_TO]-> (c)
                OPTIONAL MATCH (c) -[:AUTHORED_BY]-> (u:User)
                RETURN c, count(reply) AS replyCount,
                       u.firstName AS firstName, u.prefix AS prefix, u.lastName AS lastName
                ORDER BY c.createdAt DESC
                SKIP $offset LIMIT $limit
                """)
                .bindAll(Map.of("articleId", articleId.toString(), "offset", offset, "limit", limit))
                .fetchAs(RawCommentResult.class)
                .mappedBy(rawResultMapper)
                .all();

        return records.stream().map(this::toCommentWithAuthor).toList();
    }

    @Override
    public List<CommentWithAuthor> findCommentsByParentId(UUID parentId) {
        Collection<RawCommentResult> records = neo4jClient
                .query(
                        // language=Cypher
                        """
                MATCH (c:Comment) -[:REPLIED_TO]-> (parent:Comment {id: $parentId})
                OPTIONAL MATCH (reply:Comment) -[:REPLIED_TO]-> (c)
                OPTIONAL MATCH (c) -[:AUTHORED_BY]-> (u:User)
                RETURN c, count(reply) AS replyCount,
                       u.firstName AS firstName, u.prefix AS prefix, u.lastName AS lastName
                ORDER BY c.createdAt ASC
                """)
                .bindAll(Map.of("parentId", parentId.toString()))
                .fetchAs(RawCommentResult.class)
                .mappedBy(rawResultMapper)
                .all();

        return records.stream().map(this::toCommentWithAuthor).toList();
    }

    @Override
    public Optional<Comment> findById(UUID id) {
        return neo4jClient
                .query(
                        // language=Cypher
                        """
                MATCH (c:Comment {id: $id})
                RETURN c
                """)
                .bindAll(Map.of("id", id.toString()))
                .fetchAs(Comment.class)
                .mappedBy(commentMapper)
                .one();
    }

    @Override
    public void delete(UUID id) {
        neo4jClient
                .query(
                        // language=Cypher
                        """
                MATCH (c:Comment {id: $id})
                SET c.deletedAt = $deletedAt
                """)
                .bindAll(Map.of(
                        "id", id.toString(),
                        "deletedAt", OffsetDateTime.now().toZonedDateTime()))
                .run();
    }

    @Override
    public int countByArticleId(UUID articleId) {
        return neo4jClient
                .query(
                        // language=Cypher
                        """
                MATCH (c:Comment {articleId: $articleId})
                RETURN count(c) AS total
                """)
                .bindAll(Map.of("articleId", articleId.toString()))
                .fetchAs(Integer.class)
                .mappedBy((_, record) -> record.get("total").asInt(0))
                .one()
                .orElse(0);
    }

    @Override
    public List<Comment> findAllPaged(int page, int limit) {
        if (limit <= 0) throw new IllegalArgumentException("Limit must be greater than 0.");
        if (page <= 0) throw new IllegalArgumentException("Page must be greater than 0.");
        int offset = (page - 1) * limit;

        return new ArrayList<>(neo4jClient
                .query(
                        // language=Cypher
                        """
                MATCH (c:Comment)
                RETURN c
                ORDER BY c.createdAt DESC, c.id DESC
                SKIP $offset LIMIT $limit
                """)
                .bindAll(Map.of("offset", offset, "limit", limit))
                .fetchAs(Comment.class)
                .mappedBy(commentMapper)
                .all());
    }

    private Comment mapCommentNode(Value node) {
        UUID mediaId = node.get("mediaId").isNull()
                ? null
                : UUID.fromString(node.get("mediaId").asString());

        OffsetDateTime deletedAt = node.get("deletedAt").isNull()
                ? null
                : node.get("deletedAt").asZonedDateTime().toOffsetDateTime();

        Comment comment = Comment.builder()
                .id(UUID.fromString(node.get("id").asString()))
                .commentBody(node.get("commentBody").asString())
                .articleId(UUID.fromString(node.get("articleId").asString()))
                .creatorId(UUID.fromString(node.get("creatorId").asString()))
                .mediaId(mediaId)
                .createdAt(node.get("createdAt").asZonedDateTime().toOffsetDateTime())
                .deletedAt(deletedAt)
                .build();

        if (deletedAt != null) {
            comment.setCommentBody(DELETED_BODY);
        }
        return comment;
    }

    private Map<String, Object> commentToMap(Comment comment) {
        Map<String, Object> props = new HashMap<>();
        props.put("id", comment.getId().toString());
        props.put("commentBody", comment.getCommentBody());
        props.put("articleId", comment.getArticleId().toString());
        props.put("creatorId", comment.getCreatorId().toString());
        props.put("createdAt", comment.getCreatedAt().toZonedDateTime());

        if (comment.getMediaId() != null) {
            props.put("mediaId", comment.getMediaId().toString());
        }
        if (comment.getDeletedAt() != null) {
            props.put("deletedAt", comment.getDeletedAt().toZonedDateTime());
        }

        return props;
    }

    private CommentWithAuthor toCommentWithAuthor(RawCommentResult raw) {
        String authorName;
        if (raw.comment().getDeletedAt() != null) {
            authorName = HIDDEN_AUTHOR;
        } else if (raw.firstName() != null && raw.lastName() != null) {
            authorName = raw.prefix() != null
                    ? raw.firstName() + " " + raw.prefix() + " " + raw.lastName()
                    : raw.firstName() + " " + raw.lastName();
        } else {
            authorName = HIDDEN_AUTHOR;
        }
        return new CommentWithAuthor(raw.comment(), authorName, raw.replyCount());
    }
}
