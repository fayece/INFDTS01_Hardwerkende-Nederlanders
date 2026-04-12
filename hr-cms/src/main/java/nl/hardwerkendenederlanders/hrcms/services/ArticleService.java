package nl.hardwerkendenederlanders.hrcms.services;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Article;

public interface ArticleService {
    void EnsureArticleExists(Article article) throws Exception;

    Article GetById(UUID id);

    Article[] GetAll();
}
