package nl.hardwerkendenederlanders.hrcms.database.interfaces;

import java.util.*;
import nl.hardwerkendenederlanders.hrcms.models.User;

public interface UserRepository {
    void insert(User user);

    void update(User user);

    Optional<User> findById(UUID id);

    List<User> findAllUsers();

    List<User> findAllPaginated(Integer page, Integer amount);

    void updateActivityById(UUID id, boolean setActive);

    List<User> findByNamePaginated(String name, int page, int amount);

    List<User> findUserOnActivityPaginated(boolean isActive, int page, int amount);

    void deleteById(UUID id);

    Integer countByActive(boolean active);

    Integer countAll();

    Integer countByNamePaginated(String name);
}
