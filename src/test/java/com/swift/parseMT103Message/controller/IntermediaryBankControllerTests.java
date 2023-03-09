package com.swift.parseMT103Message.controller;

import com.prowidesoftware.swift.model.SwiftMessage;
import com.prowidesoftware.swift.model.mt.mt1xx.MT103;
import com.swift.parseMT103Message.model.ResponseData;
import com.swift.parseMT103Message.model.SwiftMessageMT103;
import com.swift.parseMT103Message.service.IntermediaryBankService;
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

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntermediaryBankControllerTests {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IntermediaryBankController intermediaryBankController;

    @Autowired
    IntermediaryBankService intermediaryBankService;

    private ResponseData result = new ResponseData();
    SwiftMessageMT103 swiftMessageMT103;

    @Test
    @Order(1)
    public void testWelcome() {
        intermediaryBankController.welcome();
    }

    @Test
    @Order(2)
    public void testSendMT103() throws Exception {
        SwiftMessageMT103 swiftMessageMT103 = new SwiftMessageMT103();
        testSendMT103toMongoWithActualData();
        intermediaryBankController.sendMT103ToMongoDB(swiftMessageMT103);
        assertTrue(true);
    }

    @Test
    @Order(3)
    public void testGetMT103FromMongoDB() throws Exception {
        String srn = "951030035526";
        ResponseEntity<ResponseData> response = intermediaryBankController.getMT103FromMongoDB(srn);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
    }

    @Test
    @Order(4)
    public void testGetMT103FromMongoDBNNotFound() throws Exception {
        String srn = "551030035526";
        ResponseEntity<ResponseData> response = intermediaryBankController.getMT103FromMongoDB(srn);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

        public void testSendMT103toMongoWithActualData() throws IOException {
        String messageWithCorrectFormat = "{1:F21SOMEBANKAXXX1986850704}{4:{177:1511020826}{451:0}}{1:F01SOMEBANKAXXX1986850704}{2:O1030351151102SENDERSXBXXX02467244891511020826N}{3:{103:TGT}{113:NYNN}{108:1510300035526-06}{115:070146070146DE0000000653977094}{119:STP}}{4:\n" +
                ":20:951030035526\n" +
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

        sendMT103ServiceMessage(messageWithCorrectFormat);
    }

    void sendMT103ServiceMessage(String messageData) throws IOException {

        String UETR = UUID.randomUUID().toString();
        SwiftMessageMT103 swiftMessageMT103 = new SwiftMessageMT103();
        SwiftMessage serviceMessage = SwiftMessage.parse(messageData);
        if (serviceMessage.isServiceMessage()) {
            serviceMessage = SwiftMessage.parse(serviceMessage.getUnparsedTexts().getAsFINString());
        }
        if (serviceMessage.isType(103)) {
            //Specialise the message to its specific model representation
            MT103 mt = new MT103(serviceMessage);
            //get MT103Fields after validation
            intermediaryBankService.getMT103Fields(UETR, mt);
            intermediaryBankService.sendMt103ToMongoDB(swiftMessageMT103);
            assertTrue(true);
        }
    }


}
