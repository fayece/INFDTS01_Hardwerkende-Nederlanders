package nl.hardwerkendenederlanders.hrcms.controllers;

import jakarta.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;
import nl.hardwerkendenederlanders.hrcms.configuration.RequiresPermission;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.Role;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.models.dtos.userDtos.UserViewDto;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ProfileService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.RoleService;
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
    private final RoleService roleService;
    private final ProfileService profileService;

    public UserController(
            UserService userService,
            UserSessionService userSessionService,
            RoleService roleService,
            ProfileService profileService) {
        this.userService = userService;
        this.userSessionService = userSessionService;
        this.roleService = roleService;
        this.profileService = profileService;
    }

    @RequiresPermission("admin:manage_users")
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

        Map<UUID, String> roleNames =
                roleService.findAll().stream().collect(Collectors.toMap(Role::getId, Role::getRoleName));

        List<UserViewDto> userDtos =
                users.stream().map(user -> toUserViewDto(user, roleNames)).toList();

        model.addAttribute("users", userDtos);
        model.addAttribute("currentPage", page);
        model.addAttribute("finalPage", maxPages);

        return "pages/manage-users";
    }

    @RequiresPermission("admin:manage_users")
    @GetMapping("/create-user")
    public String createUserPage(Model model) {
        model.addAttribute("user", User.builder().build());
        model.addAttribute("roles", roleService.findAll());
        return "pages/create-user";
    }

    @RequiresPermission("admin:manage_users")
    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable UUID id, Model model) {
        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/manage-users";
        }

        List<Role> roles = roleService.findAll();

        model.addAttribute("currentUser", user);
        model.addAttribute("currentUserId", id);
        model.addAttribute("roles", roles);
        return "pages/edit-user";
    }

    @RequiresPermission("admin:manage_users")
    @PostMapping("/new")
    public String createNewUser(
            @RequestParam User userModel,
            Model model) {

        userService.insertUser(userModel);
        model.addAttribute("inserted", true);
        String username = userService.insertUser(userModel);
        model.addAttribute("successMessage", "User \"" + username + "\" created successfully!");

        model.addAttribute("roles", roleService.findAll());
        return "pages/create-user";
    }

    @RequiresPermission("admin:manage_users")
    @PostMapping("/update")
    public String updateUser(
            @RequestParam UUID id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String prefix,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) UUID roleId) {
        userService.updateUser(id, firstName, prefix, lastName, roleId);
        return "redirect:/manage-users"; // or a successpage -> manage-users
    }

    @RequiresPermission("admin:manage_users")
    @PostMapping("/set-active")
    public String changeActiveStatus(@RequestParam UUID userId, @RequestParam boolean setActive) {
        userService.updateActivityById(userId, setActive);

        return "redirect:/manage-users";
    }

    @RequiresPermission("admin:manage_users")
    @GetMapping("/confirm-delete")
    public String confirmDeleteUser(@RequestParam UUID userId, Model model) {
        User user = userService.findById(userId);
        model.addAttribute("userFirstName", user.getFirstName());
        model.addAttribute("userLastName", user.getLastName());

        model.addAttribute("userToDeleteId", userId);
        return "pages/confirm-delete-user";
    }

    @RequiresPermission("admin:manage_users")
    @PostMapping("/delete-user")
    public String deleteUser(@RequestParam UUID userId, HttpSession session) {
        Optional<UUID> currentUserId = userSessionService.getLoggedInUser(session);
        currentUserId.ifPresent(uuid -> userService.deleteById(userId, uuid));

        return "redirect:/manage-users";
    }

    private UserViewDto toUserViewDto(User user, Map<UUID, String> roleNames) {
        UUID id = user.getId();
        Profile profile = profileService.getProfileById(id);

        return UserViewDto.builder()
                .id(user.getId())
                .userName(profile.getUsername())
                .firstName(user.getFirstName())
                .prefix(user.getPrefix())
                .lastName(user.getLastName())
                .roleId(user.getRoleId())
                .roleName(user.getRoleId() != null ? roleNames.get(user.getRoleId()) : null)
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
