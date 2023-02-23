package com.swift.paymentMessage.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class DebtorBankProducerService {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Value(value = "${kafka.topic_name}")
    private String kafkaTopicName = "KAFKA_TOPIC_MT103";

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public String sendMt103(String inputData) throws Exception {

        MDC.put("uniquePaymentID", UUID.randomUUID().toString());
        ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(kafkaTopicName, inputData);
        producerRecord.headers().add("uniquePaymentID", MDC.get("uniquePaymentID").getBytes(StandardCharsets.UTF_8));
        logger.info("\nThe following request received from Debtor Bank\n" + MDC.get("uniquePaymentID") + "\n" + inputData);
        kafkaTemplate.send(producerRecord);
        return "{\n" +
                "\"response\":\" Message has been sent\"\n" +
                "\"status\":\" 200\"\n" +
                "\"Message Type\":\" MT103\"\n" +
                "}";
    }
}


