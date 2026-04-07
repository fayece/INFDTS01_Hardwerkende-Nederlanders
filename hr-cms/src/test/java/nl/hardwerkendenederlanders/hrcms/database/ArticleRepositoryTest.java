//package nl.hardwerkendenederlanders.hrcms.database;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.time.OffsetDateTime;
//import java.util.UUID;
//import java.util.stream.Stream;
//import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
//import nl.hardwerkendenederlanders.hrcms.database.sqldb.ArticleRepositoryImpl;
//import nl.hardwerkendenederlanders.hrcms.models.Article;
//import nl.hardwerkendenederlanders.hrcms.models.PublicationStatus;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@Import(TestcontainersConfiguration.class)
//@Transactional
//public class ArticleRepositoryTest {
//
//    @Autowired
//    private ArticleRepositoryImpl articleRepository;
//
//    @Test
//    void insertArticle_withValidArticle_shouldPersistAndRetrieve() {
//
//        String title = "Test Insert Article";
//        String textContent = "This article is meant as a test.";
//
//        Article article =
//                Article.builder().title(title).textContent(textContent).build();
//
//        articleRepository.insert(article);
//        Article retrieved = articleRepository.findById(article.getId());
//
//        assertNotNull(retrieved);
//        assertEquals(article.getId(), retrieved.getId());
//        assertEquals(title, retrieved.getTitle());
//        assertEquals(textContent, retrieved.getTextContent());
//    }
//
//    @Test
//    void updateArticle_withModifiedFields_shouldReflectChanges() {
//        Article article = Article.builder()
//                .title("Original Title")
//                .textContent("Original content")
//                .build();
//
//        articleRepository.insert(article);
//
//        article.setTitle("Updated Title");
//        article.setTextContent("Updated content");
//        article.setPublicationStatus(PublicationStatus.PUBLISHED);
//        article.setUpdatedAt(OffsetDateTime.now());
//        articleRepository.update(article);
//
//        Article retrieved = articleRepository.findById(article.getId());
//        assertEquals("Updated Title", retrieved.getTitle());
//        assertEquals("Updated content", retrieved.getTextContent());
//        assertEquals(PublicationStatus.PUBLISHED, retrieved.getPublicationStatus());
//    }
//
//    @Test
//    void findArticleById_withExistingId_shouldReturnArticle() {
//        Article article = Article.builder()
//                .title("Find By ID Test")
//                .textContent("Content for find by id test")
//                .build();
//        articleRepository.insert(article);
//
//        Article retrieved = articleRepository.findById(article.getId());
//
//        assertNotNull(retrieved);
//        assertEquals(article.getId(), retrieved.getId());
//        assertEquals("Find By ID Test", retrieved.getTitle());
//        assertEquals("Content for find by id test", retrieved.getTextContent());
//    }
//
//    @Test
//    void findArticleById_withNonExistentId_shouldThrowException() {
//        assertThrows(Exception.class, () -> articleRepository.findById(UUID.randomUUID()));
//    }
//
//    @Test
//    void deleteArticleById_withExistingArticle_shouldRemoveArticle() {
//        Article article = Article.builder()
//                .title("Delete Test Article")
//                .textContent("This article will be deleted")
//                .build();
//        articleRepository.insert(article);
//
//        Article retrieved = articleRepository.findById(article.getId());
//        assertNotNull(retrieved);
//
//        articleRepository.delete(article.getId());
//
//        assertThrows(Exception.class, () -> articleRepository.findById(article.getId()));
//    }
//
//    @Test
//    void findAllArticlesPaged_withValidPaginationData_shouldReturnCorrectCount() {
//        for (int i = 0; i < 15; i++) {
//            Article article = Article.builder()
//                    .title("Article " + i)
//                    .textContent("Content for article " + i)
//                    .build();
//            articleRepository.insert(article);
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
//}
