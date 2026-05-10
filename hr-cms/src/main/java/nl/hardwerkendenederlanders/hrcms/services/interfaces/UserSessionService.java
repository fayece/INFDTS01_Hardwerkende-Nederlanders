package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import jakarta.servlet.http.HttpSession;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.User;

public interface UserSessionService {
    void login(HttpSession session, User user);

    Optional<UUID> getLoggedInUser(HttpSession session);

    boolean isLoggedIn(HttpSession session);

    void logout(HttpSession session);
}
