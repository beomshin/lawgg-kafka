package com.kr.kafka.api;

import com.kr.kafka.repository.RedisLockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
public class RedisController {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisLockRepository redisLockRepository;
    private final RedissonClient redissonClient;

    @GetMapping("/test/redis")
    public String redis() {
        ValueOperations<String, Object> operations = redisTemplate.opsForValue();
        System.out.println(operations.get("key"));

        operations.getAndDelete("key");
        return "success";
    }

    @GetMapping("/test/redis/lock")
    public String lock1(@RequestParam(name = "key") Long key) throws InterruptedException {
        while (!redisLockRepository.lock(key)) {
            log.info("LOCK 대기중 상태");
            Thread.sleep(100);
        }

        log.info("서비스 호출");
        return "success";
    }

    @GetMapping("/test/redis/unlock")
    public String unlock(@RequestParam(name = "key") Long key) {
        log.info("LOCK 해제");
        redisLockRepository.unlock(key);
        return "success";
    }


    @GetMapping("/test/redisson/lock")
    public String redissonLock(@RequestParam(name = "key") Long key) throws InterruptedException {
        RLock lock = redissonClient.getLock(String.valueOf(key));

        boolean available = lock.tryLock(10, 20, TimeUnit.SECONDS);
        if (!available) {
            log.info("LOCK");
            return "fail";
        }

        lock.unlock();

        log.info("서비스 호출");
        return "success";
    }

    @GetMapping("/test/redisson/unlock")
    public String redissonUnLock(@RequestParam(name = "key") Long key) {
        RLock lock = redissonClient.getLock(String.valueOf(key));

        if (lock.isLocked()) { // redisson은 lock thread 만 unlock 처리 가능
            log.info("LOCK");
            lock.unlock();
        } else {
            log.info("UNLOCK");
        }
        return "success";
    }
}
