package nl.hardwerkendenederlanders.hrcms.models;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
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
    private String profilePicture;

    @Nullable
    private String bio;

    private List<String> interests = new ArrayList<>();
    private List<String> pronouns = new ArrayList<>();
    private List<CustomField> customFields = new ArrayList<>();
    private List<Social> socials = new ArrayList<>();


    @Data
    public static class Social{
        private String type;
        private String username;
    }

    @Data
    public static class CustomField {
        private String label;
        private String value;
    }


}
