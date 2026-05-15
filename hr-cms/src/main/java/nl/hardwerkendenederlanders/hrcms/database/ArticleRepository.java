package nl.hardwerkendenederlanders.hrcms.database;

import jakarta.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.ArticleFullDetailsDto;

public interface ArticleRepository {

    void insert(Article article);

    void update(Article article);

    @Nullable
    Article findById(UUID id);

    List<ArticleFullDetailsDto> findAllPaged(int limit, int offset);

    List<ArticleFullDetailsDto> findNewArticlesPublishedPaged(int limit, int offset);

    void delete(UUID id);

    ArticleFullDetailsDto findArticlePublished(UUID id);
}
