package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.*;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.ArticleAuthor;
import nl.hardwerkendenederlanders.hrcms.models.Role;
import nl.hardwerkendenederlanders.hrcms.models.User;
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
    private ArticleRepository articleRepository;

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

        articleAuthorRepository.insert(articleAuthor);
        ArticleAuthor retrieved =
                articleAuthorRepository.findById(articleAuthor.getId()).orElse(null);

        assertNotNull(retrieved);
        assertEquals(articleAuthor.getId(), retrieved.getId());
        assertEquals(article.getId(), retrieved.getArticleId());
        assertEquals(author.getId(), retrieved.getAuthorId());
    }

    @Test
    void findArticleAuthorById_withExistingId_shouldReturnArticleAuthor() {
        ArticleAuthor articleAuthor = new ArticleAuthor(article.getId(), author.getId());

        articleAuthorRepository.insert(articleAuthor);
        ArticleAuthor retrieved =
                articleAuthorRepository.findById(articleAuthor.getId()).orElse(null);

        assertNotNull(retrieved);
        assertEquals(articleAuthor.getId(), retrieved.getId());
        assertEquals(articleAuthor.getArticleId(), retrieved.getArticleId());
        assertEquals(articleAuthor.getAuthorId(), retrieved.getAuthorId());
    }

    @Test
    void findArticleAuthorById_withNonExistentId_shouldReturnEmptyOptional() {
        Optional<ArticleAuthor> result = articleAuthorRepository.findById(UUID.randomUUID());
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteArticleAuthorById_withExistingArticleAuthor_shouldRemoveArticleAuthor() {
        ArticleAuthor articleAuthor = new ArticleAuthor(article.getId(), author.getId());

        articleAuthorRepository.insert(articleAuthor);
        articleAuthorRepository.delete(articleAuthor.getId());

        Optional<ArticleAuthor> result = articleAuthorRepository.findById(articleAuthor.getId());
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllArticleAuthorsPaged_withValidPaginationData_shouldReturnCorrectCount() {
        for (int i = 0; i < 15; i++) {
            User newAuthor = User.builder()
                    .firstName("Author " + i)
                    .lastName("Test")
                    .email("author" + i + "@example.com")
                    .passwordHash("R@ndomP4ssw0rd1!@x")
                    .roleId(author.getRoleId())
                    .build();

            userRepository.insert(newAuthor);

            articleAuthorRepository.insert(new ArticleAuthor(article.getId(), newAuthor.getId()));
        }

        var page1 = articleAuthorRepository.findAllPaged(1, 10);
        var page2 = articleAuthorRepository.findAllPaged(2, 10);

        assertEquals(10, page1.size());
        assertEquals(5, page2.size());
    }

    static Stream<Arguments> invalidPaginationData() {
        return Stream.of(Arguments.of(0, 0), Arguments.of(1, 0), Arguments.of(1, -1), Arguments.of(0, -1));
    }

    @ParameterizedTest
    @MethodSource("invalidPaginationData")
    void findAllArticleAuthorsPaged_withInvalidLimit_shouldThrowException(int offset, int limit) {
        assertThrows(IllegalArgumentException.class, () -> articleAuthorRepository.findAllPaged(offset, limit));
    }
}
