package nl.hardwerkendenederlanders.hrcms.models.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import nl.hardwerkendenederlanders.hrcms.models.Profile;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProfileDTO {
    @NotBlank(message = "Username is required")
    private String username;
    private String bio = "";
    private List<String> interests = new ArrayList<>();
    private List<Profile.Pronoun> pronouns = new ArrayList<>();
    private List<Profile.CustomField> customFields = new ArrayList<>(List.of(
            new Profile.CustomField(), new Profile.CustomField(), new Profile.CustomField(), new Profile.CustomField()));
    private List<Profile.Social> socials = new ArrayList<>(List.of(new Profile.Social(), new Profile.Social(),
            new Profile.Social(), new Profile.Social()));
}
