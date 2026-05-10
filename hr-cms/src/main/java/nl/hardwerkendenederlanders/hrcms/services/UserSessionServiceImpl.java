package nl.hardwerkendenederlanders.hrcms.services;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.springframework.stereotype.Service;

@Service
public class UserSessionServiceImpl implements UserSessionService {

    public void login(HttpSession session, User user) {
        session.setAttribute("userId", user.getId());
    }

    public Optional<UUID> getLoggedInUser(HttpSession session) {
        return Optional.ofNullable((UUID) session.getAttribute("userId"));
    }

    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}
