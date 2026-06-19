package nl.hardwerkendenederlanders.hrcms.services.interfaces;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.dtos.ProfileDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
    void setProfile(UUID id, ProfileDTO profileDTO, MultipartFile file);

    Profile getProfileById(UUID id);

    Profile getProfileByUsername(String username);

    void deleteById(UUID id);

    ProfileDTO toDTO(Profile profile);
}
