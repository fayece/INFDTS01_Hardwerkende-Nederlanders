package nl.hardwerkendenederlanders.hrcms.database;

import java.util.List;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Comment;

public interface CommentRepository extends DatabaseMutableRepository<Comment> {

    public List<Comment> findTopLevelCommentsByArticleIdPaged(UUID articleId, int page, int limit);

    public List<Comment> findCommentsByParentId(UUID parentId);
}
