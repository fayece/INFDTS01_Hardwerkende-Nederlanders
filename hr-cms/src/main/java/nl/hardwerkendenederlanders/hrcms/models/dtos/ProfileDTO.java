package nl.hardwerkendenederlanders.hrcms.models.dtos;

import lombok.Data;
import nl.hardwerkendenederlanders.hrcms.models.Profile;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProfileDTO {
    private String username;
    private String bio;
    private List<String> interests = new ArrayList<>();
    private List<Profile.Pronoun> pronouns = new ArrayList<>();
    private List<Profile.CustomField> customFields = new ArrayList<>();
    private List<Profile.Social> socials = new ArrayList<>();
}
