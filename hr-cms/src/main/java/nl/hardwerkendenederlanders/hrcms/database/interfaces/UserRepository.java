package nl.hardwerkendenederlanders.hrcms.database.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.User;

public interface UserRepository {
    void insert(User user);

    void update(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    List<User> findAllUsers();

    List<User> findAllPaginated(Integer page, Integer amount);

    void updateActivityById(UUID id, boolean setActive);

    List<User> findByNameOrEmailPaginated(String name, int page, int amount);

    List<User> findUserOnActivityPaginated(boolean isActive, int page, int amount);

    void deleteById(UUID id);

    int countByActive(boolean active);

    int countAll();

    int countByNameOrEmailPaginated(String name);
}
