package com.swift.constructmt199message.service;

import com.google.gson.Gson;
import com.swift.constructmt199message.model.ResponseData;
import com.swift.constructmt199message.model.SwiftMessageMT103;
import com.swift.constructmt199message.model.SwiftMessageMT199;
import com.swift.constructmt199message.repository.MessageRepository;
import com.swift.constructmt199message.repository.NotificationRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class NotificationService {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Value(value = "${kafka.topic_name}")
    private String kafkaTopicName = "KAFKA_TOPIC_MT199";


    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private ResponseData result = new ResponseData();
    private List<SwiftMessageMT103> findTransactionDetails;
    private ArrayList<String> constructMT199 = new ArrayList<>();
    Random random = new Random();

    //fetch details from MongoDB by uetr
    public ResponseEntity<ResponseData> fetchMt103FromMongoDB(String uetr) {
        logger.info("Get message by uetr");
        try {
            List<SwiftMessageMT103> fetchMessageByUETR = messageRepository.findByUniqueEndToEndTransactionReferenceNumber(uetr);
            if (fetchMessageByUETR.isEmpty()) {
                responseData();
                result.setMessage("This transaction is not exists");
                return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
            } else {
                result.setMessages(fetchMessageByUETR);
                result.setMessage("Searched Transaction details by uetr found");
                return new ResponseEntity<>(result, HttpStatus.FOUND);
            }
        } catch (Exception e) {
            responseData();
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ResponseData> fetchAllDataFromMongoDB() {
        try {
            findTransactionDetails = messageRepository.findAll();
            if (findTransactionDetails.isEmpty()) {
                responseData();
                result.setMessage("The database is empty. No Transactions Found");
                return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
            }
            result.setMessages(findTransactionDetails);
            result.setMessage("Total Transactions are: " + findTransactionDetails.size());
            return new ResponseEntity<>(result, HttpStatus.FOUND);

        } catch (Exception e) {
            responseData();
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<ResponseData> sendMT199ToKafka(String uetr) {
        try {
            findTransactionDetails = messageRepository.findByUniqueEndToEndTransactionReferenceNumber(uetr);
            constructMT199Message(findTransactionDetails);
            int documentCountByUETR = getDocumentCountBYUETR(uetr);
            if (documentCountByUETR == 1) {
                responseData();
                result.setMessage("MT199 Notification of this " + constructMT199.get(3) + " is availble in DB");
                return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
            }

            String mt199json = new Gson().toJson(constructMT199);
            produceToKafka(mt199json); //calling Kafka Producer
            result.setNotification(constructMT199);
            result.setMessages(null);
            result.setMessage("Successfully constructs MT199 of this " + constructMT199.get(3) + "and is published to the kafka Topic " + kafkaTopicName);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            responseData();
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
    }

    //get details by UETR
    public int getDocumentCountBYUETR(String uetr) {
        List<SwiftMessageMT199> fetchMessageByUETR = notificationRepository.findByUniqueEndToEndTransactionReferenceNumber(uetr);
        return fetchMessageByUETR.size();
    }

    //Need to create a Transaction Reference Number from sender details
    public void constructMT199Message(List<SwiftMessageMT103> mt103Records) {
        logger.info("Total Records for construction {} " , mt103Records.size());
        constructMT199.clear();
        String senderBank = mt103Records.get(0).getSenderBank();

        String transactionreferencenumber = transactionNumberGeneration(senderBank);

        constructMT199.add(senderBank);
        constructMT199.add(transactionreferencenumber);
        constructMT199.add(mt103Records.get(0).getSenderReferenceNumber());
        constructMT199.add(mt103Records.get(0).getUniqueEndToEndTransactionReferenceNumber());
        constructMT199.add(mt103Records.get(0).getBankOperationCode());
        constructMT199.add(mt103Records.get(0).getValueDateOrCurrencyOrSettledAmount());
        constructMT199.add(mt103Records.get(0).getPayer());
        constructMT199.add(mt103Records.get(0).getBeneficiary());
        constructMT199.add(mt103Records.get(0).getDetailsOfCharge());
        constructMT199.add(mt103Records.get(0).getExchangeRate());
        constructMT199.add(mt103Records.get(0).getReceiversCharge());
    }

    private void produceToKafka(String inputData) {
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(kafkaTopicName, inputData);
        kafkaTemplate.send(producerRecord);
    }


    private void responseData() {
        result.setMessage("Failed");
        result.setMessages(null);
        result.setNotification(null);
    }

    private String transactionNumberGeneration(String senderBank) {
        String id = String.format("%04d", random.nextInt(10000));
        return senderBank + id;
    }
}
