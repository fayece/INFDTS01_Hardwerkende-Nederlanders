package nl.hardwerkendenederlanders.hrcms.schedulers;

import lombok.extern.slf4j.Slf4j;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.database.mongodb.ProfileRepository;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcIntegrityLogRepository;
import nl.hardwerkendenederlanders.hrcms.models.LoggingEntity;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UsernameGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Component
@Slf4j
public class DataIntegrityScheduler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private JdbcIntegrityLogRepository jdbcIntegrityLogRepository;

    @Autowired
    private UsernameGeneratorService usernameGeneratorService;

    @Scheduled(fixedRate = 3600000) // elk uur
    public void checkUsersWithoutProfile() {
        List<User> users = userRepository.findAllUsers();

        for (User user : users) {
            Optional<Profile> profile = profileRepository.findById(user.getId().toString());
            if (profile.isEmpty()) {
                log.warn("Data integrity problem: User {} doesn't have a profile", user.getId());

                jdbcIntegrityLogRepository.save(LoggingEntity.builder()
                        .message("User " + user.getId() + " didn't have a profile, one was created")
                        .severity("WARN")
                        .timestamp(OffsetDateTime.now())
                        .build()
                );

                String username = usernameGeneratorService.generateUniqueUsername();

                Profile newProfile = new Profile();
                newProfile.setId(user.getId().toString());
                newProfile.setUsername(username);
                profileRepository.save(newProfile);
            }
        }
    }

    @Scheduled(fixedRate = 3600000)
    public void checkProfilesWithoutUser() {
        List<Profile> profiles = profileRepository.findAll();

        for (Profile profile : profiles) {
            Optional<User> user = userRepository.findById(UUID.fromString(profile.getId()));
            if (user.isEmpty()) {
                log.warn("Data integrity problem: Profile {} doesn't have a user", profile.getId());

                jdbcIntegrityLogRepository.save(LoggingEntity.builder()
                        .message("Profile " + profile.getId() + " didn't have a user, profile was deleted")
                        .severity("WARN")
                        .timestamp(OffsetDateTime.now())
                        .build()
                );

                profileRepository.deleteById(profile.getId());
            }
        }
    }
}
