package nl.hardwerkendenederlanders.hrcms.database.cache.interfaces;

import nl.hardwerkendenederlanders.hrcms.models.dtos.article.ArticleFullDetailsDto;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface ArticleCache {
    void insertFullArticle(ArticleFullDetailsDto fullArticle);

    @Nullable ArticleFullDetailsDto findFullArticle(UUID id);

    void IncrementViewForArticle(UUID articleId);

    void IncrementCommentForArticle(UUID articleId);
}
