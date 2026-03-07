package nl.hardwerkendenederlanders.hrcms.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReaderHomeController {

    @GetMapping("/")
    public String getHomePage() {

        return "index";
    }
}
