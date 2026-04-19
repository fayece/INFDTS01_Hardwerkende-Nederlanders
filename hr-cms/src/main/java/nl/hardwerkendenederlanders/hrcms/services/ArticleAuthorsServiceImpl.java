package nl.hardwerkendenederlanders.hrcms.services;

import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleAuthorRepository;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.AuthorDto;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleAuthorsService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ArticleAuthorsServiceImpl implements ArticleAuthorsService {
    private final ArticleAuthorRepository authorRepository;

    public ArticleAuthorsServiceImpl(ArticleAuthorRepository articleAuthorRepository){
        this.authorRepository = articleAuthorRepository;
    }

    public AuthorDto[] findAuthorsForArticle(UUID articleId){
        return authorRepository.findAuthorsForArticle(articleId);
    }
}
