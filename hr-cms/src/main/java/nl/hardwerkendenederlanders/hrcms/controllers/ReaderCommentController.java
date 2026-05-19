package nl.hardwerkendenederlanders.hrcms.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.configuration.RequiresPermission;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentCreateDto;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentViewDto;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.PagedComments;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comment")
public class ReaderCommentController {

    private final CommentService commentService;

    // Fragments set up to avoid false positives on component unavailability.
    private static final String COMMENT_SECTION_FRAGMENT = "fragments/articles/comment-section";
    private static final String COMMENT_FRAGMENT = "fragments/articles/comment";
    private static final String COMMENT_SECTION_VIEW = COMMENT_SECTION_FRAGMENT + " :: comment-section";
    private static final String COMMENT_LIST_FRAGMENT = COMMENT_SECTION_FRAGMENT + " :: comment-list";
    private static final String COMMENT_VIEW = COMMENT_FRAGMENT + " :: comment";

    public ReaderCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @RequiresPermission("comment:read")
    @GetMapping("/article/{articleId}")
    public String getCommentsByArticle(
            @PathVariable UUID articleId, @RequestParam(defaultValue = "10") int offset, Model model) {
        final int limit = 10;
        PagedComments result = commentService.getTopLevelComments(articleId, offset, limit);

        model.addAttribute("comments", result.getComments());
        model.addAttribute("hasMore", result.isHasMore());
        model.addAttribute("offset", offset + result.getComments().size());
        model.addAttribute("articleId", articleId);

        return COMMENT_SECTION_VIEW;
    }

    @RequiresPermission("comment:read")
    @GetMapping("/{parentId}/replies")
    public String getReplies(@PathVariable UUID parentId, Model model) {
        List<CommentViewDto> replies = commentService.getReplies(parentId);
        model.addAttribute("comments", replies);

        return COMMENT_LIST_FRAGMENT;
    }

    @RequiresPermission("comment:create")
    @PostMapping("/article/{articleId}/new")
    public String postComment(
            @PathVariable UUID articleId,
            @Valid @ModelAttribute CommentCreateDto formDto,
            BindingResult bindingResult,
            Model model,
            HttpSession session) {

        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(bindingResult.getFieldError("commentBody"))
                    .getDefaultMessage();
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("articleId", articleId);
            return COMMENT_SECTION_VIEW;
        }

        Comment comment = formDto.toComment(articleId);
        CommentViewDto dto = commentService.postComment(comment, session);

        model.addAttribute("comment", dto);
        return COMMENT_VIEW;
    }

    @RequiresPermission("comment:delete")
    @DeleteMapping("/{commentId}")
    public String deleteComment(@PathVariable UUID commentId, HttpSession session, Model model) {
        CommentViewDto dto = commentService.deleteComment(commentId, session);
        model.addAttribute("comment", dto);
        return COMMENT_VIEW;
    }
}
