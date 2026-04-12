package nl.hardwerkendenederlanders.hrcms.controllers;

import nl.hardwerkendenederlanders.hrcms.services.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthorArticleOverviewController {
    private ArticleService _articleService;

    public AuthorArticleOverviewController(ArticleService articleService) {
        _articleService = articleService;
    }

    @GetMapping("articles-editor-overview")
    public String GetArticleEditorOverview(Model model) {
        model.addAttribute("articles", _articleService.GetAll());
        return "pages/articles-editor-overview";
    }
}
