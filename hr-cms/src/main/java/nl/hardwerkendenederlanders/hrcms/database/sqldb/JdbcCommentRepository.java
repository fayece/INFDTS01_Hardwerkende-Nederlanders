package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.CommentRepository;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentWithAuthor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class JdbcCommentRepository extends JdbcMutableRepository<Comment> implements CommentRepository {

    public JdbcCommentRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        super(jdbc, resourceLoader, Comment.class);
    }

    @Override
    protected RowMapper<Comment> rowMapper() {
        return (rs, _) -> Comment.builder()
                .id(UUID.fromString(rs.getString("id")))
                .commentBody(rs.getString("comment_body"))
                .creatorId(UUID.fromString(rs.getString("creator_id")))
                .articleId(UUID.fromString(rs.getString("article_id")))
                .mediaId(rs.getString("media_id") != null ? UUID.fromString(rs.getString("media_id")) : null)
                .parentCommentId(
                        rs.getString("parent_comment_id") != null
                                ? UUID.fromString(rs.getString("parent_comment_id"))
                                : null)
                .createdAt(rs.getObject("created_at", OffsetDateTime.class))
                .deletedAt(rs.getObject("deleted_at", OffsetDateTime.class))
                .build();
    }

    private final RowMapper<CommentWithAuthor> commentWithAuthorRowMapper = (rs, rowNum) -> {
        Comment comment = rowMapper().mapRow(rs, rowNum);
        String authorName = rs.getString("name");
        return new CommentWithAuthor(comment, authorName);
    };

    public List<CommentWithAuthor> findTopLevelCommentsByArticleIdPaged(UUID articleId, int page, int limit) {

        if (limit <= 0) throw new IllegalArgumentException("Limit must be greater than 0.");
        if (page <= 0) throw new IllegalArgumentException("Page must be greater than 0.");
        int offset = (page - 1) * limit;

        return jdbc.query(
                "SELECT c.*, " + "CONCAT_WS(' ', u.first_name, NULLIF(TRIM(u.prefix), ''), u.last_name) AS name "
                        + "FROM comments c "
                        + "JOIN users u ON c.creator_id = u.id "
                        + "WHERE c.article_id = :articleId "
                        + "AND c.parent_comment_id IS NULL "
                        + "ORDER BY c.created_at DESC "
                        + "LIMIT :limit OFFSET :offset",
                Map.of("articleId", articleId, "limit", limit, "offset", offset),
                commentWithAuthorRowMapper);
    }

    public List<CommentWithAuthor> findCommentsByParentId(UUID parentId) {
        return jdbc.query(
                "SELECT c.*, " + "CONCAT_WS(' ', u.first_name, NULLIF(TRIM(u.prefix), ''), u.last_name) AS name "
                        + "FROM comments c "
                        + "JOIN users u ON c.creator_id = u.id "
                        + "WHERE c.parent_comment_id = :parentId "
                        + "ORDER BY c.created_at",
                Map.of("parentId", parentId),
                commentWithAuthorRowMapper);
    }
}
