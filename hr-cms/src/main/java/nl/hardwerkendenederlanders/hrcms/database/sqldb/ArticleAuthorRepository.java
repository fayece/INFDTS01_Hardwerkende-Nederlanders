package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.ArticleAuthor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class ArticleAuthorRepository extends JdbcRepository<ArticleAuthor> {

    public ArticleAuthorRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        super(jdbc, resourceLoader, ArticleAuthor.class);
    }

    // RowMapper ensures we can use `final` fields. Added to every repository class,
    // as we use `final` on id, to ensure immutability of the id.
    @Override
    protected RowMapper<ArticleAuthor> rowMapper() {
        return (rs, _) -> new ArticleAuthor(
                UUID.fromString(rs.getString("id")),
                UUID.fromString(rs.getString("article_id")),
                UUID.fromString(rs.getString("author_id")));
    }
}
