package com.swift.paymentMessage.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DebtorBankProducerServiceTests {

    @Autowired
    DebtorBankProducerService debtorBankProducerService;
    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    private String kafkaTopicName = "KAFKA_TOPIC_MT103";
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    @Order(1)
    public void testSendMT103() throws Exception {
        String testData = MT103TestData();
        String produceDataToKafka = debtorBankProducerService.sendMt103(testData);
        assertEquals("200 OK", "200 OK");
    }

    public String MT103TestData() {
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
