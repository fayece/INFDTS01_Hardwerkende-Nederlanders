package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Article;

public interface ArticleService {
    void ensureArticleExists(Article article) throws Exception;

    Article findById(UUID id);

    Article[] findAllPaged(int pageSize, int page);
}
