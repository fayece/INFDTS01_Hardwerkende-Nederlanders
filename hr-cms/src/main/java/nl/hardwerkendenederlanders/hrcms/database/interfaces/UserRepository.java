package nl.hardwerkendenederlanders.hrcms.database.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.User;

public interface UserRepository {
    boolean insert(User user);

    boolean update(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);
    List<User> findByName(String name);
    List<User> findUserOnActivity(boolean isActive);
    List<User> findAllUsers();
    List<User> findAllPaginated(Integer page, Integer amount);
    boolean updateActivityById(UUID id, boolean setActive);
    List<User> findByNamePaginated(String name, int page, int amount);

    boolean deleteById(UUID id);
}
