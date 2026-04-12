package nl.hardwerkendenederlanders.hrcms.services;

import nl.hardwerkendenederlanders.hrcms.database.ArticleRepository;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArticleServiceImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Test
    void ensureCreated_AddArticleWhenNotExists_ShouldAddArticle() throws SQLException{
        UUID article_id = UUID.randomUUID();
        Article article1 = Article.builder()
                .id(article_id)
                .title("article title")
                .textContent("just some text")
                .build();
        when(articleRepository.GetById(article_id)).thenReturn(null);
        articleService.EnsureArticleExists(article1);

        verify(articleRepository, times(1)).Create(any());
    }

    @Test
    void ensureCreated_UpdateArticleWhenExists_ShouldUpdateArticle() throws SQLException {
        UUID article_id = UUID.randomUUID();
        Article article2 = Article.builder()
                .id(article_id)
                .title("More")
                .textContent("Moore's law")
                .build();
        when(articleRepository.GetById(article_id)).thenReturn(article2);
        articleService.EnsureArticleExists(article2);

        verify(articleRepository, times(1)).Update(any());
    }
}
