package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import java.util.List;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.User;

public interface UserService {
    List<User> getUsers(int page, String searchName, Boolean sortActive);

    int getMaxPages(String searchName, Boolean sortActive);

    void insertUser(User user);

    String insertUser(String firstName, String prefix, String lastName, String password, UUID roleId);


    User findById(UUID id);

    List<User> findAllUsers();

    void updateActivityById(UUID id, boolean setActive);

    void updateUser(User user);

    void updateUser(
            UUID id, String firstName, String prefix, String lastName, UUID roleId, UUID organisationId);

    void deleteById(UUID id, UUID currentUserId);

    List<User> findUsersPaginated(int page, int amount);

    List<User> findUserOnActivityPaginated(boolean isActive, int page, int amount);

    List<User> searchByNamePaginated(String name, int page, int amount);

    int countByActive(boolean active);

    int countAll();

    int countByNamePaginated(String name);
}
