package nl.hardwerkendenederlanders.hrcms.models.dtos.article;

import org.jetbrains.annotations.Nullable;

public record AuthorDto(String firstName, @Nullable String prefix, String lastName) {
    public String getFullName() {
        if (prefix == null || prefix.trim().isEmpty()) {
            return firstName + " " + lastName;
        }
        return firstName + " " + prefix + " " + lastName;
    }
}
