package nl.hardwerkendenederlanders.hrcms.controllers;

import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReaderHomeController {
    private final ArticleService articleService;

    public ReaderHomeController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/")
    public String getHomePage(Model model, @RequestParam(defaultValue = "1") int page) {
        model.addAttribute("articles", articleService.findNewPublished(20, page));
        model.addAttribute("currentPage", page);
        return "pages/index";
    }
}
