package nl.hardwerkendenederlanders.hrcms.database;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import java.util.stream.Stream;
import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
import nl.hardwerkendenederlanders.hrcms.database.sqldb.*;
import nl.hardwerkendenederlanders.hrcms.models.*;
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
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MediaItemRepository mediaItemRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JdbcUserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    private Article article;
    private User author;
    private MediaItem mediaItem;

    @BeforeEach
    void setUp() {
        article = Article.builder()
                .title("Test Article")
                .textContent("Test content.")
                .build();
        articleRepository.Create(article);

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

        mediaItem = MediaItem.builder()
                .url("https://example.com/image" + UUID.randomUUID() + ".jpg")
                .mediaType(MediaType.IMAGE)
                .build();
        mediaItemRepository.insert(mediaItem);
    }

    @Test
    void insertComment_withValidComment_shouldPersistAndRetrieve() {
        Comment comment = Comment.builder()
                .articleId(article.getId())
                .creatorId(author.getId())
                .commentBody("This is a test comment.")
                .mediaId(mediaItem.getId())
                .build();

        commentRepository.insert(comment);
        Comment retrieved = commentRepository.findById(comment.getId());

        assertNotNull(retrieved);
        assertEquals(comment.getId(), retrieved.getId());
        assertEquals(article.getId(), retrieved.getArticleId());
        assertEquals(author.getId(), retrieved.getCreatorId());
        assertEquals("This is a test comment.", retrieved.getCommentBody());
        assertEquals(mediaItem.getId(), retrieved.getMediaId());
    }

    @Test
    void insertComment_replyToExistingComment_shouldPersistWithParentCommentId() {
        Comment parentComment = Comment.builder()
                .articleId(article.getId())
                .creatorId(author.getId())
                .commentBody("This is a parent comment.")
                .mediaId(mediaItem.getId())
                .build();
        commentRepository.insert(parentComment);

        Comment replyComment = Comment.builder()
                .articleId(article.getId())
                .creatorId(author.getId())
                .commentBody("This is a reply to the parent comment.")
                .mediaId(mediaItem.getId())
                .parentCommentId(parentComment.getId())
                .build();
        commentRepository.insert(replyComment);

        Comment thirdLevelComment = Comment.builder()
                .articleId(article.getId())
                .creatorId(author.getId())
                .commentBody("This is a reply to the reply comment.")
                .mediaId(mediaItem.getId())
                .parentCommentId(replyComment.getId())
                .build();
        commentRepository.insert(thirdLevelComment);

        Comment retrievedReply = commentRepository.findById(replyComment.getId());
        Comment retrievedThirdLevel = commentRepository.findById(thirdLevelComment.getId());

        assertNotNull(retrievedReply);
        assertNotNull(retrievedThirdLevel);
        assertEquals(parentComment.getId(), retrievedReply.getParentCommentId());
        assertEquals(replyComment.getId(), retrievedThirdLevel.getParentCommentId());
    }

    @Test
    void updateComment_withModifiedFields_shouldReflectChanges() {
        Comment comment = Comment.builder()
                .articleId(article.getId())
                .creatorId(author.getId())
                .commentBody("This is a test comment.")
                .mediaId(mediaItem.getId())
                .build();
        commentRepository.insert(comment);

        comment.setCommentBody("This is an updated test comment.");
        commentRepository.update(comment);

        Comment retrieved = commentRepository.findById(comment.getId());
        assertNotNull(retrieved);
        assertEquals("This is an updated test comment.", retrieved.getCommentBody());
    }

    @Test
    void findCommentById_withExistingId_shouldReturnComment() {
        Comment comment = Comment.builder()
                .articleId(article.getId())
                .creatorId(author.getId())
                .commentBody("This is a test comment.")
                .mediaId(mediaItem.getId())
                .build();
        commentRepository.insert(comment);

        Comment retrieved = commentRepository.findById(comment.getId());

        assertNotNull(retrieved);
        assertEquals(comment.getId(), retrieved.getId());
        assertEquals(article.getId(), retrieved.getArticleId());
        assertEquals(author.getId(), retrieved.getCreatorId());
        assertEquals("This is a test comment.", retrieved.getCommentBody());
        assertEquals(mediaItem.getId(), retrieved.getMediaId());
    }

    @Test
    void findCommentById_withNonExistingId_shouldThrowException() {
        assertThrows(RuntimeException.class, () -> commentRepository.findById(UUID.randomUUID()));
    }

    @Test
    void deleteCommentById_withExistingComment_shouldRemoveComment() {
        Comment comment = Comment.builder()
                .articleId(article.getId())
                .creatorId(author.getId())
                .commentBody("This is a test comment.")
                .mediaId(mediaItem.getId())
                .build();
        commentRepository.insert(comment);

        commentRepository.delete(comment.getId());

        // Since comments are soft-deleted, we can still retrieve it, and deletedAt should have been set
        Comment retrieved = commentRepository.findById(comment.getId());
        assertNotNull(retrieved);
        assertNotNull(retrieved.getDeletedAt());
    }

    @Test
    void findAllCommentsPaged_withMultipleComments_shouldReturnPagedResults() {
        for (int i = 0; i < 15; i++) {
            Comment comment = Comment.builder()
                    .articleId(article.getId())
                    .creatorId(author.getId())
                    .commentBody("Comment " + i)
                    .mediaId(mediaItem.getId())
                    .build();
            commentRepository.insert(comment);
        }

        var page1 = commentRepository.findAllPaged(1, 10);
        var page2 = commentRepository.findAllPaged(2, 10);

        assertEquals(10, page1.size());
        assertEquals(5, page2.size());
    }

    static Stream<Arguments> invalidPaginationData() {
        return Stream.of(Arguments.of(0, 0), Arguments.of(1, 0), Arguments.of(1, -1), Arguments.of(0, -1));
    }

    @ParameterizedTest
    @MethodSource("invalidPaginationData")
    void findAllCommentsPaged_withInvalidLimit_shouldThrowException(int page, int size) {
        assertThrows(RuntimeException.class, () -> commentRepository.findAllPaged(page, size));
    }
}
