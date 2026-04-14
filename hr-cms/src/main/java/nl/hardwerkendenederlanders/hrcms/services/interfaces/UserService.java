package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import java.util.List;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.User;

public interface UserService {
    void insertUser(User user);

    User findByEmail(String email);

    User findById(UUID id);

    List<User> findAllUsers();

    void updateActivityById(UUID id, boolean setActive);

    void updateUser(User user);

    void deleteById(UUID id);

    List<User> findUsersPaginated(int page, int amount);

    List<User> findUserOnActivityPaginated(boolean isActive, int page, int amount);

    List<User> searchByNamePaginated(String name, int page, int amount);

    int countByActive(boolean active);

    int countAll();

    int countByNameOrEmailPaginated(String name);
}
