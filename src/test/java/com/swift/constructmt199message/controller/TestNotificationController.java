package com.swift.constructmt199message.controller;

import com.swift.constructmt199message.model.ResponseData;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestNotificationController {

    @Autowired
    NotificationController notificationController;

    @Test
    @Order(1)
    void testWelcome(){
        notificationController.welcome();
        assertTrue(true);
    }

    @Test
    @Order(2)
    void testGetMT103FromMongoDBFound(){
        String uetr = "e6e21c35-cb11-4371-8799-8f9b119a0faf";
        ResponseEntity<ResponseData> response =notificationController.getMT103FromMongoDB(uetr);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
    }

    @Test
    @Order(3)
    void testGetMT103FromMongoDBNotFound(){
        String uetr = "d2125c11-7b2a-4fb0-9ebc-81d69bd190d8";
        ResponseEntity<ResponseData> response =notificationController.getMT103FromMongoDB(uetr);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @Order(4)
    void testGetAllTransactionsFound(){
        ResponseEntity<ResponseData> response =notificationController.getAllTransactions();
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
    }

    @Test
    @Order(5)
    void testSend199ToKafkaServerErr(){
        String uetr = "ab4c12bc-1f75-4407-92bd-c9a998a3a916";
        ResponseEntity<ResponseData> response =notificationController.send199ToKafka(uetr);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @Order(6)
    void testSend199ToKafkaCreate(){
        String uetr = "e6e21c35-cb11-4371-8799-8f9b119a0faf";
        ResponseEntity<ResponseData> response =notificationController.send199ToKafka(uetr);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }


}
