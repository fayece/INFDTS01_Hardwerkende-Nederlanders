package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.AuthorDto;

public interface ArticleAuthorsService {
    AuthorDto[] findAuthorsForArticle(UUID articleId);
}
