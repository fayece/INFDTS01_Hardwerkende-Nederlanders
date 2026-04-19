package nl.hardwerkendenederlanders.hrcms.controllers;

import java.util.UUID;

import jakarta.servlet.http.HttpSession;
import nl.hardwerkendenederlanders.hrcms.exceptions.ComponentUnavailableException;
import nl.hardwerkendenederlanders.hrcms.models.ArticleViewer;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.ArticleWithSubjectAndViewsDto;
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
    private  final UserSessionService userSessionService;
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
        articleViewersService.AddView(new ArticleViewer(articleId, userSessionService.getLoggedInUser(httpSession)));
        ArticleWithSubjectAndViewsDto articleWithSubject = articleService.findArticleWithSubjectById(articleId);

        if (articleWithSubject == null) {
            return "error/404";
        }
        model.addAttribute("view_count", articleWithSubject.viewCount());
        model.addAttribute("article", articleWithSubject.article());
        model.addAttribute("subject", articleWithSubject.subjectName());
        model.addAttribute("authors", authorsService.findAuthorsForArticle(articleId));

        try {
            PagedComments result = commentService.getTopLevelComments(articleId, 0, 10);
            model.addAttribute("comments", result.comments());
            model.addAttribute("hasMore", result.hasMore());
            model.addAttribute("nextPage", 2);
            model.addAttribute("articleId", articleId);
            model.addAttribute("offset", result.comments().size());

        } catch (ComponentUnavailableException e) {
            model.addAttribute("failedComponent", "comments");
        }

        return "pages/article-page";
    }
}
