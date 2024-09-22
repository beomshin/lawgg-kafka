package com.kr.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmConsumer {


    /**
     * offset을 최신으로 설정.
     * https://stackoverflow.com/questions/57163953/kafkalistener-consumerconfig-auto-offset-reset-doc-earliest-for-multiple-listene
     * @param alarmEvent
     * @param ack
     */
//    @KafkaListener(topics = "${kafka.topic.alarm.name}", groupId = "${kafka.consumer.alarm.rdb-group-id}", containerFactory = "kafkaListenerContainerFactoryRDB")
//    public void createAlarmInRDBConsumerGroup(AlarmEvent alarmEvent, Acknowledgment ack) {
//        log.info("createAlarmInRDBConsumerGroup");
//        ack.acknowledge();
//    }
//
//    @KafkaListener(topics = "${kafka.topic.alarm.name}", groupId = "${kafka.consumer.alarm.redis-group-id}", containerFactory = "kafkaListenerContainerFactoryRedis")
//    public void redisPublishConsumerGroup(AlarmEvent alarmEvent, Acknowledgment ack) {
//        log.info("redisPublishConsumerGroup");
//        ack.acknowledge();
//    }

}
