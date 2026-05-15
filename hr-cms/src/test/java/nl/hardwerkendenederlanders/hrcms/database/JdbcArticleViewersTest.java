package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleViewersRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.ArticleViewer;
import nl.hardwerkendenederlanders.hrcms.models.PublicationStatus;
import nl.hardwerkendenederlanders.hrcms.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

@SpringBootTest
public class JdbcArticleViewersTest {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleViewersRepository viewersRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Article getDefaultArticle() {
        return Article.builder()
                .title("titalus")
                .textContent("body")
                .publicationStatus(PublicationStatus.PUBLISHED)
                .build();
    }

    private User getDefaultUser() {
        return User.builder()
                .firstName("Siegfried")
                .prefix("Von")
                .lastName("Höhefelden")
                .email(UUID.randomUUID() + "" + UUID.randomUUID() + "@" + UUID.randomUUID() + UUID.randomUUID()
                        + ".com")
                .passwordHash("pwwasswoert")
                .active(true)
                .build();
    }

    @BeforeEach
    void setUp() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "article_viewers");
    }

    @Test
    void findArticlePublished_viewerViews_viewAdded() {
        var validDefaultArticle = getDefaultArticle();
        var validDefaultUser = getDefaultUser();
        articleRepository.insert(validDefaultArticle);
        userRepository.insert(validDefaultUser);
        ArticleViewer viewer = ArticleViewer.builder()
                .articleId(validDefaultArticle.getId())
                .viewerId(validDefaultUser.getId())
                .build();
        viewersRepository.ensureInsert(viewer);

        var fullArticle = articleRepository.findArticlePublished(validDefaultArticle.getId());
        assertEquals(1, fullArticle.getViewCount());
    }

    @Test
    void findArticlePublished_noViewers_expect0ViewCount() {
        var validDefaultArticle = getDefaultArticle();
        var validDefaultUser = getDefaultUser();
        articleRepository.insert(validDefaultArticle);
        var fullArticle = articleRepository.findArticlePublished(validDefaultArticle.getId());
        assertEquals(0, fullArticle.getViewCount());
    }

    @Test
    void findArticlePublished_10viewers_expectViewCountOf10() {
        var validDefaultArticle = getDefaultArticle();
        articleRepository.insert(validDefaultArticle);
        for (int i = 0; i < 5; i++) {
            User validDefaultUser = User.builder()
                    .firstName("inmate")
                    .prefix("nr.")
                    .lastName(String.valueOf(i))
                    .email(String.valueOf(i) + "@bijlmerbajes.nl")
                    .passwordHash("strenge beveiliging")
                    .active(true)
                    .build();
            userRepository.insert(validDefaultUser);

            ArticleViewer viewer = ArticleViewer.builder()
                    .articleId(validDefaultArticle.getId())
                    .viewerId(validDefaultUser.getId())
                    .build();
            viewersRepository.ensureInsert(viewer);
        }

        var fullArticle = articleRepository.findArticlePublished(validDefaultArticle.getId());
        assertEquals(5, fullArticle.getViewCount());
    }

    @Test
    void findArticlePublished_sameUserViewsTwice_OneView() {
        var validDefaultArticle = getDefaultArticle();
        var validDefaultUser = getDefaultUser();
        articleRepository.insert(validDefaultArticle);
        userRepository.insert(validDefaultUser);
        ArticleViewer viewer1 = ArticleViewer.builder()
                .articleId(validDefaultArticle.getId())
                .viewerId(validDefaultUser.getId())
                .build();
        ArticleViewer viewer2 = ArticleViewer.builder()
                .articleId(validDefaultArticle.getId())
                .viewerId(validDefaultUser.getId())
                .build();
        viewersRepository.ensureInsert(viewer2);

        var fullArticle = articleRepository.findArticlePublished(validDefaultArticle.getId());
        assertEquals(1, fullArticle.getViewCount());
    }
}
