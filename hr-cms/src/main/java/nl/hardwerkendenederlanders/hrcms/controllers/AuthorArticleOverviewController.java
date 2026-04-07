package nl.hardwerkendenederlanders.hrcms.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthorArticleOverviewController {
    @GetMapping("articles-editor-overview")
    public String GetArticleEditorOverview(Model model) {

        return "pages/articles-editor-overview";
    }
}
