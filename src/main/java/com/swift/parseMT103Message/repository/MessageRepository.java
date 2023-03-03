package com.swift.parseMT103Message.repository;

import com.swift.parseMT103Message.model.SwiftMessageMT103;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<SwiftMessageMT103, String> {

    //    @Query("{transactionReferenceNumber: '?0'}")
    public List<SwiftMessageMT103> findBySenderReferenceNumber(String TRN);
    public List<SwiftMessageMT103> findByUniqueEndToEndTransactionReferenceNumber(String UETR);

}
