package nl.hardwerkendenederlanders.hrcms.database;

import jakarta.annotation.Nullable;
import nl.hardwerkendenederlanders.hrcms.models.Article;

import java.util.UUID;

public interface ArticleRepository {

    void Create(Article article);

    void Update(Article article);

    @Nullable Article GetById(UUID id);

    Article[] GetAll();
}
