package nl.hardwerkendenederlanders.hrcms.models.dtos;

import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import nl.hardwerkendenederlanders.hrcms.models.Profile;

@Data
public class ProfileDTO {
    @NotBlank(message = "Username is required")
    private String username;

    private String bio = "";
    private String profilePicture = "";
    private List<String> interests = new ArrayList<>();
    private List<String> pronouns = new ArrayList<>();
    private List<Profile.CustomField> customFields = new ArrayList<>(List.of(
            new Profile.CustomField(),
            new Profile.CustomField(),
            new Profile.CustomField(),
            new Profile.CustomField()));
    private List<Profile.Social> socials = new ArrayList<>(
            List.of(new Profile.Social(), new Profile.Social(), new Profile.Social(), new Profile.Social()));
}
