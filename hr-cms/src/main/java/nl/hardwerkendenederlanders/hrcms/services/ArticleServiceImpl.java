package nl.hardwerkendenederlanders.hrcms.services;

import java.sql.SQLException;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.ArticleRepository;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import org.springframework.stereotype.Service;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository _articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        _articleRepository = articleRepository;
    }

    // ensures the given article is present in the database. If the ID doesn't exist a new article is made. If it does
    // the article is updated
    public void EnsureArticleExists(Article article) throws SQLException {
        article = Article.FillOutNullFields(article);
        if (_articleRepository.GetById(article.getId()) == null) {
            _articleRepository.Create(article);
        } else {
            _articleRepository.Update(article);
        }
    }

    public Article GetById(UUID id) {
        return _articleRepository.GetById(id);
    }

    public Article[] GetAll() {
        return _articleRepository.GetAll();
    }
}
