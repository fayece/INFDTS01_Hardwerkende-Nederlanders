package nl.hardwerkendenederlanders.hrcms.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import jakarta.servlet.http.HttpSession;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.SubjectService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

public class ArticleEditorControllerTest {
    private final ArticleService articleService = mock(ArticleService.class);
    private final SubjectService subjectService = mock(SubjectService.class);
    private final UserSessionService userSessionService = mock(UserSessionService.class);

    private final EditorArticleController controller = new EditorArticleController(articleService, subjectService, userSessionService);

    @Test
    void WriteArticle_SaveAsDraft_UserIsRedirectedToPageWithIdOfArticle() {
        // arrange
        UUID id = UUID.randomUUID();

        Article article = Article.builder()
                .id(id)
                .title("article title")
                .textContent("text content")
                .build();
        when(articleService.findById(any())).thenReturn(article);
        when(userSessionService.getLoggedInUser(any())).thenReturn(UUID.randomUUID());

        // act
        String redirect = controller.putArticle(new ConcurrentModel(), article, mock(HttpSession.class));

        // assert
        assertEquals("redirect:/article/editor/" + id.toString(), redirect);
    }

    @Test
    void LoadArticle_UnknownArticle_Returns404Page() {
        // arrange
        UUID id = UUID.randomUUID();
        when(articleService.findById(any())).thenReturn(null);

        // act
        String page = controller.getArticle(new ConcurrentModel(), id);

        // assert
        assertEquals("redirect:/error/404", page);
    }

    @Test
    void LoadArticle_KnownArticle_ReturnsArticle() {
        // arrange
        UUID id = UUID.randomUUID();
        Article article = Article.builder().id(id).build();

        when(articleService.findById(any())).thenReturn(article);

        // act
        String page = controller.getArticle(new ConcurrentModel(), id);

        // assert
        assertEquals("pages/article-editor-page", page);
    }

    @Test
    void NewArticle_ReturnsPage() {
        // act
        String page = controller.getArticle(new ConcurrentModel());
        // assert
        assertEquals("pages/article-editor-page", page);
    }
}
