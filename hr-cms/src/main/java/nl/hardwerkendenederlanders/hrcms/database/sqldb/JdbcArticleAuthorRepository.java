package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleAuthorRepository;
import nl.hardwerkendenederlanders.hrcms.models.ArticleAuthor;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.AuthorDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class JdbcArticleAuthorRepository implements ArticleAuthorRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public JdbcArticleAuthorRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * make a RowMapper that maps SQL rows to Java ArticleAuthors
     * @return The RowMapper
     */
    private RowMapper<ArticleAuthor> articleAuthorMapper() {
        return (rs, _) -> ArticleAuthor.builder()
                .articleId(rs.getObject("article_id", UUID.class))
                .authorId(rs.getObject("author_id", UUID.class))
                .build();
    }

    /**
     * Make a RowMapper that maps SQL rows to Java AuthorDTO's
     * @return The RowMapper
     */
    private RowMapper<AuthorDto> authorNameMapper() {
        return (rs, _) -> new AuthorDto(rs.getString("first_name"), rs.getString("prefix"), rs.getString("last_name"));
    }

    /**
     * Map a article into SQL
     * @param articleAuthor the author
     * @return mapper
     */
    private MapSqlParameterSource ArticleMapper(ArticleAuthor articleAuthor) {
        MapSqlParameterSource mapping = new MapSqlParameterSource();

        mapping.addValue("article_id", articleAuthor.getArticleId());
        mapping.addValue("author_id", articleAuthor.getAuthorId());

        return mapping;
    }

    @Override
    public void ensureInsert(ArticleAuthor articleAuthor) {
        String sql = """
        INSERT INTO article_authors(article_id, author_id)
        VALUES (:article_id, :author_id)
        ON CONFLICT (article_id, author_id)
        DO NOTHING;
        """;
        jdbc.update(sql, ArticleMapper(articleAuthor));
    }

    @Override
    public AuthorDto[] findAuthorsForArticle(UUID articleId) {
        String sql = """
                SELECT *
                FROM article_authors_named
                WHERE article_id = :articleId
                """;
        MapSqlParameterSource mapping = new MapSqlParameterSource();
        mapping.addValue("articleId", articleId);
        return jdbc.query(sql, mapping, authorNameMapper()).toArray(new AuthorDto[0]);
    }
}
