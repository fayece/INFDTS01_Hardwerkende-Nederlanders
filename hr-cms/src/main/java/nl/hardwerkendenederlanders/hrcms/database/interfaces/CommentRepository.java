package nl.hardwerkendenederlanders.hrcms.database.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentWithAuthor;

public interface CommentRepository extends DatabaseRepository<Comment> {

    CommentWithAuthor insertReturning(Comment comment);

    Optional<CommentWithAuthor> findByIdWithAuthor(UUID id);

    List<CommentWithAuthor> findTopLevelCommentsByArticleIdPaged(UUID articleId, int offset, int limit);

    List<CommentWithAuthor> findCommentsByParentId(UUID parentId);
}
