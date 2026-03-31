package nl.hardwerkendenederlanders.hrcms.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReaderArticleController {
    @GetMapping("article")
    public String GetArticle(String articleId) {
        return "pages/article-page";
    }
}
