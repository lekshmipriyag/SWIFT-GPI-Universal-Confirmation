package com.swift.constructmt199message.controller;

import com.swift.constructmt199message.model.ResponseData;
import com.swift.constructmt199message.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/swiftNotificationConstructing/version1/")
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @RequestMapping("welcome")
    public String welcome() {
        return "Microservice for constructing MT199 Notification";
    }

    @GetMapping( value = "getMT103FromMongoDB/{uetr}")
    public ResponseEntity<ResponseData> getMT103FromMongoDB(@PathVariable String uetr) {
        return notificationService.fetchMt103FromMongoDB(uetr);
    }

    @GetMapping(value = "getAllTransactions")
    public ResponseEntity<ResponseData> getAllTransactions() {
        return notificationService.fetchAllDataFromMongoDB();
    }

    @PostMapping( value = "sendMT99ToKafka")
    public ResponseEntity<ResponseData> send199ToKafka(@RequestParam String uetr) throws NullPointerException {
        return notificationService.sendMT199ToKafka(uetr);
    }
}
