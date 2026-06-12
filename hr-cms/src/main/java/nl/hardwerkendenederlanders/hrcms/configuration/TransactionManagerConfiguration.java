package nl.hardwerkendenederlanders.hrcms.configuration;

import javax.sql.DataSource;
import org.neo4j.driver.Driver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.neo4j.core.DatabaseSelectionProvider;
import org.springframework.data.neo4j.core.transaction.Neo4jTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;

@Configuration
public class TransactionManagerConfiguration {

    @Bean
    @Primary
    public JdbcTransactionManager jdbcTransactionManager(DataSource dataSource) {
        return new JdbcTransactionManager(dataSource);
    }

    @Bean
    public Neo4jTransactionManager neo4jTransactionManager(
            Driver driver, DatabaseSelectionProvider databaseSelectionProvider) {
        return new Neo4jTransactionManager(driver, databaseSelectionProvider);
    }
}