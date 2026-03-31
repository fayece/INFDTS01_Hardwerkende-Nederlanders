package nl.hardwerkendenederlanders.hrcms.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReaderProfileController {
    @GetMapping("profile")
    public String GetArticle(String articleId){
        return "pages/profile-page";
    }
}
