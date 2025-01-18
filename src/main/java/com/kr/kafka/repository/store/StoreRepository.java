package com.kr.kafka.repository.store;

import com.kr.kafka.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Integer> {

    Store findByName(String name);
}
