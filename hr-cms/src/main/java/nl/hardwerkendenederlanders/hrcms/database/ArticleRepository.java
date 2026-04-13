package nl.hardwerkendenederlanders.hrcms.database;

import jakarta.annotation.Nullable;
import java.sql.SQLException;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Article;

public interface ArticleRepository {

    void Create(Article article) throws SQLException;

    void Update(Article article) throws SQLException;

    @Nullable
    Article GetById(UUID id);

    Article[] GetAll();
}
