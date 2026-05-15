package nl.hardwerkendenederlanders.hrcms.controllers;

import nl.hardwerkendenederlanders.hrcms.configuration.RequiresPermission;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReaderProfileController {

    @RequiresPermission("profile:read")
    @GetMapping("profile")
    public String GetArticle(String articleId) {
        return "pages/profile-page";
    }
}
