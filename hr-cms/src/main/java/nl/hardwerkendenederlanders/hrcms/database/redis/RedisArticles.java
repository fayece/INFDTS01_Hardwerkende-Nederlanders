package nl.hardwerkendenederlanders.hrcms.database.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.RedisClient;
import redis.clients.jedis.*;

@Service
public class RedisArticles {

    private final RedisClient jedis;

    public RedisArticles(RedisClient jedis) {
        this.jedis = jedis;
    }

    public void insert(String key, String value) {

        jedis.set(key, value);

    }

    public String find(String key) {
        var result = jedis.get(key);
        return result;
    }
}
