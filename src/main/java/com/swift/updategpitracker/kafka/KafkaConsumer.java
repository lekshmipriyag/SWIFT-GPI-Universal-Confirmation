package com.swift.updategpitracker.kafka;

import com.swift.updategpitracker.service.GPITrackService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${kafka.topic_name}")
    private String topicName;

    @Autowired
    GPITrackService gpiTrackService;

    @KafkaListener(topics = "${kafka.topic_name}", groupId = "${kafka.group_id}")
    public void consume(ConsumerRecord<String, String> consumerRecord) {
        try {
            log.info("TópicName: {}", topicName);
            gpiTrackService.processKafkaData(consumerRecord.value());
        } catch (Exception e) {
            log.error( e.getMessage());
        }
    }
}
