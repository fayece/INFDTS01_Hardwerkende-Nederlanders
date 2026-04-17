package nl.hardwerkendenederlanders.hrcms.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ConflictException;
import nl.hardwerkendenederlanders.hrcms.exceptions.NotFoundException;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserService;
import org.jspecify.annotations.NonNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void insertUser(@NonNull User user) {
        Optional<User> alreadyExists = userRepository.findByEmail(user.getEmail());
        if (alreadyExists.isPresent()) {
            throw new ConflictException("email address already taken");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(user.getPasswordHash());
        user.setPasswordHash(hash);

        userRepository.insert(user);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    @Override
    public void updateActivityById(UUID id, boolean setActive) {
        userRepository.updateActivityById(id, setActive);
    }

    @Override
    @Transactional
    public void updateUser(@NonNull User user) {
        Optional<User> alreadyExists = userRepository.findByEmail(user.getEmail());
        if (alreadyExists.isPresent() && alreadyExists.get().getId() != user.getId()) {
            throw new ConflictException("Email address already taken");
        }
        userRepository.update(user);
    }

    @Override
    public void deleteById(UUID id, UUID currentUserId) {
        if (id == currentUserId) {
            throw new ConflictException("Cannot delete your own account when logged in");
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<User> findUsersPaginated(int page, int amount) {
        return userRepository.findAllPaginated(page, amount);
    }

    @Override
    public List<User> searchByNamePaginated(String name, int page, int amount) {
        return userRepository.findByNameOrEmailPaginated(name, page, amount);
    }

    @Override
    public List<User> findUserOnActivityPaginated(boolean isActive, int page, int amount) {
        return userRepository.findUserOnActivityPaginated(isActive, page, amount);
    }

    @Override
    public int countByActive(boolean active) {
        return userRepository.countByActive(active);
    }

    @Override
    public int countAll() {
        return userRepository.countAll();
    }

    @Override
    public int countByNameOrEmailPaginated(String name) {
        return userRepository.countByNameOrEmailPaginated(name);
    }
}
