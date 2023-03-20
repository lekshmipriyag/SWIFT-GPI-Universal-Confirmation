package com.swift.updategpitracker.service;

import com.swift.updategpitracker.model.ResponseData;
import com.swift.updategpitracker.model.SwiftMessageMT199;
import com.swift.updategpitracker.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class GPITrackService {

    @Autowired
    NotificationRepository notificationRepository;

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private String receiver = "TRCKCHZZ";
    private ArrayList<String> narrative = new ArrayList<>();
    private ResponseData result = new ResponseData();
    private List<SwiftMessageMT199> notificationList = new ArrayList<>();

    public ResponseEntity<ResponseData> processKafkaData(String mt199NotificationData) {
        logger.info("Consuming Notification from Kafka" + mt199NotificationData);
        String notification = "";
        notification = mt199NotificationData.replace("\"", "");
        narrative.clear();
        try {
            String[] dataToDB = notification.split(",");
            String sender = dataToDB[0].replace("[", "");
            String transactionRefereneNumber = ":20: " + dataToDB[1];
            String relatedReference = ":21: " + dataToDB[2];
            String uetr = dataToDB[3];
            String bankOperationCode = ":23B: " + dataToDB[4];
            String valueDate = ":32A: " + dataToDB[5];
            String payer = ":50F: " + dataToDB[7];
            String beneficiary = ":59: " + dataToDB[8];
            String detailsOfCharge = ":71A: " + dataToDB[9];
            String exchangeRate = ":36: " + dataToDB[10];
            String receiverCharges = ":71G: " + dataToDB[10];
            narrative.add(bankOperationCode);
            narrative.add(valueDate);
            narrative.add(payer);
            narrative.add(beneficiary);
            narrative.add(detailsOfCharge);
            narrative.add(exchangeRate);
            narrative.add(receiverCharges);
            SwiftMessageMT199 mt199Notification = new SwiftMessageMT199(sender, receiver, uetr, transactionRefereneNumber,
                    relatedReference, narrative);
            int documentCountBySenderRefrenceNumber = getDocumentCountBYSRN(transactionRefereneNumber);
            int documentCountByUETR = getDocumentCountBYUETR(uetr);
            if (documentCountBySenderRefrenceNumber >= 1 || documentCountByUETR >= 1) {
                logger.info("This Notification is Available in DB");
                responseData();
                result.setMessage("Transaction denied!!. This transaction details are currently available in DB. ");
                return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
            }
            notificationRepository.save(mt199Notification);
          //  logger.info("Storing to MongoDB completed");
           // notificationList.add(mt199Notification);
          //  result.setMessages(notificationList);
          //  result.setMessage("Successfully sent MT199 notification to MongoDB");
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            responseData();
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }

    }

    //get details by TRN
    private int getDocumentCountBYSRN(String trn) {
        List<SwiftMessageMT199> fetchMessageByTRN = notificationRepository.findByTransactionReferenceNumber(trn);
        return fetchMessageByTRN.size();
    }

    //get details by UETR
    private int getDocumentCountBYUETR(String trn) {
        List<SwiftMessageMT199> fetchMessageByUETR = notificationRepository.findByUniqueEndToEndTransactionReferenceNumber(trn);
        return fetchMessageByUETR.size();
    }

    public void responseData() {
        result.setMessage("Failed");
        result.setMessages(null);
    }


}
