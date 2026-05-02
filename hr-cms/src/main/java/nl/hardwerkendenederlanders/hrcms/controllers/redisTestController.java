package nl.hardwerkendenederlanders.hrcms.controllers;

import nl.hardwerkendenederlanders.hrcms.database.redis.RedisArticles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class redisTestController {

    @Autowired
    RedisArticles articles;

    @PostMapping("item")
    public void PostItem(@RequestParam String key, @RequestParam String value){
        articles.insert(key, value);
    }


    @GetMapping("item")
    public String GetItem(@RequestParam String key) {
        return articles.find(key);
    }
}
