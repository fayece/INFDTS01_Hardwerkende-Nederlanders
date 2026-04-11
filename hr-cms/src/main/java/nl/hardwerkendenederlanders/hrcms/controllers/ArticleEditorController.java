package nl.hardwerkendenederlanders.hrcms.controllers;

import lombok.extern.slf4j.Slf4j;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.services.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.UUID;

@Slf4j
@Controller
public class ArticleEditorController {
    ArticleService _articleService;

    public  ArticleEditorController(ArticleService articleService){
        _articleService = articleService;
    }

    // start empty editor
    @GetMapping("article-editor")
    public String GetArticle(Model model) {
        model.addAttribute("articleForm", new Article());
        return "pages/article-editor-page";
    }

    // load exisiting article
    @GetMapping("article-editor/{articleId}")
    public String GetArticle(Model model, @PathVariable(value="articleId")UUID id) {
        Article article = _articleService.GetById(id);
        model.addAttribute("articleForm", article);
        return "pages/article-editor-page";
    }

    // method must be Post for HTML form (it does not support put)
    // save draft
    @PostMapping("save-article")
    public String PutDraftArticle(Model model, @ModelAttribute("articleForm") Article articleForm) {
        model.addAttribute("articleForm", articleForm);
        try{
            _articleService.EnsureArticleExists(articleForm);
        }
        catch (Exception e){
            log.error("e: ", e);
            return "redirect:/article-editor/" + articleForm.getId().toString() + "?error=true";

        }

        return "redirect:/article-editor/" + articleForm.getId().toString();
    }
}