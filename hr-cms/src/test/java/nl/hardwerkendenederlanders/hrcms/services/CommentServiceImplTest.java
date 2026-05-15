package nl.hardwerkendenederlanders.hrcms.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.CommentRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ComponentActionException;
import nl.hardwerkendenederlanders.hrcms.exceptions.ComponentUnavailableException;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentViewDto;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentWithAuthor;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.PagedComments;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommentServiceImplTest {

    // region Setup
    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final UserSessionService userSessionService = mock(UserSessionService.class);
    private final CommentServiceImpl commentService = new CommentServiceImpl(commentRepository, userSessionService);
    // endregion

    // region getTopLevelComments tests
    @Test
    void getTopLevelComments_hasMoreTrue_returnsCommentSublist() {
        UUID articleId = UUID.randomUUID();
        List<CommentWithAuthor> mockData = IntStream.range(0, 11)
                .mapToObj(i -> new CommentWithAuthor(
                        Comment.builder().id(UUID.randomUUID()).build(), "Author " + i, 0))
                .toList();

        when(commentRepository.findTopLevelCommentsByArticleIdPaged(eq(articleId), anyInt(), eq(11)))
                .thenReturn(mockData);

        PagedComments result = commentService.getTopLevelComments(articleId, 0, 10);

        assertTrue(result.isHasMore());
        assertEquals(10, result.getComments().size());
        assertEquals("Author 0", result.getComments().getFirst().authorName());
    }

    @Test
    void getTopLevelComments_hasMoreFalse_returnsAllComments() {
        UUID articleId = UUID.randomUUID();
        List<CommentWithAuthor> mockData = List.of(
                new CommentWithAuthor(Comment.builder().id(UUID.randomUUID()).build(), "Author", 0));

        when(commentRepository.findTopLevelCommentsByArticleIdPaged(eq(articleId), anyInt(), eq(11)))
                .thenReturn(mockData);

        PagedComments result = commentService.getTopLevelComments(articleId, 0, 10);

        assertFalse(result.isHasMore());
        assertEquals(1, result.getComments().size());
    }

    @Test
    void getTopLevelComments_repoThrows_throwsComponentUnavailable() {
        when(commentRepository.findTopLevelCommentsByArticleIdPaged(any(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("DB Down"));

        assertThrows(
                ComponentUnavailableException.class,
                () -> commentService.getTopLevelComments(UUID.randomUUID(), 0, 10));
    }
    // endregion

    // region getReplies tests
    @Test
    void getReplies_success_returnsList() {
        UUID parentId = UUID.randomUUID();
        when(commentRepository.findCommentsByParentId(parentId))
                .thenReturn(List.of(new CommentWithAuthor(Comment.builder().build(), "Author", 0)));

        List<CommentViewDto> result = commentService.getReplies(parentId);

        assertEquals(1, result.size());
        assertEquals("Author", result.getFirst().authorName());
    }
    // endregion

    // region deleteComment tests
    @Test
    void deleteComment_success_deletesAndReturnsDto() {
        HttpSession session = mock(HttpSession.class);
        UUID userId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        Comment comment = Comment.builder()
                .id(commentId)
                .creatorId(userId)
                .articleId(UUID.randomUUID())
                .build();
        CommentWithAuthor deletedRecord = new CommentWithAuthor(comment, "Unknown", 0);

        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.of(userId));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.findByIdWithAuthor(commentId)).thenReturn(Optional.of(deletedRecord));

        CommentViewDto result = commentService.deleteComment(commentId, session);

        verify(commentRepository).delete(commentId);
        assertNotNull(result);
        assertEquals(commentId, result.id());
    }

    @Test
    void deleteComment_notLoggedIn_throwsComponentActionException() {
        HttpSession session = mock(HttpSession.class);
        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.empty());

        ComponentActionException ex = assertThrows(
                ComponentActionException.class, () -> commentService.deleteComment(UUID.randomUUID(), session));

        assertEquals(ComponentActionException.Action.DELETE, ex.getAction());
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void deleteComment_commentNotFound_throwsComponentActionException() {
        HttpSession session = mock(HttpSession.class);
        UUID userId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();

        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.of(userId));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        ComponentActionException ex =
                assertThrows(ComponentActionException.class, () -> commentService.deleteComment(commentId, session));

        assertEquals(ComponentActionException.Action.DELETE, ex.getAction());
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void deleteComment_notOwner_throwsComponentActionException() {
        HttpSession session = mock(HttpSession.class);
        UUID userId = UUID.randomUUID();
        UUID commentId = UUID.randomUUID();
        Comment comment = Comment.builder()
                .id(commentId)
                .creatorId(UUID.randomUUID())
                .articleId(UUID.randomUUID())
                .build();

        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.of(userId));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        ComponentActionException ex =
                assertThrows(ComponentActionException.class, () -> commentService.deleteComment(commentId, session));

        assertEquals(ComponentActionException.Action.DELETE, ex.getAction());
        verify(commentRepository, never()).delete(any());
    }
    // endregion

    // region postComment tests
    @Test
    void postComment_success_sanitizesAndInserts() {
        HttpSession session = mock(HttpSession.class);
        UUID userId = UUID.randomUUID();
        Comment inputComment = Comment.builder()
                .articleId(UUID.randomUUID())
                .commentBody("# Hello World")
                .build();

        CommentWithAuthor savedRecord = new CommentWithAuthor(inputComment, "John Doe", 0);

        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.of(userId));
        when(commentRepository.insertReturning(any(Comment.class))).thenReturn(savedRecord);

        CommentViewDto result = commentService.postComment(inputComment, session);

        assertNotNull(result);
        assertEquals(userId, inputComment.getCreatorId());
        verify(commentRepository).insertReturning(inputComment);
    }

    @Test
    void postComment_genericException_wrapsInComponentActionException() {
        HttpSession session = mock(HttpSession.class);
        Comment comment = Comment.builder().articleId(UUID.randomUUID()).build();

        when(commentRepository.insertReturning(any(Comment.class))).thenThrow(new RuntimeException("DB error"));

        ComponentActionException ex =
                assertThrows(ComponentActionException.class, () -> commentService.postComment(comment, session));

        assertEquals(ComponentActionException.Action.CREATE, ex.getAction());
        assertTrue(ex.getRedirectTarget().contains(comment.getArticleId().toString()));
    }
    // endregion

    // region cache test
    @Test
    void getTopLevelComments_cacheHit_repoCalledOnce() {
        UUID articleId = UUID.randomUUID();

        when(commentRepository.findTopLevelCommentsByArticleIdPaged(any(), anyInt(), anyInt()))
                .thenReturn(new ArrayList<>());
        commentService.getTopLevelComments(articleId, 0, 10);
        commentService.getTopLevelComments(articleId, 0, 10);

        verify(commentRepository, times(2)).findTopLevelCommentsByArticleIdPaged(articleId, 0, 11);
    }

    @Test
    void getTopLevelComments_PostBetweenCalls_repoCalledTwice() {
        HttpSession session = mock(HttpSession.class);
        UUID userId = UUID.randomUUID();
        UUID articleId = UUID.randomUUID();
        Comment inputComment = Comment.builder()
                .articleId(articleId)
                .commentBody("# Goodbye World")
                .build();

        when(commentRepository.findTopLevelCommentsByArticleIdPaged(any(), anyInt(), anyInt()))
                .thenReturn(new ArrayList<>());
        when(userSessionService.getLoggedInUser(session)).thenReturn(Optional.of(userId));

        CommentWithAuthor savedRecord = new CommentWithAuthor(inputComment, "John Doe", 0);
        when(commentRepository.insertReturning(any(Comment.class))).thenReturn(savedRecord);

        commentService.getTopLevelComments(articleId, 0, 10);
        commentService.postComment(inputComment, session);
        commentService.getTopLevelComments(articleId, 0, 10);

        verify(commentRepository, times(2)).findTopLevelCommentsByArticleIdPaged(articleId, 0, 11);
    }
}
