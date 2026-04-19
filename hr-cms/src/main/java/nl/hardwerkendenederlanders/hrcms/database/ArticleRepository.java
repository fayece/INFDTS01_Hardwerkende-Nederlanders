package nl.hardwerkendenederlanders.hrcms.database;

import jakarta.annotation.Nullable;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.ArticleWithSubjectAndViewsDto;

public interface ArticleRepository {

    void insert(Article article);

    void update(Article article);

    @Nullable
    Article findById(UUID id);

    Article[] findAllPaged(int limit, int offset);

    Article[] findNewPublished(int limit, int offset);

    void delete(UUID id);

    ArticleWithSubjectAndViewsDto findArticleWithSubject(UUID id);
}
