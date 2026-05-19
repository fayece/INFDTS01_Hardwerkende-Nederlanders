package nl.hardwerkendenederlanders.hrcms.controllers;

import nl.hardwerkendenederlanders.hrcms.configuration.RequiresPermission;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("article/editor")
@Controller
public class EditorArticleOverviewController {
    private final ArticleService articleService;

    public EditorArticleOverviewController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @RequiresPermission("article:create")
    @GetMapping("/overview")
    public String GetArticleEditorOverview(Model model, @RequestParam(defaultValue = "1") int page) {
        model.addAttribute("articles", articleService.findAllPaged(20, page));
        model.addAttribute("currentPage", page);
        return "pages/article-editor-overview";
    }
}
