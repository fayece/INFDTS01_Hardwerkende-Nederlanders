package nl.hardwerkendenederlanders.hrcms.database.cache.redis;

import nl.hardwerkendenederlanders.hrcms.database.cache.interfaces.ArticleCache;
import nl.hardwerkendenederlanders.hrcms.models.dtos.article.ArticleFullDetailsDto;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.json.Path2;

import java.util.UUID;

@Service
public class RedisArticle implements ArticleCache {

    private final String name = "fullArticle:";
    private final RedisClient jedis;

    public RedisArticle(RedisClient jedis) {
        this.jedis = jedis;
    }

    public void insertFullArticle(ArticleFullDetailsDto fullArticle) {
        jedis.jsonSet(name + fullArticle.getId() , fullArticle);

    }

    public @Nullable ArticleFullDetailsDto findFullArticle(UUID id) {
        var result = jedis.jsonGet(name + id);

        if (result == null){
            return null;
        }
        else{
            return (ArticleFullDetailsDto) result;
        }

    }

    public void IncrementViewForArticle(UUID articleId) {
        jedis.jsonNumIncrBy(name + articleId, new Path2("viewCount"), 1);
    }

    public void IncrementCommentForArticle(UUID articleId) {
        jedis.jsonNumIncrBy(name + articleId, new Path2("commentCount"), 1);
    }
}
