package nl.hardwerkendenederlanders.hrcms.database.sqldb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class DatabaseConfiguration {

    @Bean
    public ArticleRepository articleRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        return new ArticleRepository(jdbc, resourceLoader);
    }

    @Bean
    public ArticleAuthorRepository ArticleAuthorRepository(
            NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        return new ArticleAuthorRepository(jdbc, resourceLoader);
    }

    @Bean
    public CommentRepository commentRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        return new CommentRepository(jdbc, resourceLoader);
    }

    @Bean
    public MediaItemRepository mediaItemRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        return new MediaItemRepository(jdbc, resourceLoader);
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
    public JdbcUserRepository userRepository(NamedParameterJdbcTemplate jdbc, ResourceLoader resourceLoader) {
        return new JdbcUserRepository(jdbc);
    }
}
