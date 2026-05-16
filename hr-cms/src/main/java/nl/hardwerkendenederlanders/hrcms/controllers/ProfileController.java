package nl.hardwerkendenederlanders.hrcms.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import nl.hardwerkendenederlanders.hrcms.exceptions.ConflictException;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.models.dtos.ProfileDTO;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ProfileService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Controller
public class ProfileController {
    private final ProfileService profileService;
    private final UserSessionService userSessionService;

    public ProfileController(ProfileService profileService, UserSessionService userSessionService){
        this.profileService = profileService;
        this.userSessionService = userSessionService;
    }

    @GetMapping("/my-profile")
    public String getProfilePageOfCurrentUser(Model model, HttpSession session){
        Optional<UUID> userId = userSessionService.getLoggedInUser(session);
        if(userId.isEmpty())
            return "pages/login";

        Profile profile = profileService.getProfileById(userId.get());

        model.addAttribute("editable", true);
        model.addAttribute("profile", profile);

        return "pages/profile-page";
    }

    @GetMapping("/profile/{username}")
    public String getProfilePageOfOtherUser(@PathVariable String username, Model model){
        Profile profile = profileService.getProfileByUsername(username);

        model.addAttribute("editable", false);
        model.addAttribute("profile", profile);

        return "pages/profile-page";
    }

    @PostMapping("/create-username")
    public String createUsername(@Valid @ModelAttribute ProfileDTO profileDTO, BindingResult result, Model model, HttpSession session){
        if (result.hasErrors())
            return "pages/create-username";

        Optional<UUID> userId = userSessionService.getLoggedInUser(session);
        if(userId.isEmpty())
            return "pages/login";

        try{
            profileService.setProfile((userId.get()), profileDTO);
            return "redirect:/my-profile";
        }catch(ConflictException e){
            result.rejectValue("username", "error.username", "Username is already taken");
            return "pages/create-username";
        }
    }

    @GetMapping("/my-profile-edit")
    public String editProfile(Model model, HttpSession session){
        Optional<UUID> userId = userSessionService.getLoggedInUser(session);
        if(userId.isEmpty())
            return "pages/login";

        Profile profile = profileService.getProfileById(userId.get());
        model.addAttribute("profileDTO", profileService.toDTO(profile));

        return "pages/edit-profile";
    }

    @PostMapping("/my-profile/update")
    public String updateProfile(@Valid @ModelAttribute ProfileDTO profileDTO, BindingResult result, HttpSession session)
    {
        System.out.println(profileDTO.getUsername());
        System.out.println(profileDTO.getBio());
        System.out.println(profileDTO.getInterests());
        System.out.println(profileDTO.getPronouns());

        if (result.hasErrors()){
            result.getAllErrors().forEach(e -> System.out.println(e));
            return "pages/edit-profile";}

        Optional<UUID> userId = userSessionService.getLoggedInUser(session);
        if(userId.isEmpty())
            return "pages/login";

        try{
            profileService.setProfile(userId.get(), profileDTO);
            return "redirect:/my-profile";
        } catch (ConflictException e) {
        result.rejectValue("username", "error.username", "Username is already taken");
        return "pages/edit-profile";
    } catch (Exception e) {
        System.out.println("ERROR: " + e.getMessage());
        return "pages/edit-profile";
    }}

    @GetMapping("/create-new-profile")
    public String createUsernamePage(Model model)
    {
        model.addAttribute("profileDTO", new ProfileDTO());
        return "pages/create-username";
    }
}
