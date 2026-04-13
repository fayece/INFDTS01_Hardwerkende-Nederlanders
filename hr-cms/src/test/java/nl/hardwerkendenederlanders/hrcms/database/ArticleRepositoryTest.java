package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;
import java.util.UUID;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcArticleRepositoryImpl;
import nl.hardwerkendenederlanders.hrcms.models.Article;
import nl.hardwerkendenederlanders.hrcms.models.PublicationStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
public class ArticleRepositoryTest {

    @Autowired
    private JdbcArticleRepositoryImpl articleRepository;

    @Test
    void insertArticle_withValidArticle_shouldPersistAndRetrieve() {

        String title = "Test Insert Article";
        String textContent = "This article is meant as a test.";

        Article article =
                Article.builder().title(title).textContent(textContent).build();

        articleRepository.Create(article);
        Article retrieved = articleRepository.GetById(article.getId());

        assertNotNull(retrieved);
        assertEquals(article.getId(), retrieved.getId());
        assertEquals(title, retrieved.getTitle());
        assertEquals(textContent, retrieved.getTextContent());
    }

    @Test
    void updateArticle_withModifiedFields_shouldReflectChanges() {
        Article article = Article.builder()
                .title("Original Title")
                .textContent("Original content")
                .build();

        articleRepository.Create(article);

        article.setTitle("Updated Title");
        article.setTextContent("Updated content");
        article.setPublicationStatus(PublicationStatus.PUBLISHED);
        article.setUpdatedAt(OffsetDateTime.now());
        articleRepository.Update(article);

        Article retrieved = articleRepository.GetById(article.getId());
        assertEquals("Updated Title", retrieved.getTitle());
        assertEquals("Updated content", retrieved.getTextContent());
        assertEquals(PublicationStatus.PUBLISHED, retrieved.getPublicationStatus());
    }

    @Test
    void findArticleById_withExistingId_shouldReturnArticle() {
        Article article = Article.builder()
                .title("Find By ID Test")
                .textContent("Content for find by id test")
                .build();
        articleRepository.Create(article);

        Article retrieved = articleRepository.GetById(article.getId());

        assertNotNull(retrieved);
        assertEquals(article.getId(), retrieved.getId());
        assertEquals("Find By ID Test", retrieved.getTitle());
        assertEquals("Content for find by id test", retrieved.getTextContent());
    }

    @Test
    void findArticleById_withNonExistentId_shouldReturnNull() {
        assertNull(articleRepository.GetById(UUID.randomUUID()));
    }

    @Test
    void getAllArticles_withTwoArticles_shouldReturnArticles() {
        Article article1 = Article.builder()
                .title("Article 1")
                .textContent("it sure is an article")
                .build();
        articleRepository.Create(article1);
        Article article2 = Article.builder()
                .title("Article 2")
                .textContent("it sure is another article")
                .build();
        articleRepository.Create(article2);

        Article[] articles = articleRepository.GetAll();
        assertEquals("Article 1", articles[0].getTitle());
        assertEquals("Article 2", articles[1].getTitle());
    }

    @Test
    void addArticle_invalidTitleTooShort_shouldThrowSQLException() {
        // article too short -> invalid
        Article article = Article.builder()
                .title("")
                .textContent("sample text")
                .publicationStatus(PublicationStatus.PUBLISHED)
                .build();
        assertThrows(DataIntegrityViolationException.class, () -> articleRepository.Create(article));
    }

    @Test
    void addArticle_validTitleWhenInDraft_IsAddedToDb() {
        // an article without title is allowed when in draft
        Article article = Article.builder()
                .title("")
                .textContent("sample text")
                .publicationStatus(PublicationStatus.DRAFT)
                .build();
        assertDoesNotThrow(() -> articleRepository.Create(article));
    }

    @Test
    void addArticle_invalidTextContentTooShort_shouldThrowSQLException() {
        // text content too short -> invalid
        Article article = Article.builder()
                .title("really good title")
                .textContent("")
                .publicationStatus(PublicationStatus.PUBLISHED)
                .build();
        assertThrows(DataIntegrityViolationException.class, () -> articleRepository.Create(article));
    }
    //    @Test
    //    void deleteArticleById_withExistingArticle_shouldRemoveArticle() {
    //        Article article = Article.builder()
    //                .title("Delete Test Article")
    //                .textContent("This article will be deleted")
    //                .build();
    //        articleRepository.Create(article);
    //
    //        Article retrieved = articleRepository.GetById(article.getId());
    //        assertNotNull(retrieved);
    //
    //        articleRepository.delete(article.getId());
    //
    //        assertThrows(Exception.class, () -> articleRepository.GetById(article.getId()));
    //    }

    //    @Test
    //    void findAllArticlesPaged_withValidPaginationData_shouldReturnCorrectCount() {
    //        for (int i = 0; i < 15; i++) {
    //            Article article = Article.builder()
    //                    .title("Article " + i)
    //                    .textContent("Content for article " + i)
    //                    .build();
    //            articleRepository.Create(article);
    //        }
    //
    //        var page1 = articleRepository.findAllPaged(1, 10);
    //        var page2 = articleRepository.findAllPaged(2, 10);
    //
    //        assertEquals(10, page1.size());
    //        assertEquals(5, page2.size());
    //    }
    //
    //    static Stream<Arguments> invalidPaginationData() {
    //        return Stream.of(Arguments.of(0, 0), Arguments.of(1, 0), Arguments.of(1, -1), Arguments.of(0, -1));
    //    }
    //
    //    @ParameterizedTest
    //    @MethodSource("invalidPaginationData")
    //    void findAllArticlesPaged_withInvalidLimit_shouldThrowException(int offset, int limit) {
    //        assertThrows(IllegalArgumentException.class, () -> articleRepository.findAllPaged(offset, limit));
    //    }
}
