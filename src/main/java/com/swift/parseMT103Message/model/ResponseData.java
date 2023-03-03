package com.swift.parseMT103Message.model;

import lombok.Data;

import java.util.List;

@Data
public class ResponseData {
    private String message;
    private List<SwiftMessageMT103> messages;
}
