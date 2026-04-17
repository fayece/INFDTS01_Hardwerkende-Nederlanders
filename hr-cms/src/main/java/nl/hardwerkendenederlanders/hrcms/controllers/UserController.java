package nl.hardwerkendenederlanders.hrcms.controllers;

import jakarta.servlet.http.HttpSession;
import java.util.*;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@Validated
@RequestMapping("manage-users")
public class UserController {
    private final UserService userService;
    private final UserSessionService userSessionService;

    public UserController(UserService userService, UserSessionService userSessionService) {
        this.userService = userService;
        this.userSessionService = userSessionService;
    }

    @GetMapping()
    public String manageUserPage(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(required = false) String searchName,
            @RequestParam(required = false) Boolean sortActive,
            Model model,
            HttpSession session) {
        List<User> users = userService.getUsers(page, searchName, sortActive);
        int maxPages = userService.getMaxPages(searchName, sortActive);

        @SuppressWarnings("unchecked")
        ArrayDeque<String> recentSearches = (ArrayDeque<String>) session.getAttribute("recentSearches");

        if (recentSearches == null) {
            recentSearches = new ArrayDeque<>();
        }

        if (searchName != null) {
            model.addAttribute("searchName", searchName);
            recentSearches.remove(searchName);
            recentSearches.addFirst(searchName);
            int maxSearches = 5;
            while (recentSearches.size() > maxSearches) {
                recentSearches.removeLast();
            }
            session.setAttribute("recentSearches", recentSearches);
        }

        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("finalPage", maxPages);

        return "pages/manage-users";
    }

    @GetMapping("/create-user")
    public String createUserPage() {
        return "pages/create-user";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable UUID id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/manage-users";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("currentUserId", id);
        return "pages/edit-user";
    }

    @PostMapping("/new")
    public String createNewUser(
            @RequestParam String firstName,
            @RequestParam(required = false) String prefix,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password,
            Model model) {

        userService.insertUser(firstName, prefix, lastName, email, password);
        model.addAttribute("inserted", true);
        return "pages/create-user";
    }

    @PostMapping("/update")
    public String updateUser(
            @RequestParam UUID id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String prefix,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UUID roleId,
            @RequestParam(required = false) UUID organisationId,
            Model model) {
        userService.updateUser(id, firstName, prefix, lastName, email, roleId, organisationId);
        return "redirect:/manage-users"; // or a successpage -> manage-users
    }

    @PostMapping("/set-active")
    public String changeActiveStatus(@RequestParam UUID userId, @RequestParam boolean setActive, Model model) {
        userService.updateActivityById(userId, setActive);

        return "redirect:/manage-users";
    }

    @GetMapping("/confirm-delete")
    public String confirmDeleteUser(@RequestParam UUID userId, Model model) {
        User user = userService.findById(userId);
        model.addAttribute("userFirstName", user.getFirstName());
        model.addAttribute("userLastName", user.getLastName());
        model.addAttribute("userEmail", user.getEmail());

        model.addAttribute("userToDeleteId", userId);
        return "pages/confirm-delete-user";
    }

    @PostMapping("/delete-user")
    public String deleteUser(@RequestParam UUID userId, HttpSession session) {
        UUID currentUserId = userSessionService.getLoggedInUser(session);
        userService.deleteById(userId, currentUserId);

        return "redirect:/manage-users";
    }
}
