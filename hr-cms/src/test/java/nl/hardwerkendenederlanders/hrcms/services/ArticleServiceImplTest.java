package nl.hardwerkendenederlanders.hrcms.services;

import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.ArticleRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleAuthorRepository;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.ArticleAuthor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import nl.hardwerkendenederlanders.hrcms.models.ArticleAuthor;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceImplTest {

    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private ArticleAuthorRepository authorRepo;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Test
    void ensureCreated_AddArticleWhenNotExists_ShouldAddArticle() throws SQLException {
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
    void ensureCreated_UpdateArticleWhenExists_ShouldUpdateArticle() throws SQLException {
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
}
