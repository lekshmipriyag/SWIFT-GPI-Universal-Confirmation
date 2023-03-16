package com.swift.constructmt199message.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document("mt199notifications")
@Data
@NoArgsConstructor
public class SwiftMessageMT199 {

    @Id
    private String id;

    @Indexed(unique = true)
    private String transactionReferenceNumber;

    private String relatedReference;

    @Indexed(unique = true)
    @Field(name = "UETR")
    private String uniqueEndToEndTransactionReferenceNumber;

    private List<String> narrative;
    private String sender;
    private String receiver;

    public SwiftMessageMT199(String sender,
                             String receiver,
                             String uniqueEndToEndTransactionReferenceNumber,
                             String transactionReferenceNumber,
                             String relatedReference,
                             List<String> narrative) {

        this.sender = sender;
        this.receiver = receiver;
        this.uniqueEndToEndTransactionReferenceNumber = uniqueEndToEndTransactionReferenceNumber;
        this.transactionReferenceNumber = transactionReferenceNumber;
        this.relatedReference = relatedReference;
        this.narrative = narrative;
    }
}
