package nl.hardwerkendenederlanders.hrcms.services;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.database.mongodb.ProfileRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ConflictException;
import nl.hardwerkendenederlanders.hrcms.exceptions.NotFoundException;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserService;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UsernameGeneratorService;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static nl.hardwerkendenederlanders.hrcms.services.UsernameGeneratorServiceImpl.generateUsername;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    private final UsernameGeneratorService usernameGeneratorService;

    private final int pageSize = 13;

    public UserServiceImpl(UserRepository userRepository, ProfileRepository profileRepository, UsernameGeneratorService usernameGeneratorService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.usernameGeneratorService = usernameGeneratorService;
    }

    public List<User> getUsers(int page, String searchName, Boolean sortActive) {
        if (searchName != null) {
            return userRepository.findByNameOrEmailPaginated(searchName, page, pageSize);
        } else if (sortActive != null) {
            return userRepository.findUserOnActivityPaginated(sortActive, page, pageSize);
        }
        return userRepository.findAllPaginated(page, pageSize);
    }

    public int getMaxPages(String searchName, Boolean sortActive) {
        if (searchName != null) {
            return (int) Math.ceil(((double) countByNameOrEmailPaginated(searchName)) / pageSize);
        } else if (sortActive != null) {
            return (int) Math.ceil(((double) countByActive(sortActive)) / pageSize);
        }
        return (int) Math.ceil(((double) countAll()) / pageSize);
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
    @Transactional
    public void insertUser(String firstName, String prefix, String lastName, String email, String password, UUID roleId) {
        User toInsert = new User(
                UUID.randomUUID(),
                firstName,
                prefix,
                lastName,
                email,
                password,
                roleId,
                null,
                true,
                OffsetDateTime.now());

        validateUserAttributes(toInsert);
        insertUser(toInsert);

        //Profile username creation
        String username = usernameGeneratorService.generateUniqueUsername();
        Profile profile = new Profile();
        profile.setUsername(username);
        profileRepository.save(profile);
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
        if (alreadyExists.isPresent()
                && !java.util.Objects.equals(alreadyExists.get().getId(), user.getId())) {
            throw new ConflictException("Email address already taken");
        }
        userRepository.update(user);
    }

    @Override
    @Transactional
    public void updateUser(
            UUID id, String firstName, String prefix, String lastName, String email, UUID roleId, UUID organisationId) {
        User currentUser = findById(id);
        if (firstName != null) {
            currentUser.setFirstName(firstName);
        }
        if (prefix != null) {
            currentUser.setPrefix(prefix);
        }
        if (lastName != null) {
            currentUser.setLastName(lastName);
        }
        if (email != null) {
            currentUser.setEmail(email);
        }
        if (roleId != null) {
            currentUser.setRoleId(roleId);
        }
        if (organisationId != null) {
            currentUser.setOrganizationId(organisationId);
        }

        validateUserAttributes(currentUser);

        updateUser(currentUser);
    }

    @Override
    @Transactional
    public void deleteById(UUID id, UUID currentUserId) {
        if (id == currentUserId) {
            throw new ConflictException("Cannot delete your own account when logged in");
        }
        userRepository.deleteById(id);
        profileRepository.deleteById(id.toString());
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

    public void validateUserAttributes(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user is null");
        }

        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String email = user.getEmail();

        if (user.getId() == null) {
            throw new IllegalArgumentException("user id is required");
        }
        if (firstName == null || firstName.isBlank() || firstName.length() < 2) {
            throw new IllegalArgumentException("first name should be at least 2 characters long");
        }
        if (lastName == null || lastName.isBlank() || lastName.length() < 2) {
            throw new IllegalArgumentException("last name should be at least 2 characters long");
        }
        if (email == null || email.isBlank() || !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("invalid email address");
        }
    }
}
