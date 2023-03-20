package com.swift.updategpitracker.repository;

import com.swift.updategpitracker.model.SwiftMessageMT199;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<SwiftMessageMT199, String>{

    public List<SwiftMessageMT199> findByTransactionReferenceNumber(String trn);
    public List<SwiftMessageMT199> findByUniqueEndToEndTransactionReferenceNumber(String uetr);

}
