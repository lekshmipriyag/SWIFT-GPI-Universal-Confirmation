package com.swift.constructmt199message.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document("mt103messages")
@Data
@NoArgsConstructor
public class SwiftMessageMT103 {

    @Id
    private String id;

    @Indexed(unique = true)
    private String senderReferenceNumber;

    @Indexed(unique = true)
    @Field(name = "UETR")
    private String uniqueEndToEndTransactionReferenceNumber;

    private String bankOperationCode;
    private String valueDateOrCurrencyOrSettledAmount;
    private String payer;
    private String beneficiary;
    private String orderingInstitution;
    private String detailsOfCharge;
    private String exchangeRate;
    private String receiversCharge;
    private String senderToReceiverInfo;
    private String senderBank;

    public SwiftMessageMT103(String senderReferenceNumber,
                             String uniqueEndToEndTransactionReferenceNumber,
                             String bankOperationCode,
                             String valueDateOrCurrencyOrSettledAmount,
                             String payer,
                             String beneficiary,
                             String orderingInstitution,
                             String detailsOfCharge,
                             String exchangeRate,
                             String receiversCharge,
                             String senderToReceiverInfo,
                             String senderBank) {

        this.senderReferenceNumber = senderReferenceNumber;
        this.uniqueEndToEndTransactionReferenceNumber = uniqueEndToEndTransactionReferenceNumber;
        this.bankOperationCode = bankOperationCode;
        this.valueDateOrCurrencyOrSettledAmount = valueDateOrCurrencyOrSettledAmount;
        this.payer = payer;
        this.beneficiary = beneficiary;
        this.orderingInstitution = orderingInstitution;
        this.detailsOfCharge = detailsOfCharge;
        this.exchangeRate = exchangeRate;
        this.receiversCharge = receiversCharge;
        this.senderToReceiverInfo = senderToReceiverInfo;
        this.senderBank = senderBank;
    }
}
