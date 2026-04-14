package nl.hardwerkendenederlanders.hrcms.services;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.CommentRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ComponentActionException;
import nl.hardwerkendenederlanders.hrcms.exceptions.ComponentUnavailableException;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentViewDto;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentWithAuthor;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.PagedComments;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.CommentService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserSessionService userSessionService;

    private static final String UNAVAILABLE_FRAGMENT = "fragments/articles/comment-section :: comments-unavailable";

    public CommentServiceImpl(CommentRepository commentRepository, UserSessionService userSessionService) {
        this.commentRepository = commentRepository;
        this.userSessionService = userSessionService;
    }

    public PagedComments getTopLevelComments(UUID articleId, int offset, int limit) {
        try {
            List<CommentViewDto> comments =
                    commentRepository.findTopLevelCommentsByArticleIdPaged(articleId, offset, limit + 1).stream()
                            .map(record ->
                                    CommentViewDto.from(record.comment(), record.authorName(), record.replyCount()))
                            .toList();

            boolean hasMore = comments.size() > limit;
            comments = hasMore ? comments.subList(0, limit) : comments;

            return new PagedComments(comments, hasMore);
        } catch (Exception e) {
            throw new ComponentUnavailableException("comments", UNAVAILABLE_FRAGMENT, e);
        }
    }

    public List<CommentViewDto> getReplies(UUID parentId) {
        try {
            return commentRepository.findCommentsByParentId(parentId).stream()
                    .map(record -> CommentViewDto.from(record.comment(), record.authorName(), record.replyCount()))
                    .toList();
        } catch (Exception e) {
            throw new ComponentUnavailableException("comments", UNAVAILABLE_FRAGMENT, e);
        }
    }

    public CommentViewDto postComment(Comment comment, HttpSession session) {
        try {
            UUID userId = userSessionService.getLoggedInUser(session);
            comment.setCreatorId(userId);

            CommentWithAuthor result = commentRepository.insertReturning(comment);
            return CommentViewDto.from(result.comment(), result.authorName(), result.replyCount());

        } catch (ComponentActionException e) {
            throw e;
        } catch (Exception e) {
            String target = "/article/" + comment.getArticleId();
            throw new ComponentActionException("comment", ComponentActionException.Action.CREATE, target, e, "Please try again later");
        }
    }
}
