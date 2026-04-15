package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import nl.hardwerkendenederlanders.hrcms.database.ArticleRepository;
import nl.hardwerkendenederlanders.hrcms.database.SubjectRepository;
import nl.hardwerkendenederlanders.hrcms.database.interfaces.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class DatabaseConfiguration {

    @Bean
    public ArticleRepository articleRepository(NamedParameterJdbcTemplate jdbc) {
        return new JdbcArticleRepository(jdbc);
    }

    @Bean
    public ArticleAuthorRepository ArticleAuthorRepository(
            NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        return new ArticleAuthorRepository(jdbc, resourceLoader);
    }

    @Bean
    public JdbcCommentRepository commentRepository(NamedParameterJdbcTemplate jdbc) {
        return new JdbcCommentRepository(jdbc);
    }

    @Bean
    public JdbcMediaRepository mediaItemRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        return new JdbcMediaRepository(jdbc);
    }

    @Bean
    public OrganizationRepository organizationRepository(
            NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        return new OrganizationRepository(jdbc, resourceLoader);
    }

    @Bean
    public PermissionRepository permissionRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        return new PermissionRepository(jdbc, resourceLoader);
    }

    @Bean
    public RoleRepository roleRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        return new RoleRepository(jdbc, resourceLoader);
    }

    @Bean
    public RolePermissionRepository rolePermissionRepository(
            NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        return new RolePermissionRepository(jdbc, resourceLoader);
    }

    @Bean
    public UserRepository userRepository(NamedParameterJdbcTemplate jdbc) {
        return new JdbcUserRepository(jdbc);
    }

    @Bean
    public SubjectRepository subjectRepository(NamedParameterJdbcTemplate jdbc) {
        return new JdbcSubjectRepository(jdbc);
    }
}
