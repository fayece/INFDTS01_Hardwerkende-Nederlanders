package nl.hardwerkendenederlanders.hrcms.controllers;

import jakarta.servlet.http.HttpSession;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;

import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.AuthService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;

import java.util.UUID;


@Controller
public class AuthController {

    private final UserSessionService userSessionService;
    private final AuthService authService;

    public AuthController(UserSessionService userSessionService, AuthService authService){
        this.userSessionService = userSessionService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(HttpSession session, String email, String password){
        try {
            User user = authService.login(email, password);
            userSessionService.login(session, user);
            return "redirect:/home";
        } catch (RuntimeException e) {
            return "redirect:/login?error=true";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session){
        userSessionService.logout(session);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) Boolean error, Model model) {
        model.addAttribute("error", error);
        return "pages/login";
    }
}
