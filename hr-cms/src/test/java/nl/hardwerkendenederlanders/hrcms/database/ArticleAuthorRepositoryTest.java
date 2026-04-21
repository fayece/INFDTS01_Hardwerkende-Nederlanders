package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleAuthorRepository;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.*;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.ArticleAuthor;
import nl.hardwerkendenederlanders.hrcms.models.Role;
import nl.hardwerkendenederlanders.hrcms.models.User;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.AuthorDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
public class ArticleAuthorRepositoryTest {

    @Autowired
    private ArticleAuthorRepository articleAuthorRepository;

    @Autowired
    private JdbcArticleRepository articleRepository;

    @Autowired
    private JdbcUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Article article;
    private User author;

    @BeforeEach
    void setUp() {
        article = Article.builder()
                .title("Test Article")
                .textContent("Test content.")
                .build();

        articleRepository.insert(article);

        Role role = new Role("Author Role");

        roleRepository.insert(role);

        author = User.builder()
                .firstName("Test")
                .lastName("Author")
                .email(UUID.randomUUID() + "@example.com")
                .passwordHash("R@ndomP4ssw0rd1!@x")
                .roleId(role.getId())
                .build();
        userRepository.insert(author);
    }

    @Test
    void insertArticleAuthor_withValidArticleAuthor_shouldPersistAndRetrieve() {
        ArticleAuthor articleAuthor = new ArticleAuthor(article.getId(), author.getId());

        articleAuthorRepository.ensureInsert(articleAuthor);
        AuthorDto[] retrieved =
                articleAuthorRepository.findAuthorsForArticle(article.getId());

        assertNotNull(retrieved);
        assertEquals("Test Author", retrieved[0].getFullName());
    }

    @Test
    void findArticleAuthorById_withNonExistentId_shouldReturnEmptyArray() {
        AuthorDto[] result = articleAuthorRepository.findAuthorsForArticle(UUID.randomUUID());
        assertEquals(0, result.length);
    }

    @Test
    void findAllArticleAuthorsPaged_withValidPaginationData_shouldReturnCorrectCount() {
        for (int i = 0; i < 15; i++) {
            User newAuthor = User.builder()
                    .firstName("Numbered Arthur " + i)
                    .lastName("Test")
                    .email("author" + i + "@example.com")
                    .passwordHash("R@ndomP4ssw0rd1!@x")
                    .roleId(author.getRoleId())
                    .build();

            userRepository.insert(newAuthor);

            articleAuthorRepository.ensureInsert(new ArticleAuthor(article.getId(), newAuthor.getId()));
        }

        AuthorDto[] authors = articleAuthorRepository.findAuthorsForArticle(article.getId());

        assertEquals(15, authors.length);
        assertEquals("Numbered Arthur 14 Test", authors[14].getFullName());
    }
}
