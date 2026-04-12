package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import jakarta.annotation.Nullable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import nl.hardwerkendenederlanders.hrcms.database.ArticleRepository;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.PublicationStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Slf4j
public class JdbcArticleRepositoryImpl implements ArticleRepository {
    private final SqlDatabaseConnection _dbCon;
    private final NamedParameterJdbcTemplate jdbc;

    public JdbcArticleRepositoryImpl(SqlDatabaseConnection dbCon, NamedParameterJdbcTemplate jdbc) {
        _dbCon = dbCon;
        this.jdbc = jdbc;
    }

    protected RowMapper<Article> rowMapper() {
        return (rs, _) -> new Article(
                rs.getObject("id", UUID.class),
                rs.getString("title"),
                rs.getString("text_content"),
                rs.getObject("created_at", OffsetDateTime.class),
                rs.getObject("updated_at", OffsetDateTime.class),
                PublicationStatus.valueOf(rs.getString("publication_status")),
                rs.getObject("subject_id", UUID.class));
    }

    private MapSqlParameterSource ArticleMapper(Article article){
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

    @Override
    public void Create(Article article) {

        String query = ("""
            INSERT INTO articles (id, title, text_content, created_at, updated_at, publication_status, subject_id)
            VALUES (:id, :title, :text_content, :created_at, :updated_at, :publication_status, :subject_id);
            """);

        jdbc.update(query, ArticleMapper(article));
    }

    @Override
    public void Update(Article article) {
        String query = """
            UPDATE articles
            SET title = :title, text_content = :text_content, updated_at = :updated_at, publication_status = :publication_status, subject_id = :subject_id
            WHERE (id = :id);
            """;
        jdbc.update(query, ArticleMapper(article));
    }

    @Override
    public @Nullable Article GetById(UUID id) {
        String query = """
            SELECT *
            FROM articles
            WHERE id = :id
            LIMIT 1;
            """;
        MapSqlParameterSource mapping = new MapSqlParameterSource();
        mapping.addValue("id", id);
        return jdbc.queryForObject(query, mapping, rowMapper());
    }

    @Override
    public Article[] GetAll() {
        String query = """
            SELECT *
            FROM articles
            """;
        return jdbc.query(query, rowMapper()).toArray(new Article[0]);
    }
}
