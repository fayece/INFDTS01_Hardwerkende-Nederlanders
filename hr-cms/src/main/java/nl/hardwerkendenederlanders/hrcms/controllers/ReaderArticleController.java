package nl.hardwerkendenederlanders.hrcms.controllers;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.exceptions.ComponentUnavailableException;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.ArticleWithSubject;
import nl.hardwerkendenederlanders.hrcms.models.dtos.comment.PagedComments;
import nl.hardwerkendenederlanders.hrcms.services.CommentServiceImpl;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleService;
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

    public ReaderArticleController(ArticleService articleService, CommentServiceImpl commentService) {
        this.articleService = articleService;
        this.commentService = commentService;
    }

    @GetMapping("/{articleId}")
    public String getArticle(@PathVariable UUID articleId, Model model) {
        ArticleWithSubject articleWithSubject = articleService.findArticleWithSubjectById(articleId);

        if (articleWithSubject == null) {
            return "error/404";
        }

        model.addAttribute("article", articleWithSubject.article());
        model.addAttribute("subject", articleWithSubject.subjectName());

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
