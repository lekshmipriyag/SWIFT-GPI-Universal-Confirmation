package com.swift.parseMT103Message.KafkaConsumer;

import com.swift.parseMT103Message.service.IntermediaryBankService;
import kafka.Kafka;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class KafkaConsumerTest {
    private static final String TEST_TOPIC = "KAFKA_TEST_MT103";
    private static Consumer<String, String> consumer;
    private String testMessage = MT103TestData();

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    IntermediaryBankService intermediaryBankService;

    @BeforeAll
    public static void setup() {
        // Create a Kafka consumer configuration
        Properties config = new Properties();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

         //Create a Kafka consumer
        consumer = new KafkaConsumer<>(config);
        consumer.subscribe(Collections.singleton(TEST_TOPIC));
    }

    @AfterAll
    public static void cleanup() {
        consumer.close();
    }


    @Test
    @Order(1)
    public void testKafkaProducer() throws ExecutionException, InterruptedException {
        String uniquePaymentID = "uniquePaymentID";
        MDC.put(uniquePaymentID, UUID.randomUUID().toString());
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(TEST_TOPIC, testMessage);
        producerRecord.headers().add(uniquePaymentID, MDC.get(uniquePaymentID).getBytes(StandardCharsets.UTF_8));
        kafkaTemplate.send(producerRecord);
    }

    @Test
    @Order(2)
    public void testKafkaConsumer() throws ExecutionException, InterruptedException, IOException {

        // Poll for the test message from the Kafka consumer
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofDays(1));
        // Verify that the message was received correctly
        ConsumerRecord<String, String> received = records.iterator().next();
        assertThat(received).isNotNull();
        assertThat(received.topic()).isEqualTo(TEST_TOPIC);
        assertThat(received.key()).isNull();
        assertThat(received.value()).isEqualTo(testMessage);
        intermediaryBankService.swiftMessageParsing(received.value(), MDC.get("uniquePaymentID"));
        assertTrue(true);
    }

    String MT103TestData() {
        String testData = "{1:F21SOMEBANKAXXX1986850704}{4:{177:1511020826}{451:0}}{1:F01SOMEBANKAXXX1986850704}{2:O1030351151102SENDERSXBXXX02467244891511020826N}{3:{103:TGT}{113:NYNN}{108:1510300035526-06}{115:070146070146DE0000000653977094}{119:STP}}{4:\n" +
                ":20:151030035526\n" +
                ":23B:CRED\n" +
                ":32A:151102EUR135,\n" +
                ":33B:EUR135,\n" +
                ":50F:/FR343409549895438945098548\n" +
                "1/SOME NAME\n" +
                "2/SOME ADDRESS\n" +
                "3/FR/PARIS\n" +
                "7/FR/1231532472\n" +
                ":52A:ABCDFGHK123\n" +
                ":59:/AU351234567800123456789\n" +
                "FIRM LTD\n" +
                ":71A:OUR\n" +
                ":72:/INS/ABNANL2A\n" +
                "//ABNANL2A OTHERS 123\n" +
                "/INS/ABNANL2A\n" +
                "-}{5:{MAC:00000000}{PAC:00000000}{CHK:447B8E8D50A7}{DLM:}}{S:{SAC:}{FAC:}{COP:P}}";
        return testData;
    }
}
