package nl.hardwerkendenederlanders.hrcms.models;

import com.mongodb.lang.NonNull;
import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "profiles")
@Data
@Builder
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

    private List<String> interests;
    private List<String> pronouns;
    private List<CustomField> customFields;
    private List<Social> socials;

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
