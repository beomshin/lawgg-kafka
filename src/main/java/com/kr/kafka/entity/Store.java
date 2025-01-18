package com.kr.kafka.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity(name = "store")
@Table(name = "store")
@ToString(callSuper = true)
@Getter
@Setter
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "count")
    private Integer count;

    public void decreaseCount() {
        log.info("decrease count {}", count);
        this.count--;
    }


}
