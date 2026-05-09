package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import nl.hardwerkendenederlanders.hrcms.database.interfaces.CommentRepository;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentWithAuthor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public abstract class CommentRepositoryContractTest {

    protected abstract CommentRepository getRepository();

    protected abstract UUID getValidArticleId();

    protected abstract UUID getValidAuthorId();

    protected abstract UUID getValidMediaId();

    protected abstract void clearCommentsTable();

    @Test
    void insert_withValidComment_shouldPersistAndRetrieve() {
        Comment comment = Comment.builder()
                .articleId(getValidArticleId())
                .creatorId(getValidAuthorId())
                .commentBody("This is a test comment.")
                .mediaId(getValidMediaId())
                .build();

        getRepository().insert(comment);
        Comment retrieved = getRepository().findById(comment.getId()).orElse(null);

        assertNotNull(retrieved);
        assertEquals(comment.getId(), retrieved.getId());
        assertEquals(getValidArticleId(), retrieved.getArticleId());
        assertEquals("This is a test comment.", retrieved.getCommentBody());
    }

    @Test
    void update_withModifiedFields_shouldReflectChanges() {
        Comment comment = Comment.builder()
                .articleId(getValidArticleId())
                .creatorId(getValidAuthorId())
                .commentBody("This is a test comment.")
                .build();
        getRepository().insert(comment);

        comment.setCommentBody("This is an updated test comment.");
        getRepository().update(comment);

        Comment retrieved = getRepository().findById(comment.getId()).orElse(null);
        assertNotNull(retrieved);
        assertEquals("This is an updated test comment.", retrieved.getCommentBody());
    }

    @Test
    void delete_withExistingId_shouldSoftDeleteComment() {
        Comment comment = Comment.builder()
                .articleId(getValidArticleId())
                .creatorId(getValidAuthorId())
                .commentBody("This is a test comment.")
                .build();
        getRepository().insert(comment);

        getRepository().delete(comment.getId());

        Comment retrieved = getRepository().findById(comment.getId()).orElse(null);
        assertNotNull(retrieved);
        assertEquals(comment.getId(), retrieved.getId());
        assertNotNull(retrieved.getDeletedAt());
    }

    @Test
    void findAllPaged_withMultipleComments_shouldReturnPagedResults() {
        for (int i = 0; i < 15; i++) {
            getRepository()
                    .insert(Comment.builder()
                            .articleId(getValidArticleId())
                            .creatorId(getValidAuthorId())
                            .commentBody("Comment " + i)
                            .build());
        }

        List<Comment> firstPage = getRepository().findAllPaged(1, 10);
        List<Comment> secondPage = getRepository().findAllPaged(2, 10);

        assertEquals(10, firstPage.size());
        assertEquals(5, secondPage.size());
    }

    static Stream<Arguments> invalidPaginationData() {
        return Stream.of(Arguments.of(0, 0), Arguments.of(1, 0), Arguments.of(1, -1), Arguments.of(0, -1));
    }

    @ParameterizedTest
    @MethodSource("invalidPaginationData")
    void findAllPaged_withInvalidLimit_shouldThrowException(int page, int size) {
        assertThrows(RuntimeException.class, () -> getRepository().findAllPaged(page, size));
    }

    @Test
    void insertReturning_shouldReturnCommentWithAuthorDetails() {
        Comment comment = Comment.builder()
                .articleId(getValidArticleId())
                .creatorId(getValidAuthorId())
                .commentBody("Comment requiring author DTO")
                .build();

        CommentWithAuthor result = getRepository().insertReturning(comment);

        assertNotNull(result);
        assertEquals(comment.getId(), result.comment().getId());
    }

    @Test
    void findTopLevelCommentsByArticleIdPaged_shouldOnlyReturnCommentsWithoutParents() {
        UUID articleId = getValidArticleId();

        Comment topLevel = Comment.builder()
                .articleId(articleId)
                .creatorId(getValidAuthorId())
                .commentBody("Top level")
                .build();
        getRepository().insert(topLevel);

        Comment reply = Comment.builder()
                .articleId(articleId)
                .creatorId(getValidAuthorId())
                .commentBody("Reply to top level")
                .parentCommentId(topLevel.getId())
                .build();
        getRepository().insert(reply);

        List<CommentWithAuthor> results = getRepository().findTopLevelCommentsByArticleIdPaged(articleId, 0, 10);

        assertEquals(1, results.size());
        assertEquals(topLevel.getId(), results.getFirst().comment().getId());
    }

    @Test
    void findCommentsByParentId_shouldReturnReplies() {
        Comment topLevel = Comment.builder()
                .articleId(getValidArticleId())
                .creatorId(getValidAuthorId())
                .commentBody("Top level")
                .build();
        getRepository().insert(topLevel);

        Comment reply = Comment.builder()
                .articleId(getValidArticleId())
                .creatorId(getValidAuthorId())
                .commentBody("Reply to top level")
                .parentCommentId(topLevel.getId())
                .build();
        getRepository().insert(reply);

        List<CommentWithAuthor> replies = getRepository().findCommentsByParentId(topLevel.getId());

        assertEquals(1, replies.size());
        assertEquals(reply.getId(), replies.getFirst().comment().getId());
    }
}
