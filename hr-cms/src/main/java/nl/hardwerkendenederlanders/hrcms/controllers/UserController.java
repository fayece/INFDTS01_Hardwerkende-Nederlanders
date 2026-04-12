package nl.hardwerkendenederlanders.hrcms.controllers;

import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("manage-users")
public class UserController {
    private final UserService userService;
    private final int pageSize = 20;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping()
    public String manageUserPage(@RequestParam(defaultValue = "0") Integer page , Model model, @RequestParam(required = false) String searchName){
        List<User> users;

        if(searchName != null){
            users = userService.searchByNamePaginated(searchName, page, pageSize);
            model.addAttribute("searchName", searchName);
        }
        else{
            users = userService.findUsersPaginated(page, pageSize);
        }
        Integer maxPages = (int) Math.ceil(((double)((userService.findAllUsers()).size()) / pageSize));

        model.addAttribute("users", users);
        model.addAttribute("currentPage", page);
        model.addAttribute("finalPage", maxPages);

        return "pages/manage-users";
    }

    @GetMapping("/create-user")
    public String createUserPage(){
        return "pages/create-user";
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable UUID id, Model model){
        User user = userService.findById(id);
        if(user == null){
            return "redirect:/manage-users";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("currentUserId", id);
        return "pages/edit-user";
    }

    @PostMapping("/new")
    public String createNewUser(@RequestParam String firstName,
                                @RequestParam String prefix,
                                @RequestParam String lastName,
                                @RequestParam String email,
                                @RequestParam String password,
                                Model model
    ){
        User toInsert = new User(UUID.randomUUID(), firstName, prefix, lastName, email, password, null, null, true, OffsetDateTime.now());
        userService.insertUser(toInsert);
        model.addAttribute("inserted", true);
        return "pages/create-user";
    }

    @PostMapping("/update")
    public String updateUser(
            @RequestParam UUID id,
            @RequestParam(required = false) String prefix,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UUID roleId,
            @RequestParam(required = false) UUID organisationId
            , Model model){
        User currentUser = userService.findById(id);

        if(prefix != null){
            currentUser.setPrefix(prefix);
        }
        if(firstName != null){
            currentUser.setFirstName(firstName);
        }
        if(lastName != null){
            currentUser.setLastName(lastName);
        }
        if(email != null){
            currentUser.setEmail(email);
        }
        if(roleId != null){
            currentUser.setRoleId(roleId);
        }
        if(organisationId != null){
            currentUser.setOrganizationId(organisationId);
        }

        userService.updateUser(currentUser);
        return "redirect:/manage-users"; //or a successpage -> manage-users
    }



//    @GetMapping("/all")
//    public String getAllUsers(){
//        List<User> users = userService.findAllUsers();
//
//    }

//    @GetMapping("/search")
//    public String searchUserByName(@RequestParam String name, @RequestParam(defaultValue="0") Integer page, Model model){
//        List<User> users = userService.searchByNamePaginated(name.strip(), page, pageSize);
//
//        model.addAttribute("currentPage", page);
//        model.addAttribute("users", users);
//
//        return "pages/manage-users";
//    }

    @PostMapping("/set-active")
    public String changeActiveStatus(@RequestParam UUID userId, @RequestParam boolean setActive, Model model){
        userService.updateActivityById(userId, setActive);

        return "redirect:/manage-users";
    }


}
