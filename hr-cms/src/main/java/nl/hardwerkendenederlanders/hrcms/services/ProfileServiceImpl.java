package nl.hardwerkendenederlanders.hrcms.services;

import nl.hardwerkendenederlanders.hrcms.database.mongodb.ProfileRepository;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.dtos.ProfileDTO;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ProfileService;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository){
        this.profileRepository = profileRepository;
    }

    public void setProfile(UUID id, ProfileDTO profileDTO){
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
}
