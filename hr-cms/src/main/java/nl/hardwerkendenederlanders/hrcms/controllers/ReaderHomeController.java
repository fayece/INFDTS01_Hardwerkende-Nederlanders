package nl.hardwerkendenederlanders.hrcms.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReaderHomeController {

    @GetMapping("/")
    public String getHomePage(Model model) {
        model.addAttribute("articles", new String[] {"foobar", "snuffelaar", "brabbelaar"});
        return "pages/index";
    }
}
