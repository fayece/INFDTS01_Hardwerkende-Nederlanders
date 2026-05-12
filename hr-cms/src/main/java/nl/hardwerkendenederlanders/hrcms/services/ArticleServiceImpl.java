package nl.hardwerkendenederlanders.hrcms.services;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import nl.hardwerkendenederlanders.hrcms.database.ArticleRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleAuthorRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ComponentActionException;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.ArticleAuthor;
import nl.hardwerkendenederlanders.hrcms.models.PublicationStatus;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.ArticleFullDetailsDto;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleAuthorRepository articleAuthorRepository;

    // ensures the given article is present in the database. If the ID doesn't exist a new article is made. If it does
    // the article is updated
    @CacheEvict("publishedArticlesFull")
    @Transactional
    public void ensureArticleExists(Article article, UUID authorId) {
        article = Article.fillOutNullFields(article);
        try {
            if (articleRepository.findById(article.getId()) == null) {
                articleRepository.insert(article);
            } else {
                article.setUpdatedAt(OffsetDateTime.now());
                articleRepository.update(article);
            }

            ArticleAuthor articleAuthor = ArticleAuthor.builder()
                    .articleId(article.getId())
                    .authorId(authorId)
                    .build();
            articleAuthorRepository.ensureInsert(articleAuthor);

        } catch (DataAccessException dae) {
            if (article.getPublicationStatus() == PublicationStatus.PUBLISHED) {
                throw new ComponentActionException(
                        "article",
                        ComponentActionException.Action.CREATE,
                        "/article/editor?error=INSERT",
                        dae,
                        "database error" + dae.getMessage());
            } else {
                throw dae;
            }
        }
    }

    @Override
    public Article findById(UUID id) {
        return articleRepository.findById(id);
    }

    @Override
    public List<ArticleFullDetailsDto> findAllPaged(int pageSize, int page) {
        return articleRepository.findAllPaged(pageSize, page);
    }

    @Override
    @Cacheable("publishedArticlesFull")
    public List<ArticleFullDetailsDto> findNewPublished(int pageSize, int page) {
        System.out.println("Cache Miss");
        return articleRepository.findNewArticlesPublishedPaged(pageSize, page);
    }

    @Override
    @Cacheable("fullArticle")
    public ArticleFullDetailsDto findArticleFullId(UUID id) {
        System.out.println("Cache Miss");
        return articleRepository.findArticlePublished(id);
    }
}
