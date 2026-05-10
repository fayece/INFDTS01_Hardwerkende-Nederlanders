package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentViewDto;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.PagedComments;

public interface CommentService {

    PagedComments getTopLevelComments(UUID articleId, int offset, int limit);

    List<CommentViewDto> getReplies(UUID parentId);

    CommentViewDto postComment(Comment comment, HttpSession session);

    CommentViewDto deleteComment(UUID commentId, HttpSession session);
}
