package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import nl.hardwerkendenederlanders.hrcms.database.ArticleRepository;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.PublicationStatus;
import org.springframework.jdbc.core.RowMapper;

@Slf4j
public class JdbcArticleRepositoryImpl implements ArticleRepository {
    private final SqlDatabaseConnection _dbCon;

    public JdbcArticleRepositoryImpl(SqlDatabaseConnection dbCon) {
        _dbCon = dbCon;
    }

    protected RowMapper<Article> rowMapper() {
        return (rs, _) -> new Article(
                rs.getObject("id", UUID.class),
                rs.getString("title"),
                rs.getString("text_content"),
                rs.getObject("created_at", OffsetDateTime.class),
                rs.getObject("updated_at", OffsetDateTime.class),
                PublicationStatus.valueOf(rs.getString("publication_status")));
    }


    @Override
    public void Create(Article article) {
        try {
            var con = _dbCon.getPreparedStatement();

            PreparedStatement query = con.prepareStatement("""
                INSERT INTO articles (id, title, text_content, created_at, updated_at, publication_status)
                VALUES (?, ?, ?, ?, ?, ?);
                """);
            query.setObject(1, article.getId());
            query.setString(2, article.getTitle());
            query.setString(3, article.getTextContent());
            query.setObject(4, article.getCreatedAt());
            query.setObject(5, article.getUpdatedAt());
            query.setString(6, article.getPublicationStatus().toString());
            query.execute();
        }
        catch (Exception e){
            log.error("error: ", e);
        }
    }

    @Override
    public void Update(Article article) {
        try {
            var con = _dbCon.getPreparedStatement();

            PreparedStatement query = con.prepareStatement("""
                UPDATE articles
                SET title = ?, text_content = ?, updated_at = ?, publication_status = ?
                WHERE (id = ?);
                """);

            query.setString(1, article.getTitle());
            query.setString(2, article.getTextContent());
            query.setObject(3, article.getUpdatedAt());
            query.setString(4, article.getPublicationStatus().toString());
            query.setObject(5, article.getId());
            query.execute();
        }
        catch (Exception e){
            log.error("error: ", e);
        }
    }

    @Override
    public @Nullable Article GetById(UUID id){
        try {
            var con = _dbCon.getPreparedStatement();

            PreparedStatement query = con.prepareStatement("""
                SELECT *
                FROM articles
                WHERE id = ?
                LIMIT 1;
                """);
            query.setObject(1, id);
            ResultSet queryResults = query.executeQuery();
            if (queryResults.next()){
                return rowMapper().mapRow(queryResults, 1);
            }
        }
        catch (Exception e){
            log.error("error: ", e);
        }
        return null;
    }

    @Override
    public Article[] GetAll(){
        try {
            var con = _dbCon.getPreparedStatement();

            PreparedStatement query = con.prepareStatement("""
                SELECT *
                FROM articles
                """);
            ArrayList<Article> articles = new ArrayList<>();
            ResultSet queryResults = query.executeQuery();
            while (queryResults.next())
                    articles.add(rowMapper().mapRow(queryResults, 1));
            return  articles.toArray(new Article[0]);
        }
        catch (Exception e){
            log.error("error: ", e);
        }
        return null;
    }
}
