package nl.hardwerkendenederlanders.hrcms.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.services.ArticleService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.SubjectService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

public class ArticleEditorControllerTest {
    private final ArticleService articleService = mock(ArticleService.class);
    private final SubjectService subjectService = mock(SubjectService.class);

    private final ArticleEditorController controller = new ArticleEditorController(articleService, subjectService);

    @Test
    void WriteArticle_SaveAsDraft_UserIsRedirectedToPageWithIdOfArticle() {
        // arrange
        UUID id = UUID.randomUUID();

        Article article = Article.builder()
                .id(id)
                .title("article title")
                .textContent("text content")
                .build();
        when(articleService.GetById(any())).thenReturn(article);

        // act
        String redirect = controller.PutArticle(new ConcurrentModel(), article);

        // assert
        assertEquals("redirect:/article-editor/" + id.toString(), redirect);
    }

    @Test
    void LoadArticle_UnknownArticle_Returns404Page() {
        // arrange
        UUID id = UUID.randomUUID();
        when(articleService.GetById(any())).thenReturn(null);

        // act
        String page = controller.GetArticle(new ConcurrentModel(), id);

        // assert
        assertEquals("redirect:/error/404", page);
    }

    @Test
    void LoadArticle_KnownArticle_ReturnsArticle() {
        // arrange
        UUID id = UUID.randomUUID();
        Article article = Article.builder().id(id).build();

        when(articleService.GetById(any())).thenReturn(article);

        // act
        String page = controller.GetArticle(new ConcurrentModel(), id);

        // assert
        assertEquals("pages/article-editor-page", page);
    }

    @Test
    void NewArticle_ReturnsPage() {
        // act
        String page = controller.GetArticle(new ConcurrentModel());
        // assert
        assertEquals("pages/article-editor-page", page);
    }
}
