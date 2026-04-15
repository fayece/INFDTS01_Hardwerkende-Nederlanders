package nl.hardwerkendenederlanders.hrcms.services;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.ArticleRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ComponentActionException;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.PublicationStatus;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    // ensures the given article is present in the database. If the ID doesn't exist a new article is made. If it does
    // the article is updated
    public void ensureArticleExists(Article article) {
        article = Article.fillOutNullFields(article);
        try {
            if (articleRepository.findById(article.getId()) == null) {
                articleRepository.insert(article);
            } else {
                articleRepository.update(article);
            }
        } catch (DataAccessException dae) {
            if (article.getPublicationStatus() == PublicationStatus.PUBLISHED) {
                throw new ComponentActionException(
                        "article",
                        ComponentActionException.Action.CREATE,
                        "/article/editor?error=INSERT",
                        dae,
                        "database error" + dae.getMessage());
            }
        }
    }

    @Override
    public Article findById(UUID id) {
        return articleRepository.findById(id);
    }

    @Override
    public Article[] findAllPaged(int pageSize, int page) {
        return articleRepository.findAllPaged(pageSize, page);
    }
}
