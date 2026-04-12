package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import nl.hardwerkendenederlanders.hrcms.models.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void insertUser(User user);
    User findById(UUID id);
    User findByEmail(String email);
    List<User> findByName(String name);
    List<User> findAllUsers();
    void updateActivityById(UUID id, boolean setActive);
    void updateUser(User user);
    void deleteById(UUID id);
    List<User> findUsersOnActivity(boolean isActive);
    List<User> findUsersPaginated(int page, int amount);
    List<User> searchByNamePaginated(String name, int page, int amount);
}
