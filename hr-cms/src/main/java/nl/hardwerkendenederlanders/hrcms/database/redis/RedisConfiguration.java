package nl.hardwerkendenederlanders.hrcms.database.redis;

import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import redis.clients.jedis.*;

import java.time.Duration;

@Configuration
public class RedisConfiguration {

    @Value("${spring.cache.password}")
    private String password;
    @Value("${spring.cache.host}")
    private String host;
    @Value("${spring.cache.port}")
    private int port;

    private static @Nullable RedisClient client;

    private void makeClient(){
        ConnectionPoolConfig poolConfig = new ConnectionPoolConfig();
        poolConfig.setMaxTotal(4);

        poolConfig.setMaxIdle(4);
        poolConfig.setMinIdle(1);

        poolConfig.setBlockWhenExhausted(true);
        poolConfig.setMaxWait(Duration.ofSeconds(1));

        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(2));

        JedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
                .password(password)
                .build();

        client = RedisClient.builder()
                .hostAndPort(host, port)
                .poolConfig(poolConfig)
                .clientConfig(clientConfig)
                .build();
    }

    @Bean
    public RedisClient jedis() {
        if (client == null){
            makeClient();
        }
        return client;
    }
}
