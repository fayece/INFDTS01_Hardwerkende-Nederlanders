package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import nl.hardwerkendenederlanders.hrcms.models.User;

public interface AuthService {
    public User login(String email, String password);
}
