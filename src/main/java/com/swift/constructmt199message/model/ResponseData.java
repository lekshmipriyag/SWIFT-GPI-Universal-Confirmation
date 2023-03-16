package com.swift.constructmt199message.model;

import lombok.Data;

import java.util.List;

@Data
public class ResponseData {
    private String message;
    private List<SwiftMessageMT103> messages;
    private List<String> notification;

}
