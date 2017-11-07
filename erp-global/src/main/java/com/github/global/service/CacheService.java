package com.github.global.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    /** @see org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.RedisConfiguration */
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisTemplate<Object, Object> redisTemplate;

    /** 往 redis 中放值 */
    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }
    /** 往 redis 放值, 并设定超时时间 */
    public void set(String key, String value, long timeOut, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, timeOut, timeUnit);
    }
    /** 从 redis 中取值 */
    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }
    /** 从 redis 中删值 */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /** 向队列写值(从左边压栈) */
    public void push(Object key, Object value) {
        redisTemplate.opsForList().leftPush(key, value);
    }
    /** 向队列读值(从右边出栈) */
    public Object pop(Object key) {
        return redisTemplate.opsForList().rightPop(key);
    }
}
