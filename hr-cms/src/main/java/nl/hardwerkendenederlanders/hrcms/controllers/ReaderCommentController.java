package nl.hardwerkendenederlanders.hrcms.controllers;

import java.util.List;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Comment;
import nl.hardwerkendenederlanders.hrcms.services.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/comment")
public class ReaderCommentController {

    private final CommentService commentService;
    private final int limit = 10;

    public ReaderCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/article/{articleId}")
    public String getCommentsByArticle(
        @PathVariable UUID articleId, @RequestParam(defaultValue = "1") int page, Model model) {

        List<Comment> comments = commentService.getTopLevelComments(articleId, page, limit);
        model.addAttribute("comments", comments);

        return "fragments/articles/comment-section :: comment-list";
    }

    @GetMapping("/{parentId}/replies")
    public String getReplies(@PathVariable UUID parentId, Model model) {

        List<Comment> replies = commentService.getReplies(parentId);
        model.addAttribute("comments", replies);

        return "fragments/articles/comment-section :: comment-list";
    }
}
