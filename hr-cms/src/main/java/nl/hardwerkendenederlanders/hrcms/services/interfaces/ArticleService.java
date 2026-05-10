package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.ArticleFullDetailsDto;

public interface ArticleService {
    void ensureArticleExists(Article article, UUID articleId);

    Article findById(UUID id);

    ArticleFullDetailsDto[] findAllPaged(int pageSize, int page);

    ArticleFullDetailsDto[] findNewPublished(int pageSize, int page);

    /**
     * Get an article DTO ready for frontend display
     */
    ArticleFullDetailsDto findArticleFullId(UUID id);
}
