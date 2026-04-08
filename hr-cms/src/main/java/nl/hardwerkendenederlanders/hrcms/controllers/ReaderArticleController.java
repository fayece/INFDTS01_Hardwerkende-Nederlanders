package nl.hardwerkendenederlanders.hrcms.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ReaderArticleController {
    @GetMapping("article/{articleId}")
    public String GetArticle(@PathVariable(value="articleId") String articleId) {
        return "pages/article-page";
    }
}
