package nl.hardwerkendenederlanders.hrcms.database.cache.redis;

import nl.hardwerkendenederlanders.hrcms.database.cache.interfaces.ArticleCache;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.ArticleFullDetailsDto;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.json.Path2;

import java.util.UUID;

@Service

public class RedisArticle implements ArticleCache {

    private final String name = "fullArticle:";
    private final RedisTemplate<String, Object> jedis;

    public RedisArticle(RedisTemplate<String, Object> jedis) {
        this.jedis = jedis;
    }
//
//    public RedisArticle(RedisTemplate<String, Object> jedis) {
//        this.jedis = jedis;
//    }

    public void insertFullArticle(ArticleFullDetailsDto fullArticle) {
        jedis.opsForValue().set(name + fullArticle.getId() , fullArticle);

    }

    public @Nullable ArticleFullDetailsDto findFullArticle(UUID id) {

        ArticleFullDetailsDto result = (ArticleFullDetailsDto) jedis.opsForValue().get(name + id);
        System.out.println(result);
        return result;

    }

    public void IncrementViewForArticle(UUID articleId) {
//        jedis.jsonNumIncrBy(name + articleId, new Path2("viewCount"), 1);
    }

    public void IncrementCommentForArticle(UUID articleId) {
//        jedis.jsonNumIncrBy(name + articleId, new Path2("commentCount"), 1);
    }
}
