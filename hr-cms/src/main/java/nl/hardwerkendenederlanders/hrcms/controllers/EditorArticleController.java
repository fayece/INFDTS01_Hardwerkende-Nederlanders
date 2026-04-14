package nl.hardwerkendenederlanders.hrcms.controllers;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/article/editor")
@Controller
public class EditorArticleController {
    ArticleService articleService;
    SubjectService subjectService;

    public EditorArticleController(ArticleService articleService, SubjectService subjecService) {
        this.articleService = articleService;
        this.subjectService = subjecService;
    }

    // start empty editor
    @GetMapping("")
    public String getArticle(Model model) {
        model.addAttribute("articleForm", new Article());
        model.addAttribute("subjects", subjectService.findAll());
        return "pages/article-editor-page";
    }

    // load exisiting article
    @GetMapping("/{articleId}")
    public String getArticle(Model model, @PathVariable(value = "articleId") UUID id) {
        Article article = articleService.findById(id);
        if (article == null) return "redirect:/error/404";

        model.addAttribute("articleForm", article);
        model.addAttribute("subjects", subjectService.findAll());
        return "pages/article-editor-page";
    }

    // method must be Post for HTML form (it does not support put)
    // save draft
    @PostMapping("/save")
    public String putArticle(Model model, @ModelAttribute("articleForm") Article articleForm) {
        model.addAttribute("articleForm", articleForm);
        try {
            articleService.ensureArticleExists(articleForm);
        } catch (Exception e) {
            log.error("e: ", e);
            return "redirect:/article/editor/" + articleForm.getId().toString() + "?error=true";
        }

        return "redirect:/article/editor/" + articleForm.getId().toString();
    }
}
