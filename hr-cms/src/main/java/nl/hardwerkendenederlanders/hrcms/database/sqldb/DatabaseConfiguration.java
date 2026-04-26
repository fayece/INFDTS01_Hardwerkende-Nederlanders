package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class DatabaseConfiguration {

    @Bean
    public JdbcArticleRepository articleRepository(NamedParameterJdbcTemplate jdbc) {
        return new JdbcArticleRepository(jdbc);
    }

    @Bean
    public ArticleAuthorRepository ArticleAuthorRepository(
            NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        return new ArticleAuthorRepository(jdbc, resourceLoader);
    }

    @Bean
    public JdbcCommentRepository JdbcCommentRepository(NamedParameterJdbcTemplate jdbc) {
        return new JdbcCommentRepository(jdbc);
    }

    @Bean
    public JdbcMediaRepository JdbcMediaRepository(NamedParameterJdbcTemplate jdbc) {
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
