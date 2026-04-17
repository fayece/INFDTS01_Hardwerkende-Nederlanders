package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.ArticleWithSubject;

public interface ArticleService {
    void ensureArticleExists(Article article);

    Article findById(UUID id);

    Article[] findAllPaged(int pageSize, int page);

    Article[] findNewPublished(int pageSize, int page);

    /**
     * Get an article DTO ready for frontend display
     */
    ArticleWithSubject findArticleWithSubjectById(UUID id);
}
