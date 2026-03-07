package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.time.OffsetDateTime;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.PublicationStatus;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class ArticleRepository extends JdbcMutableRepository<Article> {

    public ArticleRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        super(jdbc, resourceLoader, Article.class);
    }

    @Override
    protected RowMapper<Article> rowMapper() {
        return (rs, _) -> new Article(
                UUID.fromString(rs.getString("id")),
                rs.getString("title"),
                rs.getString("text_content"),
                rs.getObject("created_at", OffsetDateTime.class),
                rs.getObject("updated_at", OffsetDateTime.class),
                PublicationStatus.valueOf(rs.getString("publication_status")));
    }
}
