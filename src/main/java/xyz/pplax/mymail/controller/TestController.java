package xyz.pplax.mymail.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.pplax.mymail.utils.RedisOperator;

@RestController
@RequestMapping("/test")
public class TestController {



    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/redis")
    public String redisTest() {

        redisOperator.set("testKey", "testValue");

        String testKey = redisOperator.get("testKey");

        System.out.println(testKey);
        return testKey;
    }



}
