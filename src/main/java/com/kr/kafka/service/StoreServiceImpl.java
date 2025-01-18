package com.kr.kafka.service;

import com.kr.kafka.entity.Store;
import com.kr.kafka.repository.store.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Override
    @Transactional
    public void store(String storeName) {
        // 🔹 락을 획득한 경우에만 트랜잭션을 시작해야 함.
        Store store = storeRepository.findByName(storeName);
        if (store == null) {
            return;
        }

        store.decreaseCount();
    }
}
