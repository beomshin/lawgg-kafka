package com.kr.kafka;

import com.kr.kafka.repository.RedisLockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisLockRepository redisLockRepository;

    @Test
    public void test() throws InterruptedException {

        AtomicReference<Integer> money = new AtomicReference<>(10000);

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for(int i=0; i<threadCount; i++){
            executorService.submit(()->{
                try{

                    while (!redisLockRepository.lock(10000L)) {
                        Thread.sleep(100);
                    }

                    money.set(money.get() - 100);

                    redisLockRepository.unlock(10000L);

                } catch (InterruptedException e) {

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        assertEquals(0, money.get());
    }


}
