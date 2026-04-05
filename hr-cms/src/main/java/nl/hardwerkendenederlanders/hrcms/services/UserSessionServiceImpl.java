package nl.hardwerkendenederlanders.hrcms.services;

import jakarta.servlet.http.HttpSession;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserSessionService;
import org.springframework.stereotype.Service;

@Service
public class UserSessionServiceImpl implements UserSessionService {

    public void login(HttpSession session, User user) {
        session.setAttribute("userId", user.getId());
    }

    public UUID getLoggedInUser(HttpSession session) {
        var userId = session.getAttribute("userId");
        if (userId == null) throw new RuntimeException("Not logged in");
        return (UUID) userId;
    }

    public boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("userId") != null;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }
}
