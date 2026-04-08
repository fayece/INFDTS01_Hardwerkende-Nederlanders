package nl.hardwerkendenederlanders.hrcms.controllers;

import nl.hardwerkendenederlanders.hrcms.services.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReaderHomeController {
    private final ArticleService articleService;

    public ReaderHomeController(ArticleService articleService){
        this.articleService = articleService;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        model.addAttribute("articles", articleService.GetAll());
        return "pages/index";
    }
}
