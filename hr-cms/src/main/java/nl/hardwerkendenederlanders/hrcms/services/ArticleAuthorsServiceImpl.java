package nl.hardwerkendenederlanders.hrcms.services;

import java.util.Arrays;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleAuthorRepository;
import nl.hardwerkendenederlanders.hrcms.database.mongodb.ProfileRepository;
import nl.hardwerkendenederlanders.hrcms.models.Profile;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.AuthorDto;
import nl.hardwerkendenederlanders.hrcms.services.interfaces.ArticleAuthorsService;
import org.springframework.stereotype.Service;

@Service
public class ArticleAuthorsServiceImpl implements ArticleAuthorsService {
    private final ArticleAuthorRepository authorRepository;
    private final ProfileRepository profileRepository;

    public ArticleAuthorsServiceImpl(
            ArticleAuthorRepository articleAuthorRepository, ProfileRepository profileRepository) {
        this.authorRepository = articleAuthorRepository;
        this.profileRepository = profileRepository;
    }

    public AuthorDto[] findAuthorsForArticle(UUID articleId) {
        return Arrays.stream(authorRepository.findAuthorsForArticle(articleId))
                .map(author -> {
                    String username = profileRepository
                            .findById(author.getUsername())
                            .map(Profile::getUsername)
                            .orElse("deleted_user");
                    return new AuthorDto(username);
                })
                .toArray(AuthorDto[]::new);
    }
}
