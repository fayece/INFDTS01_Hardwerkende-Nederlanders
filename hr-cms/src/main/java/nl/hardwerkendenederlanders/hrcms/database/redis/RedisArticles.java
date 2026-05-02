package nl.hardwerkendenederlanders.hrcms.database.redis;

import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.RedisClient;

public class RedisArticles {


    public void insert(String key, String value) {
        Jedis jedis = new Jedis("redis://:radijs@localhost:6033");
        jedis.set(key, value);
        jedis.close();
    }

    public String find(String key) {
        Jedis jedis = new Jedis("redis://:radijs@localhost:6033");
        var result = jedis.get(key);
        jedis.close();
        return result;
    }
}
