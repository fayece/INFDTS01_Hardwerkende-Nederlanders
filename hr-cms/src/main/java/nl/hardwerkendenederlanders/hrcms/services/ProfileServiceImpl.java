package nl.hardwerkendenederlanders.hrcms.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.mongodb.ProfileRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ConflictException;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.dtos.ProfileDTO;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ProfileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public Profile getProfileById(UUID id) {
        return profileRepository.findById(id.toString()).orElse(null);
    }

    public Profile getProfileByUsername(String username) {
        return profileRepository.findByUsername(username).orElse(null);
    }

    public void setProfile(UUID id, ProfileDTO profileDTO, MultipartFile file) {
        validateProfile(id, profileDTO);

        String profilePicture;
        if (file != null && !file.isEmpty()) {
            profilePicture = saveProfilePicture(id, file);
        } else {
            Profile existing = getProfileById(id);
            profilePicture = existing != null ? existing.getProfilePicture() : null;
        }

        Profile profile = Profile.builder()
                .id(id.toString())
                .bio(profileDTO.getBio())
                .username(profileDTO.getUsername())
                .pronouns(profileDTO.getPronouns())
                .customFields(profileDTO.getCustomFields())
                .interests(profileDTO.getInterests())
                .socials(profileDTO.getSocials())
                .profilePicture(profilePicture)
                .build();

        profile.getCustomFields()
                .removeIf(f -> f.getLabel() == null || f.getLabel().isBlank());
        profile.getSocials().removeIf(s -> s.getType() == null || s.getType().isBlank());
        profile.getPronouns().removeIf(p -> p == null || p.isBlank());
        profile.getInterests().removeIf(i -> i == null || i.isBlank());

        profileRepository.save(profile);
    }

    private String saveProfilePicture(UUID id, MultipartFile file) {
        try {
            String filename = id.toString() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads");

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Files.copy(file.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save profile picture", e);
        }
    }

    private void validateProfile(UUID id, ProfileDTO profileDTO) {
        if (profileDTO.getUsername().isBlank()) throw new IllegalArgumentException("Username cannot be blank");

        var alreadyExists = getProfileByUsername(profileDTO.getUsername());
        if (alreadyExists != null && !alreadyExists.getId().equals(id.toString()))
            throw new ConflictException("username already taken");

        if (profileDTO.getCustomFields().size() > 4)
            throw new IllegalArgumentException("Only 4 custom fields allowed. Delete one first or simply edit one");
        if (profileDTO.getSocials().size() > 4)
            throw new IllegalArgumentException(
                    "Only 4 social media fields allowed. Delete one first or simply edit one");
        if (profileDTO.getPronouns().size() > 4)
            throw new IllegalArgumentException("Only 4 pronouns allowed. Delete one first or simply edit one");
        if (profileDTO.getInterests().size() > 10)
            throw new IllegalArgumentException("Only 10 interests allowed. Delete one first or simply edit one");
    }

    public void deleteById(UUID id) {
        profileRepository.deleteById(id.toString());
    }

    public ProfileDTO toDTO(Profile profile) {
        ProfileDTO dto = new ProfileDTO();
        dto.setUsername(profile.getUsername());
        dto.setBio(profile.getBio() != null ? profile.getBio() : "");

        List<String> interests = new ArrayList<>(profile.getInterests());
        while (interests.size() < 6) interests.add("");
        dto.setInterests(interests);

        List<String> pronouns = new ArrayList<>(profile.getPronouns());
        while (pronouns.size() < 4) pronouns.add("");
        dto.setPronouns(pronouns);

        List<Profile.CustomField> fields = new ArrayList<>(profile.getCustomFields());
        while (fields.size() < 4) fields.add(new Profile.CustomField());
        dto.setCustomFields(fields);

        List<Profile.Social> socials = new ArrayList<>(profile.getSocials());
        while (socials.size() < 4) socials.add(new Profile.Social());
        dto.setSocials(socials);

        return dto;
    }
}
