package nl.hardwerkendenederlanders.hrcms.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArticleEditorController {
    @GetMapping("article-editor")
    public String GetArticle(String articleId) {
        return "pages/article-editor-page";
    }
}
