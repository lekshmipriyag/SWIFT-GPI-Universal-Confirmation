package com.swift.constructmt199message.repository;

import com.swift.constructmt199message.model.SwiftMessageMT103;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<SwiftMessageMT103, String> {

    public List<SwiftMessageMT103> findBySenderReferenceNumber(String trn);

    public List<SwiftMessageMT103> findByUniqueEndToEndTransactionReferenceNumber(String uetr);
}
