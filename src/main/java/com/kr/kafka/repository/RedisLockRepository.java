package com.kr.kafka.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLockRepository {


    private final RedisTemplate<String, Object> redisTemplate;

    public Boolean lock(Long key) {
        return redisTemplate.opsForValue()
                .setIfAbsent(key.toString(), "lock", Duration.ofMillis(5000));
    }

    public Boolean unlock(Long key) {
        return redisTemplate.delete(key.toString());
    }

}
