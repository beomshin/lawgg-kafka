package com.kr.kafka.api;

import com.kr.kafka.entity.Store;
import com.kr.kafka.repository.store.StoreRepository;
import com.kr.kafka.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreRepository storeRepository;
    private final RedissonClient redissonClient;
    private final StoreService storeService;

    @GetMapping("/test/store")
    public String store() {
        List<Store> stores = storeRepository.findAll();

        log.info("{}", stores);
        return "success";
    }


    @GetMapping("/test/store/down")
    @Transactional
    public String storeDown(@RequestParam(name = "name") String storeName) {
        Store store = storeRepository.findByName(storeName);

        if (store != null) {
            store.decreaseCount();
        }

        return "success";
    }

    @GetMapping("/test/store/down2")
    public String storeDown2(@RequestParam(name = "name") String storeName) throws InterruptedException {
        RLock lock = redissonClient.getLock("lock:store:" + storeName);

        boolean acquired = false;
        try {
            // 🔹 10초 동안 락을 획득 시도, 락 획득 후 20초 동안 유지
            acquired = lock.tryLock(10, 20, TimeUnit.SECONDS);

            if (!acquired) {
                log.info("락 획득 실패: {}", storeName);
                return "Lock acquisition failed, please try again.";
            }

            storeService.store(storeName);

            return "success";

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Interrupted while waiting for lock.";
        } finally {
            // 🔹 현재 스레드가 락을 가지고 있는 경우에만 해제
            if (acquired && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }


}
