package nl.hardwerkendenederlanders.hrcms.services;

import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleAuthorRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleRepository;
import nl.hardwerkendenederlanders.hrcms.database.mongodb.ProfileRepository;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.PublicationStatus;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.ArticleFullDetailsDto;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.AuthorDto;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
public class ArticleServiceImplTest {

    @Autowired
    private ArticleService articleService;

    @MockitoBean
    private ArticleRepository articleRepository;

    @MockitoBean
    private ArticleAuthorRepository authorRepo;

    @MockitoBean
    private ProfileRepository profileRepository;

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

    @Test
    void findArticlePublished_articleInsertInBetweenGettingArticles_CacheIsResetAndArticleRepoIsCalledTwice() {
        UUID article_id = UUID.randomUUID();
        ArticleFullDetailsDto articleDto = ArticleFullDetailsDto.builder()
                .id(article_id)
                .title("Lof der zotheid")
                .textContent("Dat boekske van Eras den Mus")
                .publicationStatus(PublicationStatus.PUBLISHED)
                .firstAuthor(new AuthorDto("author"))
                .build();
        Article article = Article.builder()
                .id(article_id)
                .title("Lof der zotheid")
                .textContent("Dat boekske van Eras den Mus")
                .publicationStatus(PublicationStatus.PUBLISHED)
                .build();

        when(profileRepository.findById(any()))
                .thenReturn(Optional.of(Profile.builder()
                        .id(UUID.randomUUID().toString())
                        .username("author")
                        .build()));
        when(articleRepository.findArticlePublished(article_id)).thenReturn(articleDto);
        doNothing().when(authorRepo).ensureInsert(any());

        articleService.findArticleFullId(article_id);
        articleService.ensureArticleExists(article, UUID.randomUUID());
        articleService.findArticleFullId(article_id);

        verify(articleRepository, times(2)).findArticlePublished(article_id);
    }
}
