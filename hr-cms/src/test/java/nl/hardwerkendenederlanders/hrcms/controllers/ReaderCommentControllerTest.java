package nl.hardwerkendenederlanders.hrcms.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentCreateDto;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentViewDto;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.PagedComments;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

class ReaderCommentControllerTest {

    private final CommentService commentService = mock(CommentService.class);
    private final ReaderCommentController commentController = new ReaderCommentController(commentService);

    private UUID articleId;
    private Model model;
    private HttpSession session;

    @BeforeEach
    void setUp() {
        articleId = UUID.randomUUID();
        model = new ConcurrentModel();
        session = mock(HttpSession.class);
    }

    @Test
    void getCommentsByArticle_returnsCommentSectionFragment_withCorrectModel() {
        int initialOffset = 10;
        int limit = 10;

        List<CommentViewDto> commentList = List.of(mock(CommentViewDto.class), mock(CommentViewDto.class));
        PagedComments result = new PagedComments(commentList, true);

        when(commentService.getTopLevelComments(articleId, initialOffset, limit))
                .thenReturn(result);

        String viewName = commentController.getCommentsByArticle(articleId, initialOffset, model);

        assertEquals("fragments/articles/comment-section :: comment-section", viewName);
        assertEquals(commentList, model.getAttribute("comments"));
        assertEquals(true, model.getAttribute("hasMore"));
        assertEquals(commentList.size() + initialOffset, model.getAttribute("offset"));
        assertEquals(articleId, model.getAttribute("articleId"));
    }

    @Test
    void getReplies_returnsCommentListFragment_withReplies() {
        // Arrange
        UUID parentId = UUID.randomUUID(); // parentId is unique to this test, so we keep it local
        List<CommentViewDto> replies = List.of(mock(CommentViewDto.class));

        when(commentService.getReplies(parentId)).thenReturn(replies);

        // Act
        String viewName = commentController.getReplies(parentId, model);

        // Assert
        assertEquals("fragments/articles/comment-section :: comment-list", viewName);
        assertEquals(replies, model.getAttribute("comments"));
    }

    @Test
    void postComment_success_returnsCommentFragment() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        CommentCreateDto formDto = mock(CommentCreateDto.class);
        Comment commentEntity = mock(Comment.class);
        CommentViewDto savedCommentDto = mock(CommentViewDto.class);

        when(bindingResult.hasErrors()).thenReturn(false);
        when(formDto.toComment(articleId)).thenReturn(commentEntity);
        when(commentService.postComment(commentEntity, session)).thenReturn(savedCommentDto);

        // Act
        String viewName = commentController.postComment(articleId, formDto, bindingResult, model, session);

        // Assert
        assertEquals("fragments/articles/comment :: comment", viewName);
        assertEquals(savedCommentDto, model.getAttribute("comment"));
        verify(commentService).postComment(commentEntity, session);
    }

    @Test
    void deleteComment_success_returnsCommentFragment() {
        UUID commentId = UUID.randomUUID();
        CommentViewDto dto = mock(CommentViewDto.class);
        when(commentService.deleteComment(commentId, session)).thenReturn(dto);

        String viewName = commentController.deleteComment(commentId, session, model);

        assertEquals("fragments/articles/comment :: comment", viewName);
        assertEquals(dto, model.getAttribute("comment"));
        verify(commentService).deleteComment(commentId, session);
    }

    @Test
    void postComment_validationError_returnsCommentSectionFragmentWithError() {
        // Arrange
        BindingResult bindingResult = mock(BindingResult.class);
        CommentCreateDto formDto = mock(CommentCreateDto.class);

        String expectedErrorMessage = "Comment cannot be empty";
        FieldError fieldError = new FieldError("formDto", "commentBody", expectedErrorMessage);

        when(bindingResult.hasErrors()).thenReturn(true);
        when(bindingResult.getFieldError("commentBody")).thenReturn(fieldError);

        // Act
        String viewName = commentController.postComment(articleId, formDto, bindingResult, model, session);

        assertEquals("fragments/articles/comment-section :: comment-section", viewName);
        assertEquals(expectedErrorMessage, model.getAttribute("errorMessage"));
        assertEquals(articleId, model.getAttribute("articleId"));

        verify(commentService, never()).postComment(any(), any());
    }
}
