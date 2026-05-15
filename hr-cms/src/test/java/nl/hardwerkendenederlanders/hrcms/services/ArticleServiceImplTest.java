package nl.hardwerkendenederlanders.hrcms.services;

import static org.mockito.Mockito.*;

import java.util.List;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleAuthorRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleRepository;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.PublicationStatus;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.ArticleFullDetailsDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ArticleServiceImplTest {

    private final ArticleRepository articleRepository = mock(ArticleRepository.class);

    private final ArticleAuthorRepository authorRepo = mock(ArticleAuthorRepository.class);

    private final ArticleServiceImpl articleService = new ArticleServiceImpl(articleRepository, authorRepo);

    @Test
    void ensureCreated_AddArticleWhenNotExists_ShouldAddArticle() {
        UUID article_id = UUID.randomUUID();
        Article article1 = Article.builder()
                .id(article_id)
                .title("article title")
                .textContent("just some text")
                .build();
        when(articleRepository.findById(article_id)).thenReturn(null);
        doNothing().when(authorRepo).ensureInsert(any());
        articleService.ensureArticleExists(article1, UUID.randomUUID());

        verify(articleRepository, times(1)).insert(any());
    }

    @Test
    void ensureCreated_UpdateArticleWhenExists_ShouldUpdateArticle() {
        UUID article_id = UUID.randomUUID();
        Article article2 = Article.builder()
                .id(article_id)
                .title("More")
                .textContent("Moore's law")
                .build();
        when(articleRepository.findById(article_id)).thenReturn(article2);
        doNothing().when(authorRepo).ensureInsert(any());
        articleService.ensureArticleExists(article2, UUID.randomUUID());

        verify(articleRepository, times(1)).update(any());
    }

    // region cache test

    @Test
    void findArticlePublished_articleInsertInBetweenGettingArticles_CacheIsResetAndArticleRepoIsCalledTwice() {
        UUID article_id = UUID.randomUUID();
        ArticleFullDetailsDto articleDto = ArticleFullDetailsDto.builder()
                .id(article_id)
                .title("Lof der zotheid")
                .textContent("Dat boekske van Eras den Mus")
                .publicationStatus(PublicationStatus.PUBLISHED)
                .build();
        Article article = Article.builder()
                .id(article_id)
                .title("Lof der zotheid")
                .textContent("Dat boekske van Eras den Mus")
                .publicationStatus(PublicationStatus.PUBLISHED)
                .build();
        when(articleRepository.findArticlePublished(article_id)).thenReturn(articleDto);
        articleService.findArticleFullId(article_id);

        articleService.ensureArticleExists(article, UUID.randomUUID());

        articleService.findArticleFullId(article_id);

        verify(articleRepository, times(2)).findArticlePublished(article_id);
    }

    @Test
    void findNewPublished_articleInsertInBetweenGettingAllArticles_CacheIsResetAndArticleRepoIsCalledTwice() {
        UUID article_id = UUID.randomUUID();
        Article article = Article.builder()
                .id(article_id)
                .title("Lof der zotheid")
                .textContent("Dat boekske van Eras den Mus")
                .publicationStatus(PublicationStatus.PUBLISHED)
                .build();
        when(articleRepository.findNewArticlesPublishedPaged(anyInt(), anyInt()))
                .thenReturn(List.of(ArticleFullDetailsDto.builder().build()));

        articleService.findNewPublished(10, 0);
        articleService.ensureArticleExists(article, UUID.randomUUID());

        articleService.findNewPublished(10, 0);

        verify(articleRepository, times(2)).findNewArticlesPublishedPaged(anyInt(), anyInt());
    }

    // endregion
}
