package nl.hardwerkendenederlanders.hrcms.controllers;

import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.OffsetDateTime;
import java.util.UUID;

@Controller
public class RegisterController {
    //  THIS WAS COMPLETELY MADE BY AI TO BE ABLE TO TEST OTHER THINGS

    private final UserRepository userRepository;

    public RegisterController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "pages/register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String firstName,
            @RequestParam(required = false) String prefix,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String password
    ) {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());

        User user = new User(
                UUID.randomUUID(),
                firstName,
                prefix,
                lastName,
                email,
                hash,
                null,
                null,
                true,
                OffsetDateTime.now()
        );

        userRepository.insert(user);

        return "redirect:/login";
    }
}