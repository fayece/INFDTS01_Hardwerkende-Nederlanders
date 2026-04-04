package nl.hardwerkendenederlanders.hrcms.controllers;

import java.util.List;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.UserRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ComponentUnavailableException;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.PagedComments;
import nl.hardwerkendenederlanders.hrcms.services.ArticleService;
import nl.hardwerkendenederlanders.hrcms.services.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/article")
public class ReaderArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;
    private final UserRepository userRepository;

    public ReaderArticleController(
            ArticleService articleService, CommentService commentService, UserRepository userRepository) {
        this.articleService = articleService;
        this.commentService = commentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{articleId}")
    public String getArticle(@PathVariable UUID articleId, Model model) {
        Article article = articleService.getArticleById(articleId);

        if (article == null) {
            return "error/404";
        }

        model.addAttribute("article", article);

        try {
            PagedComments result = commentService.getTopLevelComments(articleId, 0, 10);
            model.addAttribute("comments", result.comments());
            model.addAttribute("hasMore", result.hasMore());
            model.addAttribute("nextPage", 2);
            model.addAttribute("articleId", articleId);
            model.addAttribute("offset", result.comments().size());

            // TODO: remove user-related code once auth has been set up.
            List<User> users = userRepository.findAllPaged(1, Integer.MAX_VALUE);
            model.addAttribute("users", users);

        } catch (ComponentUnavailableException e) {
            model.addAttribute("failedComponent", "comments");
        }

        return "pages/article-page";
    }
}
