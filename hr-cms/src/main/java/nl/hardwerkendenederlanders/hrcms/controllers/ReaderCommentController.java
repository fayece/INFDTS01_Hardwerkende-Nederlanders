package nl.hardwerkendenederlanders.hrcms.controllers;

import java.util.List;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.UserRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ComponentUnavailableException;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.services.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comment")
public class ReaderCommentController {

    private final CommentService commentService;

    // Fragments set up to avoid false positives on component unavailability.
    private static final String COMMENT_SECTION_FRAGMENT = "fragments/articles/comment-section";
    private static final String COMMENT_SECTION_VIEW = COMMENT_SECTION_FRAGMENT + " :: comment-section";
    private static final String COMMENT_LIST_FRAGMENT = COMMENT_SECTION_FRAGMENT + " :: comment-list";
    private static final String COMMENTS_UNAVAILABLE_FRAGMENT = COMMENT_SECTION_FRAGMENT + " :: comments-unavailable";

    // TODO: remove user-related code once auth has been set up.
    private final UserRepository userRepository;

    public ReaderCommentController(CommentService commentService, UserRepository userRepository) {
        this.commentService = commentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/article/{articleId}")
    public String getCommentsByArticle(
            @PathVariable UUID articleId, @RequestParam(defaultValue = "1") int page, Model model) {
        // setting limit
        final int limit = 10;
        List<Comment> comments = commentService.getTopLevelComments(articleId, page, limit);
        model.addAttribute("comments", comments);

        // TODO: remove user-related code once auth has been set up.
        List<User> users = userRepository.findAllPaged(1, Integer.MAX_VALUE);
        model.addAttribute("users", users);

        return COMMENT_SECTION_VIEW;
    }

    @GetMapping("/{parentId}/replies")
    public String getReplies(@PathVariable UUID parentId, Model model) {
        List<Comment> replies = commentService.getReplies(parentId);
        model.addAttribute("comments", replies);

        return COMMENT_LIST_FRAGMENT;
    }

    @PostMapping("/article/{articleId}/new")
    public String postComment(
            @PathVariable UUID articleId,
            @RequestParam String commentBody,
            @RequestParam UUID authorId,
            @RequestParam(required = false) UUID mediaId,
            @RequestParam(required = false) UUID parentCommentId) {
        Comment comment = new Comment(commentBody, authorId, articleId, mediaId, parentCommentId);

        commentService.postComment(comment);

        return "redirect:/article/" + articleId;
    }

    @ExceptionHandler(ComponentUnavailableException.class)
    @ResponseStatus(HttpStatus.OK)
    public String handleUnavailable(ComponentUnavailableException ex, Model model) {
        model.addAttribute("failedComponent", ex.getComponentName());
        return COMMENTS_UNAVAILABLE_FRAGMENT;
    }
}
