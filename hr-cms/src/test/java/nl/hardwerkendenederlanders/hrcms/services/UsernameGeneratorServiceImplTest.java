package nl.hardwerkendenederlanders.hrcms.services;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import nl.hardwerkendenederlanders.hrcms.database.mongodb.ProfileRepository;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import org.junit.jupiter.api.Test;

public class UsernameGeneratorServiceImplTest {

    private final ProfileRepository profileRepository = mock(ProfileRepository.class);
    private final UsernameGeneratorServiceImpl usernameGeneratorService =
            new UsernameGeneratorServiceImpl(profileRepository);

    @Test
    void generateUniqueUsername_noConflict_returnsUsername() {
        when(profileRepository.findByUsername(any())).thenReturn(Optional.empty());

        String result = usernameGeneratorService.generateUniqueUsername();

        assertNotNull(result);
        assertFalse(result.isBlank());
        verify(profileRepository, atLeastOnce()).findByUsername(any());
    }

    @Test
    void generateUniqueUsername_firstConflict_retriesAndReturnsUsername() {
        when(profileRepository.findByUsername(any()))
                .thenReturn(Optional.of(new Profile()))
                .thenReturn(Optional.empty());

        String result = usernameGeneratorService.generateUniqueUsername();

        assertNotNull(result);
        verify(profileRepository, times(2)).findByUsername(any());
    }
}
