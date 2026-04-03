package nl.hardwerkendenederlanders.hrcms.controllers;

import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.services.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/article")
public class ReaderArticleController {

    private final ArticleService articleService;

    public ReaderArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/{articleId}")
    public String getArticle(@PathVariable UUID articleId, Model model) {
        Article article = articleService.getArticleById(articleId);

        if (article == null) {
            return "error/404";
        }

        model.addAttribute("article", article);
        return "pages/article-page";
    }
}
