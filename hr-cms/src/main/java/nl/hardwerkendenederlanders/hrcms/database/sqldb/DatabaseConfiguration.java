package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import nl.hardwerkendenederlanders.hrcms.database.ArticleRepository;
import nl.hardwerkendenederlanders.hrcms.database.CommentRepository;
import nl.hardwerkendenederlanders.hrcms.database.MediaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class DatabaseConfiguration {

    @Bean
    public ArticleRepository articleRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        return new JdbcArticleRepository(jdbc);
    }

    @Bean
    public ArticleAuthorRepository ArticleAuthorRepository(
            NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        return new ArticleAuthorRepository(jdbc, resourceLoader);
    }

    @Bean
    public CommentRepository commentRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        return new JdbcCommentRepository(jdbc);
    }

    @Bean
    public MediaRepository mediaItemRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
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
}
