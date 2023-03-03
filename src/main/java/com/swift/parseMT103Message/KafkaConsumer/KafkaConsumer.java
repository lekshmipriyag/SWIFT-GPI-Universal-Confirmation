package com.swift.parseMT103Message.KafkaConsumer;

import com.swift.parseMT103Message.service.IntermediaryBankService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class KafkaConsumer {
    Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${kafka.topic_name}")
    private String topicName;

    @Autowired
    private IntermediaryBankService intermediaryBankService;

    @KafkaListener(topics = "${kafka.topic_name}", groupId = "${kafka.group_id}")
    public void consume(ConsumerRecord<String, String> consumerRecord) {
        try {
            MDC.put("uniquePaymentID", new String(consumerRecord.headers().lastHeader("uniquePaymentID").value(),
                    StandardCharsets.UTF_8));
            log.info("TópicName: {}", topicName);
            log.info("Consuming data from: "+topicName);
            intermediaryBankService.swiftMessageParsing(consumerRecord.value(), MDC.get("uniquePaymentID"));

        } catch (Exception e) {
            log.error( e.getMessage());
        }

    }
}
