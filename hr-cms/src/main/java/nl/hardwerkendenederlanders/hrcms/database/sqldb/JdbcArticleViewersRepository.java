package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleViewersRepository;
import nl.hardwerkendenederlanders.hrcms.models.ArticleViewer;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class JdbcArticleViewersRepository implements ArticleViewersRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcArticleViewersRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public void ensureInsert(ArticleViewer articleViewer) {
        String sql = """
            INSERT INTO article_viewers (article_id, viewer_id)
            VALUES (:articleId, :viewerId)
            ON CONFLICT (article_id, viewer_id)
            DO NOTHING;
        """;
        MapSqlParameterSource mapping = new MapSqlParameterSource();
        mapping.addValue("articleId", articleViewer.getArticleId());
        mapping.addValue("viewerId", articleViewer.getViewerId());
        jdbc.update(sql, mapping);
    }
}
