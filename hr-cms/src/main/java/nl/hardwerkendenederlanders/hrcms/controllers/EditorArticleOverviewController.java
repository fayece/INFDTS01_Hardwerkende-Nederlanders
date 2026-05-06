package nl.hardwerkendenederlanders.hrcms.controllers;

import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("article/editor")
@Controller
public class EditorArticleOverviewController {
    private final ArticleService articleService;

    public EditorArticleOverviewController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/overview")
    public String GetArticleEditorOverview(Model model) {
        model.addAttribute("articles", articleService.findAllPaged(20, 1));
        return "pages/article-editor-overview";
    }
}
