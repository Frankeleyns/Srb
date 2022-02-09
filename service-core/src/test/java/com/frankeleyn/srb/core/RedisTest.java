package com.frankeleyn.srb.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Frankeleyn
 * @date 2022/2/9 8:21
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void testSetKey() {
        redisTemplate.opsForValue().set("key1", "value1");
    }

    @Test
    public void testGetKey() {
        Object key1 = redisTemplate.opsForValue().get("key1");
        System.out.println(key1);
    }
}
