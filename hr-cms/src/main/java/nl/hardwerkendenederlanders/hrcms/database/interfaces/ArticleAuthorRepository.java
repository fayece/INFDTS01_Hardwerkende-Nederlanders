package nl.hardwerkendenederlanders.hrcms.database.interfaces;

import nl.hardwerkendenederlanders.hrcms.models.ArticleAuthor;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.AuthorDto;

import java.util.UUID;

public interface ArticleAuthorRepository {
    /**
     * ensure this article author exitsts in the database. If it already exists nothing happens.
     * @param articleAuthor
     */
    void ensureInsert(ArticleAuthor articleAuthor);

    AuthorDto[] findAuthorsForArticle(UUID articleId);

}
