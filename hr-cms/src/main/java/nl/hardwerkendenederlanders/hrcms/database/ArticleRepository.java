package nl.hardwerkendenederlanders.hrcms.database;

import jakarta.annotation.Nullable;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.ArticleFullDetailsDto;

public interface ArticleRepository {

    void insert(Article article);

    void update(Article article);

    @Nullable
    Article findById(UUID id);

    ArticleFullDetailsDto[] findAllPaged(int limit, int offset);

    ArticleFullDetailsDto[] findNewPublished(int limit, int offset);

    void delete(UUID id);

    ArticleFullDetailsDto findArticleWithSubject(UUID id);
}
