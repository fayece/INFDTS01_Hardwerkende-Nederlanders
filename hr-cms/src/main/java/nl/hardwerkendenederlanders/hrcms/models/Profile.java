package nl.hardwerkendenederlanders.hrcms.models;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Document(collection = "profiles")
@Data
public class Profile {

    @Id
    String id; //the UUID from user saved as a string

    @NotBlank
    private String username;

    @Nullable
    private String bio;

    private List<String> interests;
    private List<Pronoun> pronouns;
    private List<CustomField> customFields;
    private List<Social> socials;


    @Data
    public static class Social{
        private int id;
        @NotBlank
        private String type;
        @NotBlank
        private String username;
    }

    @Data
    public static class Pronoun{
        private int id;
        @NotBlank
        private String value;
    }

    @Data
    public static class CustomField {
        private int id;
        @NotBlank
        private String label;
        @NotNull
        private String value;
    }


}
