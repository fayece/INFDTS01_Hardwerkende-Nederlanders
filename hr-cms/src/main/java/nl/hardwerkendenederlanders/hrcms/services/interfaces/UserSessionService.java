package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import jakarta.servlet.http.HttpSession;
import nl.hardwerkendenederlanders.hrcms.models.User;

import java.util.UUID;

public interface UserSessionService {
    public void login(HttpSession session, User user);
    public UUID getLoggedInUser(HttpSession session);
    public boolean isLoggedIn(HttpSession session);
    public void logout(HttpSession session);
}
