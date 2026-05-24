package nl.hardwerkendenederlanders.hrcms.models;

import com.mongodb.lang.NonNull;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "profiles")
@Data
public class Profile {

    @NonNull
    @Id
    String id; // the UUID from user saved as a string

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
    public static class Social {
        private String type;
        private String username;
    }

    @Data
    public static class CustomField {
        private String label;
        private String value;
    }
}
