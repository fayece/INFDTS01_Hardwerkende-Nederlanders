package nl.hardwerkendenederlanders.hrcms.controllers;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import nl.hardwerkendenederlanders.hrcms.exceptions.ComponentActionException;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.SubjectService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/article/editor")
@Controller
public class EditorArticleController {
    private final ArticleService articleService;
    private final SubjectService subjectService;
    private final UserSessionService userSessionService;

    public EditorArticleController(
            ArticleService articleService, SubjectService subjectService, UserSessionService userSessionService) {
        this.articleService = articleService;
        this.subjectService = subjectService;
        this.userSessionService = userSessionService;
    }

    // start empty editor
    @GetMapping("")
    public String getArticle(Model model) {
        model.addAttribute("articleForm", Article.builder().build());
        model.addAttribute("subjects", subjectService.findAll());
        return "pages/article-editor-page";
    }

    // load existing article
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
    public String putArticle(Model model, @ModelAttribute("articleForm") Article articleForm, HttpSession httpSession) {
        model.addAttribute("articleForm", articleForm);
        try {
            var user = userSessionService.getLoggedInUser(httpSession);
            articleService.ensureArticleExists(articleForm, user);

        } catch (ComponentActionException e) {
            if (e.getAction() == ComponentActionException.Action.CREATE) {
                return "redirect:/article/editor?error=INSERT";
            } else {
                return "redirect:/article/editor/" + articleForm.getId().toString() + "?error=UPDATE";
            }
        }

        return "redirect:/article/editor/" + articleForm.getId().toString();
    }
}
