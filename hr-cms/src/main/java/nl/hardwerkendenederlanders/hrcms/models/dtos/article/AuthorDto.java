package nl.hardwerkendenederlanders.hrcms.models.dtos.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.hardwerkendenederlanders.hrcms.models.commonalities.FullName;
import org.jetbrains.annotations.Nullable;

@Getter
public class AuthorDto {
    private final String username;

    public AuthorDto(String username) {
        this.username = username;
    }
}