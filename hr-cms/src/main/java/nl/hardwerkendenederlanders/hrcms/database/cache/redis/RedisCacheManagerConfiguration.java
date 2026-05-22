package nl.hardwerkendenederlanders.hrcms.database.cache.redis;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.*;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

@Configuration
public class RedisCacheManagerConfiguration {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        JsonMapper objectMapper = JsonMapper.builder()
                .activateDefaultTyping(
                        BasicPolymorphicTypeValidator.builder()
                                .allowIfSubType(Object.class)
                                .allowIfBaseType("nl.hardwerkendenederlanders.hrcms")
                                .build(),
                        DefaultTyping.NON_FINAL)
                .build();

        GenericJacksonJsonRedisSerializer serializer = new GenericJacksonJsonRedisSerializer(objectMapper);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer)))
                .transactionAware()
                .build();
    }
}
