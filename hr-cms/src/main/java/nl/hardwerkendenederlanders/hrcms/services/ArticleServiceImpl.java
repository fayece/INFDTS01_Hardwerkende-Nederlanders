package nl.hardwerkendenederlanders.hrcms.services;

import java.sql.SQLException;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.ArticleRepository;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleService;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository _articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        _articleRepository = articleRepository;
    }

    // ensures the given article is present in the database. If the ID doesn't exist a new article is made. If it does
    // the article is updated
    public void ensureArticleExists(Article article) throws SQLException {
        article = Article.FillOutNullFields(article);
        if (_articleRepository.findById(article.getId()) == null) {
            _articleRepository.insert(article);
        } else {
            _articleRepository.update(article);
        }
    }

    @Override
    public Article findById(UUID id) {
        return _articleRepository.findById(id);
    }

    @Override
    public Article[] findAllPaged(int pageSize, int page) {
        return _articleRepository.findAllPaged(pageSize, page);
    }
}
