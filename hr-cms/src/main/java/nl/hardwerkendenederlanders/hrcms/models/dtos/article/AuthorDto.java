package nl.hardwerkendenederlanders.hrcms.models.dtos.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import nl.hardwerkendenederlanders.hrcms.models.dtos.commonalities.FullName;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class AuthorDto implements FullName {
    private String firstName;
    private @Nullable String prefix;
    private String lastName;
}
