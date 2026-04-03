package nl.hardwerkendenederlanders.hrcms.controllers;

import nl.hardwerkendenederlanders.hrcms.Forms.ArticleForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ArticleEditorController {
    @GetMapping("article-editor")
    public String GetArticle(Model model) {
        model.addAttribute("articleForm", new ArticleForm());
        return "pages/article-editor-page";
    }

    @PostMapping("article-editor")
    public String PostArticle(Model model, @ModelAttribute("articleForm") ArticleForm articleForm){
        model.addAttribute("articleForm", articleForm);
        System.out.println(articleForm.getTitle());
        System.out.println(articleForm.getTextContent());
        return "pages/article-editor-page";
    }
}
