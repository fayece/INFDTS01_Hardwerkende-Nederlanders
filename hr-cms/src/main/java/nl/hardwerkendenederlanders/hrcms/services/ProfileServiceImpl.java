package nl.hardwerkendenederlanders.hrcms.services;

import nl.hardwerkendenederlanders.hrcms.database.mongodb.ProfileRepository;
import nl.hardwerkendenederlanders.hrcms.exceptions.ConflictException;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.dtos.ProfileDTO;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ProfileService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository){
        this.profileRepository = profileRepository;
    }

    public void setProfile(UUID id, ProfileDTO profileDTO){
        if(profileDTO.getUsername().isBlank())
            throw new IllegalArgumentException("Username cannot be blank");

        var alreadyExists = getProfileByUsername(profileDTO.getUsername());
        if(alreadyExists != null && !alreadyExists.getId().equals(id.toString()))
            throw new ConflictException("username already taken");

        if(profileDTO.getCustomFields().size() > 4)
            throw new IllegalArgumentException("Only 4 custom fields allowed. Delete one first or simply edit one");
        if(profileDTO.getSocials().size() > 4)
            throw new IllegalArgumentException("Only 4 social media fields allowed. Delete one first or simply edit one");
        if(profileDTO.getPronouns().size() > 4)
            throw new IllegalArgumentException("Only 4 pronouns allowed. Delete one first or simply edit one");
        if(profileDTO.getInterests().size() > 10)
            throw new IllegalArgumentException("Only 10 interests allowed. Delete one first or simply edit one");

        Profile profile = new Profile();
        profile.setId(id.toString());
        profile.setBio(profileDTO.getBio());
        profile.setUsername(profileDTO.getUsername());
        profile.setPronouns(profileDTO.getPronouns());
        profile.setCustomFields(profileDTO.getCustomFields());
        profile.setInterests(profileDTO.getInterests());
        profile.setSocials(profileDTO.getSocials());

        profile.getCustomFields().removeIf(f -> f.getLabel() == null || f.getLabel().isBlank());
        profile.getSocials().removeIf(s -> s.getType() == null || s.getType().isBlank());
        profile.getPronouns().removeIf(p -> p == null || p.isBlank());
        profile.getInterests().removeIf(i -> i == null || i.isBlank());

        profileRepository.save(profile);
    }

    public Profile getProfileById(UUID id){
        return profileRepository.findById(id.toString()).orElse(null);
    }

    public Profile getProfileByUsername(String username){
        return profileRepository.findByUsername(username).orElse(null);
    }

    public void deleteById(UUID id){
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
