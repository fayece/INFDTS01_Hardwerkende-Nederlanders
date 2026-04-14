package nl.hardwerkendenederlanders.hrcms.services;

import nl.hardwerkendenederlanders.hrcms.database.sqldb.ArticleRepository;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import org.springframework.stereotype.Service;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public Article getArticleById(java.util.UUID articleId) {
        return articleRepository.findById(articleId).orElse(null);
    }
}
