package nl.hardwerkendenederlanders.hrcms.controllers;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.exceptions.ComponentUnavailableException;
import nl.hardwerkendenederlanders.hrcms.models.ArticleViewer;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.ArticleFullDetailsDto;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.PagedComments;
import nl.hardwerkendenederlanders.hrcms.services.CommentServiceImpl;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleAuthorsService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleViewersService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/article")
public class ReaderArticleController {

    private final ArticleService articleService;
    private final CommentServiceImpl commentService;
    private final ArticleAuthorsService authorsService;
    private final UserSessionService userSessionService;
    private final ArticleViewersService articleViewersService;

    public ReaderArticleController(
            ArticleService articleService,
            CommentServiceImpl commentService,
            ArticleAuthorsService authorsService,
            UserSessionService userSessionService,
            ArticleViewersService articleViewersSerivce) {
        this.articleService = articleService;
        this.commentService = commentService;
        this.authorsService = authorsService;
        this.userSessionService = userSessionService;
        this.articleViewersService = articleViewersSerivce;
    }

    @GetMapping("/{articleId}")
    public String getArticle(@PathVariable UUID articleId, Model model, HttpSession httpSession) {
        var user = userSessionService.getLoggedInUser(httpSession);
        if (user.isEmpty()) {
            return "redirect:/login";
        }
        articleViewersService.addView(new ArticleViewer(articleId, user.get()));
        ArticleFullDetailsDto articleFull = articleService.findArticleFullId(articleId);

        if (articleFull == null) {
            return "error/404";
        }
        model.addAttribute("article", articleFull);
        model.addAttribute("authors", authorsService.findAuthorsForArticle(articleId));

        try {
            PagedComments result = commentService.getTopLevelComments(articleId, 0, 10);
            model.addAttribute("comments", result.getComments());
            model.addAttribute("hasMore", result.isHasMore());
            model.addAttribute("nextPage", 2);
            model.addAttribute("articleId", articleId);
            model.addAttribute("offset", result.getComments().size());

        } catch (ComponentUnavailableException e) {
            model.addAttribute("failedComponent", "comments");
        }

        return "pages/article-page";
    }
}
