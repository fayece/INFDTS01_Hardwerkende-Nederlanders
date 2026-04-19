package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import nl.hardwerkendenederlanders.hrcms.models.dtos.article.AuthorDto;

import java.util.UUID;

public interface ArticleAuthorsService {
    AuthorDto[] findAuthorsForArticle(UUID articleId);
}
