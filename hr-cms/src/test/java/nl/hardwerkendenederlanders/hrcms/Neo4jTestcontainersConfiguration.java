package nl.hardwerkendenederlanders.hrcms;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration(proxyBeanMethods = false)
public class Neo4jTestcontainersConfiguration {

    @Bean
    @ServiceConnection
    public Neo4jContainer<?> neo4jContainer() {
        return new Neo4jContainer<>(DockerImageName.parse("neo4j:2026.04.0-trixie")).withoutAuthentication();
    }
}
