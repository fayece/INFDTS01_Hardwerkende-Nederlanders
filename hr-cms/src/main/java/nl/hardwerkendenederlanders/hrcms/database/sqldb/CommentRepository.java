package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.time.OffsetDateTime;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class CommentRepository extends JdbcMutableRepository<Comment> {

    public CommentRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        super(jdbc, resourceLoader, Comment.class);
    }

    @Override
    protected RowMapper<Comment> rowMapper() {
        return (rs, _) -> new Comment(
                UUID.fromString(rs.getString("id")),
                rs.getString("comment_body"),
                UUID.fromString(rs.getString("creator_id")),
                UUID.fromString(rs.getString("article_id")),
                rs.getString("media_id") != null ? UUID.fromString(rs.getString("media_id")) : null,
                rs.getString("parent_comment_id") != null ? UUID.fromString(rs.getString("parent_comment_id")) : null,
                rs.getObject("created_at", OffsetDateTime.class),
                rs.getObject("deleted_at", OffsetDateTime.class));
    }
}
