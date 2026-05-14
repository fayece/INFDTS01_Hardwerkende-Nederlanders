//package nl.hardwerkendenederlanders.hrcms.database;
//
//import java.sql.SQLException;
//import java.util.UUID;
//import nl.hardwerkendenederlanders.hrcms.TestcontainersConfiguration;
//import nl.hardwerkendenederlanders.hrcms.database.interfaces.ArticleRepository;
//import nl.hardwerkendenederlanders.hrcms.database.interfaces.CommentRepository;
//import nl.hardwerkendenederlanders.hrcms.database.interfaces.MediaRepository;
//import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
//import nl.hardwerkendenederlanders.hrcms.database.sqldb.JdbcRoleRepository;
//import nl.hardwerkendenederlanders.hrcms.models.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.test.jdbc.JdbcTestUtils;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@Import(TestcontainersConfiguration.class)
//@Transactional
//public class JdbcCommentRepositoryTest extends CommentRepositoryContractTest {
//
//    @Autowired
//    private CommentRepository commentRepository;
//
//    @Autowired
//    private MediaRepository mediaRepository;
//
//    @Autowired
//    private JdbcRoleRepository jdbcRoleRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ArticleRepository articleRepository;
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//    private UUID articleId;
//    private UUID authorId;
//    private UUID mediaId;
//
//    @BeforeEach
//    void setUp() {
//        clearCommentsTable();
//    }
//
//    @BeforeEach
//    void setupDatabasePrerequisites() throws SQLException {
//        Article article = Article.builder()
//                .title("Test Article")
//                .textContent("Test content.")
//                .build();
//        articleRepository.insert(article);
//        this.articleId = article.getId();
//
//        String uniqueRoleName = "Author Role " + UUID.randomUUID();
//        Role role = Role.of(uniqueRoleName).build();
//        jdbcRoleRepository.insert(role);
//
//        User author = User.builder()
//                .firstName("Test")
//                .lastName("Author")
//                .email(UUID.randomUUID() + "@example.com")
//                .passwordHash("R@ndomP4ssw0rd1!@x")
//                .roleId(role.getId())
//                .build();
//        userRepository.insert(author);
//        this.authorId = author.getId();
//
//        MediaItem mediaItem = MediaItem.builder()
//                .url("https://example.com/image" + UUID.randomUUID() + ".jpg")
//                .mediaType(MediaType.IMAGE)
//                .build();
//        mediaRepository.insert(mediaItem);
//        this.mediaId = mediaItem.getId();
//    }
//
//    @Override
//    protected CommentRepository getRepository() {
//        return this.commentRepository;
//    }
//
//    @Override
//    protected UUID getValidArticleId() {
//        return this.articleId;
//    }
//
//    @Override
//    protected UUID getValidAuthorId() {
//        return this.authorId;
//    }
//
//    @Override
//    protected UUID getValidMediaId() {
//        return this.mediaId;
//    }
//
//    @Override
//    protected void clearCommentsTable() {
//        JdbcTestUtils.deleteFromTables(jdbcTemplate, "comments");
//    }
//}
