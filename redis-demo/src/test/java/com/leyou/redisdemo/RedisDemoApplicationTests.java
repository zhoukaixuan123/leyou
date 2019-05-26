package com.leyou.redisdemo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.SQLOutput;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisDemoApplicationTests {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Test
    public void contextLoads() {
        //String  类型
        redisTemplate.opsForValue().set("test","hello word");
        String  test = redisTemplate.opsForValue().get("test");
        System.out.println(test);

        //hash类型
        BoundHashOperations<String, Object, Object> opts = redisTemplate.boundHashOps("user:123");
        opts.put("name","Rose");
        opts.put("age","21");

        System.out.println(opts.get("name"));
        System.out.println(opts.get("age"));
        System.out.println(opts.entries());

    }

}
