package com.swift.constructmt199message.repository;

import com.swift.constructmt199message.model.SwiftMessageMT199;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<SwiftMessageMT199, String> {

    public List<SwiftMessageMT199> findByUniqueEndToEndTransactionReferenceNumber(String uetr);

}
