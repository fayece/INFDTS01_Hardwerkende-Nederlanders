package nl.hardwerkendenederlanders.hrcms.services;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.CommentRepository;
import nl.hardwerkendenederlanders.hrcms.database.mongodb.ProfileRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ComponentActionException;
import nl.hardwerkendenederlanders.hrcms.exceptions.ComponentUnavailableException;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentViewDto;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentWithAuthor;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.PagedComments;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.CommentService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserSessionService userSessionService;
    private final ProfileRepository profileRepository;

    private static final String UNAVAILABLE_FRAGMENT = "fragments/articles/comment-section :: comments-unavailable";

    public CommentServiceImpl(
            CommentRepository commentRepository,
            UserSessionService userSessionService,
            ProfileRepository profileRepository) {
        this.commentRepository = commentRepository;
        this.userSessionService = userSessionService;
        this.profileRepository = profileRepository;
    }

    @Cacheable(value = "topCommentsArticle", key = "#articleId")
    public PagedComments getTopLevelComments(UUID articleId, int offset, int limit) {
        try {
            List<CommentViewDto> comments =
                    commentRepository.findTopLevelCommentsByArticleIdPaged(articleId, offset, limit + 1).stream()
                            .map(record -> {
                                String username = profileRepository
                                        .findById(
                                                record.comment().getCreatorId().toString())
                                        .map(Profile::getUsername)
                                        .orElse("deleted_user");
                                return CommentViewDto.from(record.comment(), username, record.replyCount());
                            })
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
                    .map(record -> {
                        String username = profileRepository
                                .findById(record.comment().getCreatorId().toString())
                                .map(Profile::getUsername)
                                .orElse("deleted_user");
                        return CommentViewDto.from(record.comment(), username, record.replyCount());
                    })
                    .toList();
        } catch (Exception e) {
            throw new ComponentUnavailableException("comments", UNAVAILABLE_FRAGMENT, e);
        }
    }

    @CacheEvict(value = "topCommentsArticle", key = "#comment.articleId")
    public CommentViewDto postComment(Comment comment, HttpSession session) {
        Optional<UUID> userId = userSessionService.getLoggedInUser(session);
        if (userId.isEmpty()) {
            String target = "/article/" + comment.getArticleId();
            throw new ComponentActionException(
                    "comment", ComponentActionException.Action.CREATE, target, null, "Please log in to comment");
        }
        try {
            comment.setCreatorId(userId.get());
            CommentWithAuthor result = commentRepository.insertReturning(comment);

            String username = profileRepository
                    .findById(result.comment().getCreatorId().toString())
                    .map(Profile::getUsername)
                    .orElse("deleted_user");

            return CommentViewDto.from(result.comment(), username, result.replyCount());
        } catch (ComponentActionException e) {
            throw e;
        } catch (Exception e) {
            String target = "/article/" + comment.getArticleId();
            throw new ComponentActionException(
                    "comment", ComponentActionException.Action.CREATE, target, e, "Please try again later");
        }
    }

    public CommentViewDto deleteComment(UUID commentId, HttpSession session) {
        Optional<UUID> userId = userSessionService.getLoggedInUser(session);
        if (userId.isEmpty()) {
            throw new ComponentActionException(
                    "comment", ComponentActionException.Action.DELETE, "/", null, "Please log in to delete a comment");
        }

        Comment comment = commentRepository
                .findById(commentId)
                .orElseThrow(() -> new ComponentActionException(
                        "comment", ComponentActionException.Action.DELETE, "/", null, "Comment not found"));

        if (!comment.getCreatorId().equals(userId.get())) {
            throw new ComponentActionException(
                    "comment",
                    ComponentActionException.Action.DELETE,
                    "/",
                    null,
                    "You can only delete your own comments");
        }

        commentRepository.delete(commentId);

        CommentWithAuthor deleted = commentRepository
                .findByIdWithAuthor(commentId)
                .orElseThrow(() -> new ComponentActionException(
                        "comment",
                        ComponentActionException.Action.DELETE,
                        "/",
                        null,
                        "Comment not found after deletion"));

        String username = profileRepository
                .findById(deleted.comment().getCreatorId().toString())
                .map(Profile::getUsername)
                .orElse("deleted_user");

        return CommentViewDto.from(deleted.comment(), username, deleted.replyCount());
    }
}
