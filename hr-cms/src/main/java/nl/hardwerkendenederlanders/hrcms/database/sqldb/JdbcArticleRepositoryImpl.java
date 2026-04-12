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

    @Override
    public void Create(Article article) {

        String query = ("""
            INSERT INTO articles (id, title, text_content, created_at, updated_at, publication_status, subject_id)
            VALUES (:id, :title, :text_content, :created_at, :updated_at, :publication_status, :subject_id);
            """);
        MapSqlParameterSource mapping = new MapSqlParameterSource();

        mapping.addValue("id", article.getId());
        mapping.addValue("title", article.getTitle());
        mapping.addValue("text_content", article.getTextContent());
        mapping.addValue("created_at", article.getCreatedAt());
        mapping.addValue("updated_at", article.getUpdatedAt());
        mapping.addValue("publication_status", article.getPublicationStatus().toString());
        mapping.addValue("subject_id", article.getSubjectId());
        jdbc.update(query, mapping);
    }

    @Override
    public void Update(Article article) throws Exception {
        var con = _dbCon.GetConnection();

        PreparedStatement query = con.prepareStatement("""
            UPDATE articles
            SET title = ?, text_content = ?, updated_at = ?, publication_status = ?, subject_id = ?
            WHERE (id = ?);
            """);
        query.setString(1, article.getTitle());
        query.setString(2, article.getTextContent());
        query.setObject(3, article.getUpdatedAt());
        query.setString(4, article.getPublicationStatus().toString());
        query.setObject(5, article.getSubjectId());
        query.setObject(6, article.getId());
        query.execute();
    }

    @Override
    public @Nullable Article GetById(UUID id) {
        try {
            var con = _dbCon.GetConnection();

            PreparedStatement query = con.prepareStatement("""
                SELECT *
                FROM articles
                WHERE id = ?
                LIMIT 1;
                """);
            query.setObject(1, id);
            ResultSet queryResults = query.executeQuery();
            if (queryResults.next()) {
                return rowMapper().mapRow(queryResults, 1);
            }
        } catch (Exception e) {
            log.error("error: ", e);
        }
        return null;
    }

    @Override
    public Article[] GetAll() {
        try {
            var con = _dbCon.GetConnection();

            PreparedStatement query = con.prepareStatement("""
                SELECT *
                FROM articles
                """);
            ArrayList<Article> articles = new ArrayList<>();
            ResultSet queryResults = query.executeQuery();
            while (queryResults.next()) articles.add(rowMapper().mapRow(queryResults, 1));
            return articles.toArray(new Article[0]);
        } catch (Exception e) {
            log.error("error: ", e);
        }
        return null;
    }
}
