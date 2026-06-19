package nl.hardwerkendenederlanders.hrcms.models.dtos.article;

import lombok.Getter;

@Getter
public class AuthorDto {
    private final String username;

    public AuthorDto(String username) {
        this.username = username;
    }
}
