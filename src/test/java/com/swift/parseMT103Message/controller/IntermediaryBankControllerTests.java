package com.swift.parseMT103Message.controller;

import com.swift.parseMT103Message.model.ResponseData;
import com.swift.parseMT103Message.model.SwiftMessageMT103;
import com.swift.parseMT103Message.service.IntermediaryBankService;
import org.apache.catalina.filters.ExpiresFilter;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IntermediaryBankControllerTests {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IntermediaryBankController intermediaryBankController;

    @Autowired
    MongoRepository mongoRepository;

    @Autowired
    IntermediaryBankService intermediaryBankService;

    private ResponseData result = new ResponseData();

    @Test
    @Order(1)
    public void testWelcome() {
        intermediaryBankController.welcome();
    }

    @Test
    @Order(2)
    public void testSendMT103() throws Exception {

        logger.info("Bank operation Code"+message().getBankOperationCode());
        ResponseEntity<ResponseData> response = intermediaryBankController.sendMT103ToMongoDB(message());
        /*if(message().getBankOperationCode().equals(null)){
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
        message().setBankOperationCode("CRE");
        if(message().getBankOperationCode().equals(false)){
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
        int documentCountBySenderRefrenceNumber = intermediaryBankService.getDocumentCountBYSRN(message().getSenderReferenceNumber());
        int documentCountByUETR = intermediaryBankService.getDocumentCountBYUETR(message().getUniqueEndToEndTransactionReferenceNumber());

        if (documentCountBySenderRefrenceNumber >= 1 || documentCountByUETR >= 1) {
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }*/
           // logger.info("result "+ response.getBody().getMessages().get(0).getBankOperationCode());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    public SwiftMessageMT103 message() {
        SwiftMessageMT103 swiftMessageMT103 = new SwiftMessageMT103();
        clearMessage();
        swiftMessageMT103.setSenderReferenceNumber("151030035521");
        swiftMessageMT103.setUniqueEndToEndTransactionReferenceNumber("4bfeab5f-f3c6-4736-bf79-705024ebb752");

        swiftMessageMT103.setBankOperationCode("CRED");
        swiftMessageMT103.setValueDateOrCurrencyOrSettledAmount("151102EUR135");
        swiftMessageMT103.setPayer("/FR343409549895438945098541\n" +
                "1/SOME NAME\n" +
                "2/SOME ADDRESS\n" +
                "3/FR/PARIS\n" +
                "7/FR/1231532472");
        swiftMessageMT103.setBeneficiary("/AU351234567800123456789\n" +
                "FIRM LTD");
        swiftMessageMT103.setOrderingInstitution("ABCDFGHK123");
        swiftMessageMT103.setDetailsOfCharge("OUR");
        swiftMessageMT103.setSenderToReceiverInfo("/INS/ABNANL2A\n" +
                "//ABNANL2A OTHERS 123\n" +
                "/INS/ABNANL2A");
        swiftMessageMT103.setSenderBank("SENDERSXBXXX");
        swiftMessageMT103.setSendersCharge("");
        return swiftMessageMT103;
    }

    public SwiftMessageMT103 clearMessage(){
        SwiftMessageMT103 swiftMessageMT103 = new SwiftMessageMT103();
        swiftMessageMT103.setSenderReferenceNumber("");
        swiftMessageMT103.setUniqueEndToEndTransactionReferenceNumber("");

        swiftMessageMT103.setBankOperationCode("");
        swiftMessageMT103.setValueDateOrCurrencyOrSettledAmount("");
        swiftMessageMT103.setPayer("");
        swiftMessageMT103.setBeneficiary("");
        swiftMessageMT103.setOrderingInstitution("");
        swiftMessageMT103.setDetailsOfCharge("");
        swiftMessageMT103.setSenderToReceiverInfo("");
        swiftMessageMT103.setSenderBank("");
        swiftMessageMT103.setSendersCharge("");
        return swiftMessageMT103;
    }
}
