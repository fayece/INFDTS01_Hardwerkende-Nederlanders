package nl.hardwerkendenederlanders.hrcms.controllers;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentCreateDto;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.CommentViewDto;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.PagedComments;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.CommentService;
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

    public ReaderCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/article/{articleId}")
    public String getCommentsByArticle(
            @PathVariable UUID articleId, @RequestParam(defaultValue = "10") int offset, Model model) {
        final int limit = 10;
        PagedComments result = commentService.getTopLevelComments(articleId, offset, limit);

        model.addAttribute("comments", result.comments());
        model.addAttribute("hasMore", result.hasMore());
        model.addAttribute("offset", offset + result.comments().size());
        model.addAttribute("articleId", articleId);

        return COMMENT_SECTION_VIEW;
    }

    @GetMapping("/{parentId}/replies")
    public String getReplies(@PathVariable UUID parentId, Model model) {
        List<CommentViewDto> replies = commentService.getReplies(parentId);
        model.addAttribute("comments", replies);

        return COMMENT_LIST_FRAGMENT;
    }

    @PostMapping("/article/{articleId}/new")
    public String postComment(
            @PathVariable UUID articleId, @ModelAttribute CommentCreateDto formDto, HttpSession session) {

        Comment comment = formDto.toComment(articleId);
        commentService.postComment(comment, session);

        return "redirect:/article/" + articleId;
    }
}
