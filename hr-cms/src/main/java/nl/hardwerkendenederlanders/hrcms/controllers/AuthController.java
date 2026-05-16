package nl.hardwerkendenederlanders.hrcms.controllers;

import jakarta.servlet.http.HttpSession;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.AuthService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ProfileService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserSessionService userSessionService;
    private final ProfileService profileService;
    private final AuthService authService;

    public AuthController(UserSessionService userSessionService, ProfileService profileService, AuthService authService) {
        this.userSessionService = userSessionService;
        this.profileService = profileService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(HttpSession session, String email, String password) {
        try {
            User user = authService.login(email, password);
            userSessionService.login(session, user);

            Profile profileExists = profileService.getProfileById(user.getId());
            if (profileExists == null)
                return "redirect:/create-new-profile";

            return "redirect:/";
        } catch (RuntimeException e) {
            return "redirect:/login?error=true";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        userSessionService.logout(session);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) Boolean error, Model model) {
        model.addAttribute("error", error);
        return "pages/login";
    }
}
