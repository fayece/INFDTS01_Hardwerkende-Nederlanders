package nl.hardwerkendenederlanders.hrcms.database;

import java.util.List;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentWithAuthor;

public interface CommentRepository extends DatabaseMutableRepository<Comment> {

    CommentWithAuthor insertReturning(Comment comment);

    List<CommentWithAuthor> findTopLevelCommentsByArticleIdPaged(UUID articleId, int offset, int limit);

    List<CommentWithAuthor> findCommentsByParentId(UUID parentId);
}
