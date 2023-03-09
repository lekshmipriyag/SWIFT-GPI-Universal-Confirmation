package com.swift.parseMT103Message.service;

import com.prowidesoftware.swift.model.SwiftMessage;
import com.prowidesoftware.swift.model.field.Field71F;
import com.prowidesoftware.swift.model.mt.mt1xx.MT103;
import com.swift.parseMT103Message.model.ResponseData;
import com.swift.parseMT103Message.model.SwiftMessageMT103;
import com.swift.parseMT103Message.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Service
public class IntermediaryBankService {

    @Autowired
    private MessageRepository messageRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ArrayList<String> parsedFields = new ArrayList<String>(); // Create an ArrayList object
    private ResponseData result = new ResponseData();
    private ArrayList<String> mandatoryFields = new ArrayList<String>(); // Create an ArrayList object

    public IntermediaryBankService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }


    public String swiftMessageParsing(String message, String UETR) throws IOException {

        logger.info("SWIFT Message Type: MT103 ");
        logger.info("Parsing Started");
        //Read and Parse MT103 message
        SwiftMessage serviceMessage = SwiftMessage.parse(message);

        //at this point the serviceMessage variable will contain the actual user to user message,
        //regardless if it was preceded by and ACK.

        if (serviceMessage.isServiceMessage()) {
            serviceMessage = SwiftMessage.parse(serviceMessage.getUnparsedTexts().getAsFINString());
        }
        logger.info("Parsing Completed");
        if (serviceMessage.isType(103)) {
            //Specialise the message to its specific model representation
            MT103 mt = new MT103(serviceMessage);
            //get MT103Fields after validation
            getMT103Fields(UETR, mt);

            //print details of the fields
            //logMT103FieldValues();
        }
        return null;
    }

    //create a document
    public ResponseEntity<ResponseData> sendMt103ToMongoDB(SwiftMessageMT103 swiftMessage) {
        List<SwiftMessageMT103> messageList = new ArrayList<>();
        boolean fieldsOk = true;
        try {
            //SwiftMessageMT103 message = new SwiftMessageMT103(parsedFields.get(0),
            swiftMessage = new SwiftMessageMT103(parsedFields.get(0),
                    parsedFields.get(1),
                    parsedFields.get(2),
                    parsedFields.get(3),
                    parsedFields.get(4),
                    parsedFields.get(5),
                    parsedFields.get(6),
                    parsedFields.get(7),
                    parsedFields.get(8),
                    parsedFields.get(9),
                    parsedFields.get(10),
                    parsedFields.get(11),
                    parsedFields.get(12));

            logger.info("Checking whether Mandatory fields are present or not");
            for (int i = 0; i < mandatoryFields.size(); i++) {
                if (mandatoryFields.get(i) == null) {
                    fieldsOk = false;
                    result.setMessage("This transaction request is not acceptable. " +
                            "One of the Mandatory Fields is Missing");
                    result.setMessages(null);
                    return new ResponseEntity<ResponseData>(result, HttpStatus.NOT_ACCEPTABLE);
                }
            }
            logger.info("Mandatory Fields Format checking");
            if (mandatoryFieldsFormatChecking() == false) {
                result.setMessage("Something went wrong!. Please check the mandatory Fields Format.");
                result.setMessages(null);
                return new ResponseEntity<ResponseData>(result, HttpStatus.NOT_ACCEPTABLE);
            }
            logger.info("Mandatory fields Checking Completed");
            logger.info("Storing to MongoDB");
            int documentCountBySenderRefrenceNumber = getDocumentCountBYSRN(parsedFields.get(0));
            int documentCountByUETR = getDocumentCountBYUETR(parsedFields.get(1));
            logger.info("UETR: " + parsedFields.get(1));
            if (documentCountBySenderRefrenceNumber >= 1 || documentCountByUETR >= 1) {
                responseData();
                result.setMessage("Transaction denied!!. This transaction details are currently available in DB. ");
                return new ResponseEntity<ResponseData>(result, HttpStatus.NOT_ACCEPTABLE);
            }
            messageRepository.save(swiftMessage); //data is saving to DB
            logger.info("Storing to MongoDB completed");
            messageList.add(swiftMessage);
            result.setMessages(messageList);
            result.setMessage("Successfully sent MT103 message to MongoDB");
            return new ResponseEntity<ResponseData>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            responseData();
            return new ResponseEntity<ResponseData>(result, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ResponseData> fetchMt103FromMongoDB(String trn) {
        logger.info("Get message by TRN");
        try {
            List<SwiftMessageMT103> fetchMessageByTRN = messageRepository.findBySenderReferenceNumber(trn);
            if (fetchMessageByTRN.size() == 0) {
                responseData();
                result.setMessage("Searched Sender Reference Number is not found");
                return new ResponseEntity<ResponseData>(result, HttpStatus.NOT_FOUND);
            } else {
                result.setMessages(fetchMessageByTRN);
                result.setMessage("Searched Sender Reference Number is found");
                return new ResponseEntity<ResponseData>(result, HttpStatus.FOUND);
            }
        } catch (Exception e) {
            responseData();
            return new ResponseEntity<ResponseData>(result, HttpStatus.BAD_REQUEST);
        }
    }

    //Mt103 mandatory fields format Checking
    private boolean mandatoryFieldsFormatChecking() {
        String SRN = parsedFields.get(0);
        String bankOperationCode = parsedFields.get(2);
        String detailsOfCharges = parsedFields.get(7);
        if (isSRNAlphaNumeric(SRN) &&
                SRN.length() <= 16 &&
                haveOnlyCharacters(bankOperationCode) &&
                bankOperationCode.length() == 4 &&
                haveOnlyCharacters(detailsOfCharges) &&
                detailsOfCharges.length() == 3) {
            return true;
        }
        return false;
    }

    private static boolean isSRNAlphaNumeric(String s) {
        return s != null && s.matches("^[a-zA-Z0-9]*$");
    }

    private static boolean haveOnlyCharacters(String s) {
        return s != null && s.matches("^[a-zA-Z]*$");
    }

    //get details by TRN
    public int getDocumentCountBYSRN(String trn) {
        List<SwiftMessageMT103> fetchMessageByTRN = messageRepository.findBySenderReferenceNumber(trn);
        return fetchMessageByTRN.size();
    }

    //get details by UETR
    public int getDocumentCountBYUETR(String trn) {
        List<SwiftMessageMT103> fetchMessageByUETR = messageRepository.findByUniqueEndToEndTransactionReferenceNumber(trn);
        return fetchMessageByUETR.size();
    }


    public ArrayList getMT103Fields(String UETR, MT103 mt) {

        String senderReferenceNumber = (mt.getField20() != null) ? mt.getField20().getValue() : null;
        String bankOperationCode = (mt.getField23B() != null) ? mt.getField23B().getValue() : null;
        String ValueDate = (mt.getField32A() != null) ? mt.getField32A().getValue() : null;
        String payer = (mt.getField50F() != null) ? mt.getField50F().getValue() : null;
        String orderingInstitution = (mt.getField52A() != null) ? mt.getField52A().getValue() : null;
        String beneficiary = (mt.getField59() != null) ? mt.getField59().getValue() : null;
        String detailsOfCharge = (mt.getField71A() != null) ? mt.getField71A().getValue() : null;
        String senderToReceiverInfo = (mt.getField72() != null) ? mt.getField72().getValue() : null;
        String exchangeRate = (mt.getField36() != null) ? mt.getField36().getValue() : null;
        String receiverCharges = (mt.getField71G() != null) ? mt.getField71G().getValue() : null;
        String senderBank = mt.getSender();
        String sendersCharges = null;
        //String sendersCharges = (mt.getField71F() != null) ? mt.getField71F().get(0).getValue():null;
//        List<Field71F> sendersCharges = mt.getField71F();
//        if(sendersCharges.contains(null)){
//            String sendersCharge = null;
//        }else{
//            String sendersCharge = sendersCharges.
//        }


        //Remove the prefix forward slash
        payer = (StringUtils.startsWithIgnoreCase(payer, "/")) ? payer.substring(1) : payer;
        beneficiary = (StringUtils.startsWithIgnoreCase(beneficiary, "/")) ? beneficiary.substring(1) : beneficiary;

        //Mandatory Fields
        mandatoryFields.add(senderReferenceNumber);
        mandatoryFields.add(bankOperationCode);
        mandatoryFields.add(ValueDate);
        mandatoryFields.add(payer);
        mandatoryFields.add(beneficiary);
        mandatoryFields.add(detailsOfCharge);

        parsedFields.add(senderReferenceNumber);
        parsedFields.add(UETR);
        parsedFields.add(bankOperationCode);
        parsedFields.add(ValueDate);
        parsedFields.add(payer);
        parsedFields.add(beneficiary);
        parsedFields.add(orderingInstitution);
        parsedFields.add(detailsOfCharge);
        parsedFields.add(exchangeRate);
        parsedFields.add(receiverCharges);
        parsedFields.add(senderToReceiverInfo);
        parsedFields.add(senderBank);
        parsedFields.add(sendersCharges);
        return parsedFields;
    }

//    private void logMT103FieldValues() {
//        logger.info("Sender's reference Number of MT103: " + parsedFields.get(0));
//        logger.info("Unique End-toEnd Transaction Number is: " + parsedFields.get(1));
//        logger.info("Bank Operation Code: " + parsedFields.get(2));
//        logger.info("Value Date/ Currency/Interbank Settled Amount: " + parsedFields.get(3));
//        logger.info("Payer Details: " + parsedFields.get(4));
//        logger.info("Beneficiary Details: " + parsedFields.get(5));
//        logger.info("Ordering Institution: " + parsedFields.get(6));
//        logger.info("Details of Charge: " + parsedFields.get(7));
//        logger.info("Exchange Rate: " + parsedFields.get(8));
//        logger.info("Receiver's Charges: " + parsedFields.get(9));
//        logger.info("Sender To Receiver Information: " + parsedFields.get(10));
//        logger.info("Sender Bank: "+ parsedFields.get(11));
//        logger.info("Sender's Charges: " + parsedFields.get(12));
//
//    }

    public void responseData() {
        result.setMessage("Failed");
        result.setMessages(null);
    }

}
