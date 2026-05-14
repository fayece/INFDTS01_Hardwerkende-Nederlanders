package nl.hardwerkendenederlanders.hrcms.database.graphdb;

import jakarta.annotation.PostConstruct;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.CommentRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentWithAuthor;
import nl.hardwerkendenederlanders.hrcms.models.dtos.commonalities.FullName;
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

    private record RawCommentResult(Comment comment, int replyCount) {}

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
                    mapCommentNode(record.get("c")), record.get("replyCount").asInt(0));

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
                CREATE (c)-[:REPLIED_TO]->(parent)
                """)
                    .bindAll(Map.of(
                            "childId", comment.getId().toString(),
                            "parentId", comment.getParentCommentId().toString()))
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
        Optional<RawCommentResult> result = neo4jClient
                .query(
                        // language=Cypher
                        """
                MATCH (c:Comment {id: $id})
                OPTIONAL MATCH (reply:Comment)-[:REPLIED_TO]->(c)
                RETURN c, count(reply) AS replyCount
                """)
                .bindAll(Map.of("id", id.toString()))
                .fetchAs(RawCommentResult.class)
                .mappedBy(rawResultMapper)
                .one();

        return result.map(raw -> {
            String authorName = raw.comment().getDeletedAt() != null
                    ? HIDDEN_AUTHOR
                    : userRepository
                            .findById(raw.comment().getCreatorId())
                            .map(FullName::getFullName)
                            .orElse(HIDDEN_AUTHOR);
            return new CommentWithAuthor(raw.comment(), authorName, raw.replyCount());
        });
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
                RETURN c, count(reply) AS replyCount
                ORDER BY c.createdAt DESC
                SKIP $offset LIMIT $limit
                """)
                .bindAll(Map.of("articleId", articleId.toString(), "offset", offset, "limit", limit))
                .fetchAs(RawCommentResult.class)
                .mappedBy(rawResultMapper)
                .all();

        return mapWithAuthorsBatch(records);
    }

    @Override
    public List<CommentWithAuthor> findCommentsByParentId(UUID parentId) {
        Collection<RawCommentResult> records = neo4jClient
                .query(
                        // language=Cypher
                        """
                MATCH (c:Comment)-[:REPLIED_TO]->(parent:Comment {id: $parentId})
                OPTIONAL MATCH (reply:Comment)-[:REPLIED_TO]->(c)
                RETURN c, count(reply) AS replyCount
                ORDER BY c.createdAt ASC
                """)
                .bindAll(Map.of("parentId", parentId.toString()))
                .fetchAs(RawCommentResult.class)
                .mappedBy(rawResultMapper)
                .all();

        return mapWithAuthorsBatch(records);
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

    private List<CommentWithAuthor> mapWithAuthorsBatch(Collection<RawCommentResult> rawResults) {
        if (rawResults.isEmpty()) return List.of();

        Set<UUID> creatorIds =
                rawResults.stream().map(c -> c.comment().getCreatorId()).collect(Collectors.toSet());

        Map<UUID, String> authors = userRepository.findNamesByUserIds(creatorIds);

        return rawResults.stream()
            .map(c -> {
                String authorName = authors.get(c.comment().getCreatorId());
                String finalName = (c.comment().getDeletedAt() != null || authorName == null)
                    ? HIDDEN_AUTHOR
                    : authorName;

                return new CommentWithAuthor(c.comment(), finalName, c.replyCount());
            })
            .toList();
    }
}
