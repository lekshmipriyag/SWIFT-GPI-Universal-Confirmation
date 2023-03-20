package com.swift.updategpitracker.service;

import com.swift.updategpitracker.model.ResponseData;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestGPITrackService {


    private String TEST_TOPIC = "KAFKA_TEST_MT199";
    private static Consumer<String, String> consumer;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private String testNotification = MT199TestData();

    Random random = new Random();

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    GPITrackService gpiTrackService;

    @BeforeAll
    public static void setup() {
        // Create a Kafka consumer configuration
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumer = new KafkaConsumer<>(config);

    }

    @AfterAll
    public static void clean() {
        consumer.close();
    }

    @Test
    @Order(1)
    void testKafkaProducer() throws ExecutionException, InterruptedException {

        produceToKafka();
        assertTrue(true);
    }

    @Test
    @Order(2)
    void testKafkaConsumer() throws ExecutionException, InterruptedException, IOException {
        //Create a Kafka consumer
        consumer.subscribe(Collections.singleton(TEST_TOPIC));

        // Poll for the test message from the Kafka consumer
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofDays(1));
        // Verify that the message was received correctly
        ConsumerRecord<String, String> received = records.iterator().next();
        assertThat(received.topic()).isEqualTo(TEST_TOPIC);
        assertThat(received.value()).isEqualTo(testNotification);
        gpiTrackService.processKafkaData(testNotification);
        assertTrue(true);
    }

    @Test
    @Order(3)
    void testProcessKafkaDataNotAcceptable() throws ExecutionException, InterruptedException, IOException {

        ResponseEntity<ResponseData> response = gpiTrackService.processKafkaData(testNotification);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
    }

    String MT199TestData() {

       return "\"[" +
                "SENDERSXBXXX," +
                "SENDERSXBXXX6782," +
                "751030035526," +
                "460247ac-c422-4e58-a0c4-daa08658e28f," +
                "CRED," +
                "151102EUR135,," +
                "FR343409549895438945098548\r\n1/SOME NAME\r\n2/SOME ADDRESS\r\n3/FR/PARIS\r\n7/FR/1231532472," +
                "/AU351234567800123456789\r\nFIRM LTD," +
                "OUR," +
                null +
                "," +
                null +
                "]\"";
    }

    private void produceToKafka() {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(TEST_TOPIC, testNotification);
        kafkaTemplate.send(producerRecord);
    }

}
