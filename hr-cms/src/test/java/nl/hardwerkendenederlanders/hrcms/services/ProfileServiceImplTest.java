package nl.hardwerkendenederlanders.hrcms.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.mongodb.ProfileRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ConflictException;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.dtos.ProfileDTO;
import org.junit.jupiter.api.Test;

class ProfileServiceImplTest {

    private final ProfileRepository profileRepository = mock(ProfileRepository.class);
    private final ProfileServiceImpl profileService = new ProfileServiceImpl(profileRepository);

    @Test
    void getProfileById_success_returnsProfile() {
        UUID id = UUID.randomUUID();
        Profile profile =
                Profile.builder().id(id.toString()).username("swiftfrog42").build();

        when(profileRepository.findById(id.toString())).thenReturn(Optional.of(profile));

        Profile result = profileService.getProfileById(id);

        assertEquals(profile, result);
    }

    @Test
    void getProfileById_notFound_returnsNull() {
        UUID id = UUID.randomUUID();

        when(profileRepository.findById(id.toString())).thenReturn(Optional.empty());

        Profile result = profileService.getProfileById(id);

        assertNull(result);
    }

    @Test
    void getProfileByUsername_success_returnsProfile() {
        Profile profile = Profile.builder()
                .id(UUID.randomUUID().toString())
                .username("swiftfrog42")
                .build();

        when(profileRepository.findByUsername("swiftfrog42")).thenReturn(Optional.of(profile));

        Profile result = profileService.getProfileByUsername("swiftfrog42");

        assertEquals(profile, result);
    }

    @Test
    void getProfileByUsername_notFound_returnsNull() {
        when(profileRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        Profile result = profileService.getProfileByUsername("unknown");

        assertNull(result);
    }

    @Test
    void setProfile_success_savesProfile() {
        UUID id = UUID.randomUUID();
        ProfileDTO dto = new ProfileDTO();
        dto.setUsername("swiftfrog42");
        dto.setBio("hello");
        dto.setInterests(new ArrayList<>());
        dto.setPronouns(new ArrayList<>());
        dto.setCustomFields(new ArrayList<>());
        dto.setSocials(new ArrayList<>());

        when(profileRepository.findByUsername("swiftfrog42")).thenReturn(Optional.empty());

        profileService.setProfile(id, dto, null);

        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void setProfile_usernameTaken_throwsConflictException() {
        UUID id = UUID.randomUUID();
        UUID otherId = UUID.randomUUID();

        ProfileDTO dto = new ProfileDTO();
        dto.setUsername("taken");
        dto.setInterests(new ArrayList<>());
        dto.setPronouns(new ArrayList<>());
        dto.setCustomFields(new ArrayList<>());
        dto.setSocials(new ArrayList<>());

        Profile existing =
                Profile.builder().id(otherId.toString()).username("taken").build();

        when(profileRepository.findByUsername("taken")).thenReturn(Optional.of(existing));

        assertThrows(ConflictException.class, () -> profileService.setProfile(id, dto, null));
        verify(profileRepository, never()).save(any());
    }

    @Test
    void setProfile_blankUsername_throwsIllegalArgumentException() {
        UUID id = UUID.randomUUID();
        ProfileDTO dto = new ProfileDTO();
        dto.setUsername("");
        dto.setInterests(new ArrayList<>());
        dto.setPronouns(new ArrayList<>());
        dto.setCustomFields(new ArrayList<>());
        dto.setSocials(new ArrayList<>());

        assertThrows(IllegalArgumentException.class, () -> profileService.setProfile(id, dto, null));
        verify(profileRepository, never()).save(any());
    }

    @Test
    void setProfile_tooManyCustomFields_throwsIllegalArgumentException() {
        UUID id = UUID.randomUUID();
        ProfileDTO dto = new ProfileDTO();
        dto.setUsername("swiftfrog42");
        dto.setInterests(new ArrayList<>());
        dto.setPronouns(new ArrayList<>());
        dto.setSocials(new ArrayList<>());
        dto.setCustomFields(List.of(
                new Profile.CustomField(),
                new Profile.CustomField(),
                new Profile.CustomField(),
                new Profile.CustomField(),
                new Profile.CustomField()));

        when(profileRepository.findByUsername("swiftfrog42")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> profileService.setProfile(id, dto, null));
        verify(profileRepository, never()).save(any());
    }

    @Test
    void deleteById_success_callsRepository() {
        UUID id = UUID.randomUUID();

        profileService.deleteById(id);

        verify(profileRepository).deleteById(id.toString());
    }

    @Test
    void toDTO_success_returnsDTO() {
        Profile profile = Profile.builder()
                .id(UUID.randomUUID().toString())
                .username("swiftfrog42")
                .bio("hello")
                .interests(new ArrayList<>())
                .pronouns(new ArrayList<>())
                .customFields(new ArrayList<>())
                .socials(new ArrayList<>())
                .build();

        ProfileDTO result = profileService.toDTO(profile);

        assertEquals("swiftfrog42", result.getUsername());
        assertEquals("hello", result.getBio());
        assertEquals(6, result.getInterests().size());
        assertEquals(4, result.getPronouns().size());
        assertEquals(4, result.getCustomFields().size());
        assertEquals(4, result.getSocials().size());
    }
}
