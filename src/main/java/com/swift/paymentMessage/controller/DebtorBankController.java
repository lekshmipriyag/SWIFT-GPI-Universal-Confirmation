package com.swift.paymentMessage.controller;

import com.swift.paymentMessage.service.DebtorBankProducerService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/swiftMessage/version1/")
public class DebtorBankController {

    @Autowired
    private DebtorBankProducerService debtorBankProducerService;

    @RequestMapping("welcome")
    public String welcome(){
        return "Welcome to the world of SWIFT Transactions";
    }

    @RequestMapping(method = RequestMethod.POST, value = "sendMT103", produces = MediaType.TEXT_PLAIN_VALUE)
    public String sendMT103(@RequestBody String mt103Text) throws Exception{
        return debtorBankProducerService.sendMt103(mt103Text);
    }
}
