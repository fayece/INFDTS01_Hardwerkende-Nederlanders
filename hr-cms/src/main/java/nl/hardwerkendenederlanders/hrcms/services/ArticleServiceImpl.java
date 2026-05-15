package nl.hardwerkendenederlanders.hrcms.services;

import java.time.OffsetDateTime;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleAuthorRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.CommentRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ComponentActionException;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.ArticleAuthor;
import nl.hardwerkendenederlanders.hrcms.models.PublicationStatus;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.ArticleFullDetailsDto;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArticleServiceImpl implements ArticleService {
    private final ArticleRepository articleRepository;
    private final ArticleAuthorRepository articleAuthorRepository;
    private final CommentRepository commentRepository;

    public ArticleServiceImpl(
            ArticleRepository articleRepository,
            ArticleAuthorRepository articleAuthorRepository,
            CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.articleAuthorRepository = articleAuthorRepository;
        this.commentRepository = commentRepository;
    }

    // ensures the given article is present in the database. If the ID doesn't exist a new article is made. If it does
    // the article is updated
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
    public ArticleFullDetailsDto[] findAllPaged(int pageSize, int page) {
        ArticleFullDetailsDto[] articles = articleRepository.findAllPaged(pageSize, page);
        for (ArticleFullDetailsDto article : articles)
            article.setCommentCount(commentRepository.countByArticleId(article.getId()));

        return articles;
    }

    @Override
    public ArticleFullDetailsDto[] findNewPublished(int pageSize, int page) {
        ArticleFullDetailsDto[] articles = articleRepository.findNewArticlesPublishedPaged(pageSize, page);
        for (ArticleFullDetailsDto article : articles)
            article.setCommentCount(commentRepository.countByArticleId(article.getId()));

        return articles;
    }

    @Override
    public ArticleFullDetailsDto findArticleFullId(UUID id) {
        ArticleFullDetailsDto article = articleRepository.findArticlePublished(id);
        if (article != null) article.setCommentCount(commentRepository.countByArticleId(article.getId()));

        return article;
    }
}
