package nl.hardwerkendenederlanders.hrcms.services;

import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ConflictException;
import nl.hardwerkendenederlanders.hrcms.exceptions.DatabaseException;
import nl.hardwerkendenederlanders.hrcms.exceptions.NotFoundException;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UserService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void insertUser(User user){
        Optional<User> alreadyExists = userRepository.findByEmail(user.getEmail());
        if(alreadyExists.isPresent()) {
            throw new ConflictException("email address already taken");
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(user.getPasswordHash());
        user.setPasswordHash(hash);

        boolean inserted = userRepository.insert(user);
        if(!inserted) throw new DatabaseException("Insert failed");
    }

    public User findById(UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public List<User> findByName(String name){
        return userRepository.findByName(name);
    }

    public List<User> findAllUsers(){
        return userRepository.findAllUsers();
    }

    public void updateActivityById(UUID id, boolean setActive){
        boolean updated = userRepository.updateActivityById(id, setActive);
        if(!updated){
            throw new DatabaseException("Couldn't update activity");
        }
    }

    public void updateUser(User user){
        Optional<User> alreadyExists = userRepository.findByEmail(user.getEmail());
        if(alreadyExists.isPresent()) {
            throw new ConflictException("Email address already taken");
        }
        boolean updated = userRepository.update(user);
        if(!updated){
            throw new DatabaseException("Couldn't update user");
        }
    }

    public void deleteById(UUID id){
        boolean deleted = userRepository.deleteById(id);
        if(!deleted){
            throw new DatabaseException("Couldn't delete user");
        }
    }

    public List<User> findUsersOnActivity(boolean isActive){
        return userRepository.findUserOnActivity(isActive);
    }

    public List<User> findUsersPaginated(int page, int amount){
        return userRepository.findAllPaginated(page, amount);
    }

    public List<User> searchByNamePaginated(String name, int page, int amount){
        return userRepository.findByNamePaginated(name, page, amount);
    }

    public List<User> findUserOnActivityPaginated(boolean isActive, int page, int amount){
        return userRepository.findUserOnActivityPaginated(isActive, page, amount);
    }

}
