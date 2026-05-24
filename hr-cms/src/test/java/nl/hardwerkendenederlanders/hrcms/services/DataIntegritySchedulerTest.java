package nl.hardwerkendenederlanders.hrcms.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.database.mongodb.ProfileRepository;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcIntegrityLogRepository;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.schedulers.DataIntegrityScheduler;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.UsernameGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class DataIntegritySchedulerTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ProfileRepository profileRepository = mock(ProfileRepository.class);
    private final JdbcIntegrityLogRepository jdbcIntegrityLogRepository = mock(JdbcIntegrityLogRepository.class);
    private final UsernameGeneratorService usernameGeneratorService = mock(UsernameGeneratorService.class);

    private final DataIntegrityScheduler scheduler = new DataIntegrityScheduler();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(scheduler, "userRepository", userRepository);
        ReflectionTestUtils.setField(scheduler, "profileRepository", profileRepository);
        ReflectionTestUtils.setField(scheduler, "jdbcIntegrityLogRepository", jdbcIntegrityLogRepository);
        ReflectionTestUtils.setField(scheduler, "usernameGeneratorService", usernameGeneratorService);
    }

    @Test
    void checkUsersWithoutProfile_userWithoutProfile_createsProfile() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());
        when(userRepository.findAllUsers()).thenReturn(List.of(user));
        when(profileRepository.findById(any())).thenReturn(Optional.empty());
        when(usernameGeneratorService.generateUniqueUsername()).thenReturn("swift_frog42");

        scheduler.checkUsersWithoutProfile();

        verify(profileRepository).save(any(Profile.class));
        verify(jdbcIntegrityLogRepository).save(any());
    }

    @Test
    void checkUsersWithoutProfile_userWithProfile_doesNothing() {
        User user = mock(User.class);
        when(user.getId()).thenReturn(UUID.randomUUID());
        when(userRepository.findAllUsers()).thenReturn(List.of(user));
        when(profileRepository.findById(any()))
                .thenReturn(Optional.of(Profile.builder()
                        .id(UUID.randomUUID().toString())
                        .username("existing_user")
                        .build()));

        scheduler.checkUsersWithoutProfile();

        verify(profileRepository, never()).save(any());
        verify(jdbcIntegrityLogRepository, never()).save(any());
    }

    @Test
    void checkProfilesWithoutUser_profileWithoutUser_deletesProfile() {
        Profile profile = Profile.builder()
                .id(UUID.randomUUID().toString())
                .username("some_user")
                .build();

        when(profileRepository.findAll()).thenReturn(List.of(profile));
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        scheduler.checkProfilesWithoutUser();

        verify(profileRepository).deleteById(profile.getId());
        verify(jdbcIntegrityLogRepository).save(any());
    }

    @Test
    void checkProfilesWithoutUser_profileWithUser_doesNothing() {
        Profile profile = Profile.builder()
                .id(UUID.randomUUID().toString())
                .username("some_user")
                .build();

        when(profileRepository.findAll()).thenReturn(List.of(profile));
        when(userRepository.findById(any())).thenReturn(Optional.of(mock(User.class)));

        scheduler.checkProfilesWithoutUser();

        verify(profileRepository, never()).deleteById(any());
        verify(jdbcIntegrityLogRepository, never()).save(any());
    }
}
