package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import jakarta.annotation.Nullable;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleRepository;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.PublicationStatus;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.ArticleFullDetailsDto;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.AuthorDto;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class JdbcArticleRepository implements ArticleRepository {
    private final NamedParameterJdbcTemplate jdbc;

    public JdbcArticleRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    protected RowMapper<Article> rowMapper() {
        return (rs, _) -> Article.builder()
                .id(rs.getObject("id", UUID.class))
                .title(rs.getString("title"))
                .textContent(rs.getString("text_content"))
                .createdAt(rs.getObject("created_at", OffsetDateTime.class))
                .updatedAt(rs.getObject("updated_at", OffsetDateTime.class))
                .publicationStatus(PublicationStatus.valueOf(rs.getString("publication_status")))
                .subjectId(rs.getObject("subject_id", UUID.class))
                .build();
    }

    private MapSqlParameterSource ArticleMapper(Article article) {
        MapSqlParameterSource mapping = new MapSqlParameterSource();

        mapping.addValue("id", article.getId());
        mapping.addValue("title", article.getTitle());
        mapping.addValue("text_content", article.getTextContent());
        mapping.addValue("created_at", article.getCreatedAt());
        mapping.addValue("updated_at", article.getUpdatedAt());
        mapping.addValue("publication_status", article.getPublicationStatus().toString());
        mapping.addValue("subject_id", article.getSubjectId());

        return mapping;
    }

    private RowMapper<ArticleFullDetailsDto> fullArticleMapper() {
        return (rs, rowNum) -> ArticleFullDetailsDto.builder()
                .id(rs.getObject("article_id", UUID.class))
                .title(rs.getString("title"))
                .textContent(rs.getString("text_content"))
                .updatedAt(rs.getObject("updated_at", OffsetDateTime.class))
                .createdAt(rs.getObject("created_at", OffsetDateTime.class))
                .publicationStatus(PublicationStatus.valueOf(rs.getString("publication_status")))
                .subjectName(rs.getString("subject_name"))
                .viewCount(rs.getInt("view_count"))
                .commentCount(rs.getInt("comment_count"))
                .firstAuthor(new AuthorDto(rs.getString("first_author_id")))
                .build();
    }

    @Override
    public void insert(Article article) {

        String query = ("""

                INSERT INTO articles (id, title, text_content, created_at, updated_at, publication_status, subject_id)
            VALUES (:id, :title, :text_content, :created_at, :updated_at, :publication_status, :subject_id);
            """);
        jdbc.update(query, ArticleMapper(article));
    }

    @Override
    public void update(Article article) {
        String query = """
            UPDATE articles
            SET title = :title, text_content = :text_content, updated_at = :updated_at, publication_status = :publication_status, subject_id = :subject_id
            WHERE (id = :id);
            """;
        jdbc.update(query, ArticleMapper(article));
    }

    @Override
    public @Nullable Article findById(UUID id) {
        String query = """
            SELECT *
            FROM articles
            WHERE id = :id
            LIMIT 1;
            """;
        MapSqlParameterSource mapping = new MapSqlParameterSource();
        mapping.addValue("id", id);
        try {
            return jdbc.queryForObject(query, mapping, rowMapper());
        } catch (EmptyResultDataAccessException erdae) {
            return null;
        }
    }

    @Override
    public List<ArticleFullDetailsDto> findAllPaged(int limit, int offset) {
        if (limit <= 0)
            throw new IllegalArgumentException(
                    "findAllPages was called with an limit of " + limit + " the minimum is 1");
        if (offset <= 0)
            throw new IllegalArgumentException(
                    "findAllPages was called with a offset of " + offset + " the minimum is 1");

        String query = """
         SELECT * FROM full_articles
         ORDER BY created_at DESC
         LIMIT :limit
         OFFSET :offset;
         """;
        MapSqlParameterSource mapping = new MapSqlParameterSource();
        mapping.addValue("limit", limit);
        mapping.addValue("offset", (offset - 1) * limit);

        return jdbc.query(query, mapping, fullArticleMapper());
    }

    @Override
    public List<ArticleFullDetailsDto> findNewArticlesPublishedPaged(int limit, int offset) {
        if (limit <= 0)
            throw new IllegalArgumentException(
                    "findAllPages was called with an limit of " + limit + " the minimum is 1");
        if (offset <= 0)
            throw new IllegalArgumentException(
                    "findAllPages was called with a offset of " + offset + " the minimum is 1");

        String query = """
         SELECT *
         FROM full_articles
         WHERE publication_status = 'PUBLISHED'
         ORDER BY created_at DESC
         LIMIT :limit
         OFFSET :offset;
         """;
        MapSqlParameterSource mapping = new MapSqlParameterSource();
        mapping.addValue("limit", limit);
        mapping.addValue("offset", (offset - 1) * limit);
        return jdbc.query(query, mapping, fullArticleMapper());
    }

    @Override
    public void delete(UUID id) {
        String query = """
                DELETE
                FROM articles
                WHERE id = :id
                """;
        jdbc.update(query, Map.of("id", id));
    }

    public @Nullable ArticleFullDetailsDto findArticlePublished(UUID id) {
        String query = """
                SELECT *
                FROM full_articles
                WHERE article_id = :id AND publication_status = 'PUBLISHED'
                LIMIT 1;
                """;
        // here.
        MapSqlParameterSource mapping = new MapSqlParameterSource();
        mapping.addValue("id", id);
        try {
            return jdbc.queryForObject(query, mapping, fullArticleMapper());
        } catch (EmptyResultDataAccessException erdae) {
            return null;
        }
    }
}
