package nl.hardwerkendenederlanders.hrcms.database;

import jakarta.annotation.Nullable;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Article;

public interface ArticleRepository {

    void Create(Article article) throws Exception;

    void Update(Article article) throws Exception;

    @Nullable
    Article GetById(UUID id);

    Article[] GetAll();
}
