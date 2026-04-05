package nl.hardwerkendenederlanders.hrcms.services;

import nl.hardwerkendenederlanders.hrcms.models.Article;

import java.util.UUID;

public interface ArticleService {
    void EnsureArticleExists(Article article);
    Article GetById(UUID id);
}
