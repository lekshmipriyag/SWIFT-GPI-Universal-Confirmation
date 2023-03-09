package com.swift.parseMT103Message.controller;

import com.prowidesoftware.swift.model.SwiftMessage;
import com.swift.parseMT103Message.model.ResponseData;
import com.swift.parseMT103Message.model.SwiftMessageMT103;
import com.swift.parseMT103Message.service.IntermediaryBankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/swiftMessageParsing/version1/")
public class IntermediaryBankController {

    @Autowired
    IntermediaryBankService intermediaryBankService;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("welcome")
    public String welcome() {
        return "Microservice for parsing MT103 message";
    }

    @RequestMapping(method = RequestMethod.POST, value = "sendMT103ToMongoDB")
    public ResponseEntity sendMT103ToMongoDB(SwiftMessageMT103 swiftMessage) {
        logger.info("SwiftMessage "+ swiftMessage.getUniqueEndToEndTransactionReferenceNumber());
        return intermediaryBankService.sendMt103ToMongoDB(swiftMessage);
    }

    @RequestMapping(method = RequestMethod.GET, value = "getMT103FromMongoDB/{trn}")
    public ResponseEntity<ResponseData> getMT103FromMongoDB(@PathVariable String trn) {
        return intermediaryBankService.fetchMt103FromMongoDB(trn);
    }
}
