package nl.hardwerkendenederlanders.hrcms.database.interfaces;

import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.User;

public interface UserRepository {
    void insert(User user);

    void update(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    void deleteById(UUID id);
}
