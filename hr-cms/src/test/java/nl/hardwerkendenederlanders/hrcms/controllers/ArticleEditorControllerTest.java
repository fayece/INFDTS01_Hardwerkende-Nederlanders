package nl.hardwerkendenederlanders.hrcms.controllers;

import nl.hardwerkendenederlanders.hrcms.services.ArticleService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.SubjectService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;

public class ArticleEditorControllerTest {
    private  final ArticleService articleService = mock(ArticleService.class);
    private final SubjectService subjectService = mock(SubjectService.class);

    private final ArticleEditorController controller = new ArticleEditorController(articleService, subjectService);

    @Test
    void WriteArticle_SaveAsDraft_UserIsRedirectedToPageWithIdOfArticle(){
        controller.PutDraftArticle();
    }
}
