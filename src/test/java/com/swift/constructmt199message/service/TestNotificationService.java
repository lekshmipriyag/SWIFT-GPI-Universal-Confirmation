package com.swift.constructmt199message.service;

import com.swift.constructmt199message.model.ResponseData;
import com.swift.constructmt199message.model.SwiftMessageMT103;
import com.swift.constructmt199message.repository.MessageRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
 class TestNotificationService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    NotificationService notificationService;

    @Autowired
    MessageRepository messageRepository;

    private List<SwiftMessageMT103> findTransactionDetails;

    @Test
    @Order(1)
    void testFetchMt103FromMongoDB(){
        String uetr = "e6e21c35-cb11-4371-8799-8f9b119a0faf";
        ResponseEntity<ResponseData> response =notificationService.fetchMt103FromMongoDB(uetr);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
    }

    @Test
    @Order(2)
    void testFetchMt103FromMongoDBNotFound(){
        String uetr = "115bc452-7888-4a4b-ab98-f6bdf32817cc";
        ResponseEntity<ResponseData> response =notificationService.fetchMt103FromMongoDB(uetr);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(3)
    void testFetchAllDataFromMongoDB(){
        ResponseEntity<ResponseData> response =notificationService.fetchAllDataFromMongoDB();
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
    }

    @Test
    @Order(4)
    void testSendMT199ToKafka(){
        String uetr = "e6e21c35-cb11-4371-8799-8f9b119a0faf";
        ResponseEntity<ResponseData> response =notificationService.sendMT199ToKafka(uetr);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @Order(5)
    void testGetDocumentCountBYUETR(){
        String uetr = "905bc452-7888-4a4b-ab98-f6bdf32817cc";
       int count =notificationService.getDocumentCountBYUETR(uetr);
        assertEquals(0, count);
    }

    @Test
    @Order(6)
    void testConstructMT199Message(){
        String uetr = "e6e21c35-cb11-4371-8799-8f9b119a0faf";
        findTransactionDetails = messageRepository.findByUniqueEndToEndTransactionReferenceNumber(uetr);
        notificationService.constructMT199Message(findTransactionDetails);
        assertEquals(1, findTransactionDetails.size());
    }
    // use the below test cases at the end of the transactions
//    @Test
//    @Order(7)
//    void testGetAllTransactionsNotFound(){
//        messageRepository.deleteAll();
//        ResponseEntity<ResponseData> response =notificationService.fetchAllDataFromMongoDB();
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//    }

}
