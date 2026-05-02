package nl.hardwerkendenederlanders.hrcms.database.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfiguration {

    @Bean
    public RedisArticles redisArticles(){
        return new RedisArticles();
    }
}
